import 'dart:io';
import 'package:get/get.dart';
import 'package:file_picker/file_picker.dart';
import '../models/spell_response.dart';
import '../services/api_service.dart';

class SpellController extends GetxController {
  var isLoading = false.obs;
  var response = Rxn<SpellResponse>();

  final ApiService _apiService = ApiService();

  Future<void> pickAndSpellCheck() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['pdf'],
    );

    if (result != null) {
      File file = File(result.files.single.path!);

      try {
        isLoading.value = true;
        response.value = await _apiService.spellCheck(file);
      } catch (e) {
        Get.snackbar("Error", e.toString());
      } finally {
        isLoading.value = false;
      }
    }
  }
}
