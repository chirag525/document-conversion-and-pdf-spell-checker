package com.pdf.word.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SpellError {

    private String incorrectWord;
    private List<String> suggestions;
    private int lineNumber;
}
