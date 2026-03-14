package com.pdf.word.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SpellCheckResponse {

    private String fileName;
    private int totalErrors;
    private List<SpellError> errors;
}
