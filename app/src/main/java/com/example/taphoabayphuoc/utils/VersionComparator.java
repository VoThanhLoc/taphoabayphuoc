package com.example.taphoabayphuoc.utils;

public class VersionComparator {

    public static boolean hasNewVersion(String current, String latest) {
        if (current == null || latest == null) return false;

        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");

        int max = Math.max(currentParts.length, latestParts.length);

        for (int i = 0; i < max; i++) {
            int currentValue = 0;
            if (i < currentParts.length) {
                currentValue = parsePart(currentParts[i]);
            }

            int latestValue = 0;
            if (i < latestParts.length) {
                latestValue = parsePart(latestParts[i]);
            }

            if (latestValue > currentValue) {
                return true;
            }

            if (latestValue < currentValue) {
                return false;
            }
        }

        return false;
    }

    private static int parsePart(String part) {
        try {
            // Remove any non-numeric characters (e.g., "1-beta" -> "1")
            String numericPart = part.replaceAll("[^0-9]", "");
            if (numericPart.isEmpty()) return 0;
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}