import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'convert_page.dart';
import 'spell_page.dart';

class HomePage extends StatelessWidget {
  Widget buildCard({
    required String title,
    required String subtitle,
    required IconData icon,
    required VoidCallback onTap,
  }) {
    return Card(
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: onTap,
        child: Padding(
          padding: EdgeInsets.all(20),
          child: Row(
            children: [
              CircleAvatar(radius: 28, child: Icon(icon, size: 28)),
              SizedBox(width: 20),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(subtitle, style: TextStyle(color: Colors.grey[600])),
                  ],
                ),
              ),
              Icon(Icons.arrow_forward_ios, size: 18),
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Document Processing Suite"),
        centerTitle: true,
      ),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            buildCard(
              title: "Document Conversions",
              subtitle: "Convert Word, PDF, PPT, Excel & more",
              icon: Icons.swap_horiz,
              onTap: () => Get.to(() => ConvertPage()),
            ),
            SizedBox(height: 20),
            buildCard(
              title: "PDF Spell Check",
              subtitle: "Find spelling errors in PDF files",
              icon: Icons.spellcheck,
              onTap: () => Get.to(() => SpellPage()),
            ),
          ],
        ),
      ),
    );
  }
}
