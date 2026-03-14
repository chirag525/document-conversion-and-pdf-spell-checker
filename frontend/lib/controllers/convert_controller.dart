import 'dart:io';
import 'package:get/get.dart';
import 'package:file_picker/file_picker.dart';
import '../services/api_service.dart';

class ConvertController extends GetxController {
  var isLoading = false.obs;
  var resultPath = "".obs;

  final ApiService _apiService = ApiService();

  Future<void> pickAndConvert(
    String endpoint, {
    Map<String, String>? fields,
  }) async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      allowMultiple: false,
    );

    if (result != null) {
      File file = File(result.files.single.path!);

      try {
        isLoading.value = true;

        String path = await _apiService.downloadFile(
          endpoint,
          file,
          fields: fields,
        );

        resultPath.value = path;
        Get.snackbar("Success", "File saved at:\n$path");
      } catch (e) {
        Get.snackbar("Error", e.toString());
      } finally {
        isLoading.value = false;
      }
    }
  }
}
