package com.pdf.word.service;

import com.pdf.word.exception.FileProcessingException;
import com.pdf.word.model.SpellCheckResponse;
import com.pdf.word.model.SpellError;
import com.pdf.word.util.LineNumberUtil;
import lombok.RequiredArgsConstructor;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SpellCheckService {

    private final JLanguageTool languageTool;
    private final NerService nerService;   // ✅ Inject NER

    public SpellCheckResponse checkSpelling(String text, String fileName) {

        if (text == null || text.trim().isEmpty()) {
            throw new FileProcessingException("No text available for spell checking");
        }

        try {

            // ✅ Extract Named Entities (Persons, Orgs, Locations)
            Set<String> namedEntities = nerService.extractNamedEntities(text);

            List<RuleMatch> matches = languageTool.check(text);
            List<SpellError> errors = new ArrayList<>();
            Set<String> uniqueErrors = new HashSet<>();

            for (RuleMatch match : matches) {

                String ruleId = match.getRule().getId();

                // ✅ Only process spelling-related rules
                if (!(ruleId.contains("SPELL")
                        || ruleId.contains("MORFOLOGIK")
                        || ruleId.contains("HUNSPELL"))) {
                    continue;
                }

                int start = match.getFromPos();
                int end = match.getToPos();

                if (start < 0 || end > text.length() || start >= end) {
                    continue;
                }

                String incorrectWord = text.substring(start, end).trim();

                if (incorrectWord.isBlank()) continue;

                // ✅ Skip named entities (Chirag, Google, Mangalore etc.)
                if (namedEntities.contains(incorrectWord)) continue;

                // ✅ Skip ALL CAPS words (PDF, API, REST etc.)
                if (incorrectWord.equals(incorrectWord.toUpperCase())
                        && incorrectWord.length() > 2) continue;

                // ✅ Skip invalid tokens (numbers, symbols)
                if (!incorrectWord.matches("^[a-zA-Z-]+$")) continue;

                // ✅ Skip if no suggestions
                if (match.getSuggestedReplacements() == null
                        || match.getSuggestedReplacements().isEmpty()) continue;

                int lineNumber = LineNumberUtil.getLineNumber(text, start);
                String uniqueKey = incorrectWord.toLowerCase() + "_" + lineNumber;

                // ✅ Avoid duplicate errors
                if (!uniqueErrors.add(uniqueKey)) continue;

                errors.add(new SpellError(
                        incorrectWord,
                        match.getSuggestedReplacements(),
                        lineNumber
                ));
            }

            return new SpellCheckResponse(
                    fileName,
                    errors.size(),
                    errors
            );

        } catch (Exception e) {
            throw new FileProcessingException("Spell checking failed: " + e.getMessage());
        }
    }
}
