package com.apporiented.algorithm.clustering;

public class Utils {
    static public void printVector(String title, int header, double[] vector) {
        StringBuilder finalRow = new StringBuilder();
        finalRow.append(String.format("%15s", ""));

        for (double num : vector) {
            finalRow.append(String.format("%15f", num));
        }
        System.out.println(finalRow);
    }

    public static StringBuilder printStrings(String[] stringList) {
        StringBuilder finalString = new StringBuilder();
        finalString.append(String.format("%25s", ""));
        for (String str : stringList) {
            str = String.format("%25s", str);
            finalString.append(str);
        }
        System.out.println(finalString);
        return finalString.append(String.format("%25s", "")+"\n");
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static StringBuilder printMatrix(String[] files, double[][] matrix) {
        StringBuilder finalMatrix = new StringBuilder();
        StringBuilder finalRow;

        for (int i = 0; i < files.length; i++) {
            finalRow = new StringBuilder();
            finalRow.append(String.format("%25s", files[i]));
            for (int j = 0; j < matrix[i].length; j++) {
                finalRow.append(String.format("%25.2f", matrix[i][j]));
            }
            System.out.println(finalRow);
            finalMatrix.append(finalRow).append("\n");
        }
        return finalMatrix;
    }

    public static void printMatrix(String title, double[][] matrix) {
        StringBuilder finalRow;
        System.out.println(title);

        for (int i = 0; i < matrix.length; i++) {
            finalRow = new StringBuilder();
            for (int j = 0; j < matrix[i].length; j++) {
                finalRow.append(String.format("%25.2f", matrix[i][j]));
            }
            System.out.println(finalRow);
        }
    }

}
