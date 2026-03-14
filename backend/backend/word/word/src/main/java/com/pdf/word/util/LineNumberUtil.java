package com.pdf.word.util;

public class LineNumberUtil {

    public static int getLineNumber(String text, int position) {

        String beforeText = text.substring(0, position);
        return beforeText.split("\r\n|\r|\n").length;
    }
}
