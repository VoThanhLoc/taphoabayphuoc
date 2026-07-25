package com.example.taphoabayphuoc.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Random;

public class BarcodeGenerator {

    private static final Random random = new Random();

    public static String generate() {
        StringBuilder builder = new StringBuilder();
        builder.append("893");
        for (int i = 0; i < 9; i++) {
            builder.append(random.nextInt(10));
        }
        String first12 = builder.toString();
        int checkDigit = calculateCheckDigit(first12);
        return first12 + checkDigit;
    }

    private static int calculateCheckDigit(String code12) {
        int sum = 0;
        for (int i = 0; i < code12.length(); i++) {
            int digit = Character.getNumericValue(code12.charAt(i));
            if (i % 2 == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }
        return (10 - (sum % 10)) % 10;
    }

    public static Bitmap createBarcodeBitmap(String data, int width, int height) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(data, BarcodeFormat.EAN_13, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
