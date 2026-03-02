package org.example;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MatrixUtils {
    private static final double EPSILON = 1e-15;
    protected static final NumberFormat FORMATTER = new DecimalFormat("#0.0000");

    public static boolean isPotentiallySingular(double[][] matrix, int size) {
        for (int i = 0; i < size; i++) {
            boolean zeroRow = true;
            boolean zeroCol = true;
            for (int j = 0; j < size; j++) {
                if (Math.abs(matrix[i][j]) > EPSILON) zeroRow = false;
                if (Math.abs(matrix[j][i]) > EPSILON) zeroCol = false;
            }
            if (zeroRow || zeroCol) return true;
        }
        return false;
    }


    public static void printMatrix(String title, double[][] matrix, int size) {
        System.out.println("\n" + title);
        for (int i = 0; i < size; i++) {
            System.out.print("| ");
            for (int j = 0; j < size; j++) {
                System.out.printf("%8s ", FORMATTER.format(matrix[i][j]));
            }
            System.out.printf("| %8s |%n", FORMATTER.format(matrix[i][size]));
        }
    }


}