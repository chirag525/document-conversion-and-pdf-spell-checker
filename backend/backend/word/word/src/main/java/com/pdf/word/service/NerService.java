package com.pdf.word.service;

import jakarta.annotation.PostConstruct;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Service
public class NerService {

    private NameFinderME personFinder;
    private NameFinderME orgFinder;
    private NameFinderME locationFinder;

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    @PostConstruct
    public void init() {
        try {
            personFinder = loadModel("models/en-ner-person.bin");
            orgFinder = loadModel("models/en-ner-organization.bin");
            locationFinder = loadModel("models/en-ner-location.bin");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load NER models", e);
        }
    }

    private NameFinderME loadModel(String modelPath) throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(modelPath);

        if (inputStream == null) {
            throw new RuntimeException("Model file not found: " + modelPath);
        }

        TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
        return new NameFinderME(model);
    }

    public Set<String> extractNamedEntities(String text) {

        Set<String> entities = new HashSet<>();

        if (text == null || text.isBlank()) {
            return entities;
        }

        String[] tokens = tokenizer.tokenize(text);

        extract(personFinder, tokens, entities);
        extract(orgFinder, tokens, entities);
        extract(locationFinder, tokens, entities);

        return entities;
    }

    private void extract(NameFinderME finder, String[] tokens, Set<String> entities) {

        if (finder == null || tokens.length == 0) return;

        Span[] spans = finder.find(tokens);

        for (Span span : spans) {
            StringBuilder entity = new StringBuilder();
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                entity.append(tokens[i]).append(" ");
            }
            entities.add(entity.toString().trim());
        }

        finder.clearAdaptiveData();
    }
}
