import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/spell_response.dart';

class ApiService {

  static const String baseUrl = "http://localhost:8080";

  // SPELL CHECK
  Future<SpellResponse> spellCheck(File file) async {
    try {
      var request = http.MultipartRequest(
        'POST',
        Uri.parse("$baseUrl/api/spell-check"),
      );

      request.files.add(await http.MultipartFile.fromPath("file", file.path));

      var response = await request.send();

      if (response.statusCode == 200) {
        var data = await response.stream.bytesToString();
        return SpellResponse.fromJson(json.decode(data));
      } else {
        throw Exception("Spell check failed: ${response.statusCode}");
      }
    } catch (e) {
      print("Spell Check Error: $e");
      rethrow;
    }
  }

  // FILE DOWNLOAD 
  Future<String> downloadFile(
    String endpoint,
    File file, {
    Map<String, String>? fields,
  }) async {
    try {
      var request = http.MultipartRequest(
        'POST',
        Uri.parse("$baseUrl$endpoint"),
      );

      request.files.add(await http.MultipartFile.fromPath("file", file.path));

      if (fields != null) {
        request.fields.addAll(fields);
      }

      var response = await request.send();

      if (response.statusCode != 200) {
        throw Exception("Download failed: ${response.statusCode}");
      }

      var bytes = await response.stream.toBytes();


      final downloadsDir = Directory(
        "${Platform.environment['USERPROFILE']}\\Downloads",
      );

      if (!downloadsDir.existsSync()) {
        downloadsDir.createSync(recursive: true);
      }

      // Get file extension from backend header
      String extension = ".pdf";
      final contentDisposition = response.headers['content-disposition'];

      if (contentDisposition != null &&
          contentDisposition.contains("filename=")) {
        final fileName = contentDisposition
            .split("filename=")
            .last
            .replaceAll('"', '');
        extension = fileName.substring(fileName.lastIndexOf("."));
      }

      final filePath =
          "${downloadsDir.path}\\output_${DateTime.now().millisecondsSinceEpoch}$extension";

      File savedFile = File(filePath);
      await savedFile.writeAsBytes(bytes);

      print("File saved at: $filePath");

      return filePath;
    } catch (e) {
      print("Download Error: $e");
      rethrow;
    }
  }
}
