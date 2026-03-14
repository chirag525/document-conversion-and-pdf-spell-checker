package com.pdf.word.config;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LanguageToolConfig {

    @Bean
    public JLanguageTool languageTool() {
        return new JLanguageTool(new BritishEnglish());
    }
}
