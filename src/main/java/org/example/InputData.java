package org.example;

public class InputData {
    private final int size;
    private final double[][] matrix;
    private final double[] vector;

    public InputData(int size, double[][] matrix, double[] vector) {
        this.size = size;
        this.matrix = matrix;
        this.vector = vector;
    }

    public int getSize() {
        return size;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public double[] getVector() {
        return vector;
    }
}
