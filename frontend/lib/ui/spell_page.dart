import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../controllers/spell_controller.dart';

class SpellPage extends StatelessWidget {
  final controller = Get.put(SpellController());

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("PDF Spell Check")),
      body: Stack(
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: Column(
              children: [
                ElevatedButton.icon(
                  onPressed: controller.pickAndSpellCheck,
                  icon: Icon(Icons.upload_file),
                  label: Text("Upload PDF"),
                ),
                SizedBox(height: 20),

                Expanded(
                  child: Obx(() {
                    if (controller.response.value == null) {
                      return Center(
                        child: Text(
                          "Upload a PDF to check spelling",
                          style: TextStyle(color: Colors.grey),
                        ),
                      );
                    }

                    final data = controller.response.value!;

                    return Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          padding: EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color:
                                data.totalErrors == 0
                                    ? Colors.green[100]
                                    : Colors.red[100],
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            "Total Errors: ${data.totalErrors}",
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                              color:
                                  data.totalErrors == 0
                                      ? Colors.green
                                      : Colors.red,
                            ),
                          ),
                        ),
                        SizedBox(height: 12),

                        Expanded(
                          child: ListView.builder(
                            itemCount: data.errors.length,
                            itemBuilder: (_, index) {
                              final error = data.errors[index];

                              return Card(
                                child: ListTile(
                                  leading: Icon(Icons.error, color: Colors.red),
                                  title: Text(error.incorrectWord),
                                  subtitle: Text(
                                    "Line: ${error.lineNumber}\nSuggestions: ${error.suggestions.join(", ")}",
                                  ),
                                ),
                              );
                            },
                          ),
                        ),
                      ],
                    );
                  }),
                ),
              ],
            ),
          ),

          Obx(
            () =>
                controller.isLoading.value
                    ? Container(
                      color: Colors.black.withOpacity(0.4),
                      child: Center(child: CircularProgressIndicator()),
                    )
                    : SizedBox(),
          ),
        ],
      ),
    );
  }
}
