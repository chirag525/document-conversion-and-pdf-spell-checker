class SpellError {
  final String incorrectWord;
  final List<dynamic> suggestions;
  final int lineNumber;

  SpellError({
    required this.incorrectWord,
    required this.suggestions,
    required this.lineNumber,
  });

  factory SpellError.fromJson(Map<String, dynamic> json) {
    return SpellError(
      incorrectWord: json['incorrectWord'],
      suggestions: json['suggestions'],
      lineNumber: json['lineNumber'],
    );
  }
}

class SpellResponse {
  final String fileName;
  final int totalErrors;
  final List<SpellError> errors;

  SpellResponse({
    required this.fileName,
    required this.totalErrors,
    required this.errors,
  });

  factory SpellResponse.fromJson(Map<String, dynamic> json) {
    return SpellResponse(
      fileName: json['fileName'],
      totalErrors: json['totalErrors'],
      errors:
          (json['errors'] as List).map((e) => SpellError.fromJson(e)).toList(),
    );
  }
}
