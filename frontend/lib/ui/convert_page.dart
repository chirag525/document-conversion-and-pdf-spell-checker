import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../controllers/convert_controller.dart';

class ConvertPage extends StatelessWidget {
  final controller = Get.put(ConvertController());

  Widget buildButton({
    required String text,
    required IconData icon,
    required VoidCallback onPressed,
  }) {
    return Card(
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: onPressed,
        child: Padding(
          padding: EdgeInsets.all(20),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, size: 36),
              SizedBox(height: 12),
              Text(
                text,
                textAlign: TextAlign.center,
                style: TextStyle(fontWeight: FontWeight.w600),
              ),
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Conversions")),
      body: Stack(
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: GridView.count(
              crossAxisCount: 2,
              crossAxisSpacing: 16,
              mainAxisSpacing: 16,
              children: [
                buildButton(
                  text: "Word → PDF",
                  icon: Icons.picture_as_pdf,
                  onPressed:
                      () => controller.pickAndConvert("/convert/word-to-pdf"),
                ),
                buildButton(
                  text: "PDF → Word",
                  icon: Icons.description,
                  onPressed:
                      () => controller.pickAndConvert("/convert/pdf-to-word"),
                ),
                buildButton(
                  text: "PDF → PPT",
                  icon: Icons.slideshow,
                  onPressed:
                      () => controller.pickAndConvert("/convert/pdf-to-ppt"),
                ),
                buildButton(
                  text: "Image → PDF",
                  icon: Icons.image,
                  onPressed:
                      () => controller.pickAndConvert("/convert/image-to-pdf"),
                ),
                buildButton(
                  text: "PDF → Excel",
                  icon: Icons.table_chart,
                  onPressed:
                      () => controller.pickAndConvert("/convert/pdf-to-excel"),
                ),
                buildButton(
                  text: "Rotate PDF",
                  icon: Icons.rotate_right,
                  onPressed:
                      () => controller.pickAndConvert(
                        "/convert/rotate-pdf",
                        fields: {"angle": "90"},
                      ),
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
