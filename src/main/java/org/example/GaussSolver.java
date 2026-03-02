package org.example;

import org.apache.commons.math3.linear.*;

import static org.example.MatrixUtils.FORMATTER;


public class GaussSolver {
    private static final double EPSILON = 1e-15;

    private final double[][] matrixA;
    private final double[] vectorB;
    private final int size;

    private double[][] augmentedMatrix;
    private double[] solution;
    private double determinant;
    private double[] residual;

    private int swapCnt;

    public GaussSolver(double[][] matrixA, double[] vectorB, int size) {
        this.matrixA = matrixA;
        this.vectorB = vectorB;
        this.size = size;
        initializeFields();
    }

    public GaussSolver(InputData data) {
        this(data.getMatrix(), data.getVector(), data.getSize());
    }


    private void initializeFields() {
        solution = new double[size];
        residual = new double[size];
        swapCnt = 0;
    }

    public void solve() {
        createAugmentedMatrix();
        MatrixUtils.printMatrix("Расширенная матрица [A|B]:", augmentedMatrix, size);

        if (!doIt()) {
            System.out.println("Система не имеет единственного решения");
            return;
        }

        MatrixUtils.printMatrix("Треугольная матрица после прямого хода:", augmentedMatrix, size);

        solveBackwards();
        printSolution();

        findDet();
        System.out.printf(" Определитель матрицы A: %s%n", FORMATTER.format(determinant));

        findResidual();
        printResidual();

        compareWithLibrary();
    }

    private void createAugmentedMatrix() {
        augmentedMatrix = new double[size][size + 1];
        for (int i = 0; i < size; i++) {
            System.arraycopy(matrixA[i], 0, augmentedMatrix[i], 0, size);
            augmentedMatrix[i][size] = vectorB[i];
        }
    }

    private boolean doIt() {
        for (int col = 0; col < size; col++) {
            int bestRow = maxRow(col);

            if (Math.abs(augmentedMatrix[bestRow][col]) < EPSILON) {
                System.out.println("Обнаружена потенциально вырожденная матрица");
                return false;
            }
            if (bestRow != col) {
                swapRows(col, bestRow);
            }
            zeroBelow(col);
        }
        return true;
    }

    private int maxRow(int col) {
        int best = col;
        double maxValue = Math.abs(augmentedMatrix[col][col]);

        for (int i = col + 1; i < size; i++) {
            double currentValue = Math.abs(augmentedMatrix[i][col]);
            if (currentValue > maxValue) {
                maxValue = currentValue;
                best = i;
            }
        }
        if (maxValue < EPSILON) {
            System.out.printf(" Столбец %d содержит только близкие к нулю элементы%n", col + 1);
        }
        return best;
    }

    private void swapRows(int a, int b) {
        double[] temp = augmentedMatrix[a];
        augmentedMatrix[a] = augmentedMatrix[b];
        augmentedMatrix[b] = temp;
        swapCnt++;
    }

    private void zeroBelow(int col) {
        double mainVal = augmentedMatrix[col][col];

        for (int row = col + 1; row < size; row++) {
            double koef = augmentedMatrix[row][col] / mainVal;

            for (int j = col; j <= size; j++) {
                augmentedMatrix[row][j] -= koef * augmentedMatrix[col][j];
            }
        }
    }

    private void solveBackwards() {
        for (int i = size - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < size; j++) {
                sum += augmentedMatrix[i][j] * solution[j];
            }
            solution[i] = (augmentedMatrix[i][size] - sum) / augmentedMatrix[i][i];
        }
    }

    private void findDet() {
        determinant = 1.0;
        for (int i = 0; i < size; i++) {
            determinant *= augmentedMatrix[i][i];
        }
        if (swapCnt % 2 != 0) {
            determinant = -determinant;
        }
    }

    private void findResidual() {
        for (int i = 0; i < size; i++) {
            double sum = 0.0;
            for (int j = 0; j < size; j++) {
                sum += matrixA[i][j] * solution[j];
            }
            residual[i] = sum - vectorB[i];
        }
    }

    private void compareWithLibrary() {
        try {
            RealMatrix mA = new Array2DRowRealMatrix(matrixA);
            RealVector vB = new ArrayRealVector(vectorB);

            LUDecomposition luDecomposition = new LUDecomposition(mA);
            DecompositionSolver solverLib = luDecomposition.getSolver();
            RealVector xLib = solverLib.solve(vB);
            double detLib = luDecomposition.getDeterminant();

            System.out.println("\n РЕШЕНИЕ БИБЛИОТЕЧНЫМ МЕТОДОМ (Apache Commons Math):");
            for (int i = 0; i < size; i++) {
                System.out.printf("  x%d_lib = %.6f%n", i + 1, xLib.getEntry(i));
            }
            System.out.printf("  det_lib(A) = %.6f%n", detLib);

            System.out.println("\n СРАВНЕНИЕ РЕЗУЛЬТАТОВ:");
            double maxDiff = 0.0;
            for (int i = 0; i < size; i++) {
                double diff = Math.abs(solution[i] - xLib.getEntry(i));
                maxDiff = Math.max(maxDiff, diff);
                System.out.printf("  |x%d - x%d_lib| = %.2e%n", i + 1, i + 1, diff);
            }

            double detDiff = Math.abs(determinant - detLib);
            System.out.printf("  |det - det_lib| = %.2e%n", detDiff);
            System.out.printf("  Максимальная разница в решении: %.2e%n", maxDiff);

            if (maxDiff < 1e-10 && detDiff < 1e-10) {
                System.out.println(" Результаты совпадают с высокой точностью");
            }

        } catch (Exception e) {
            System.out.println(" Ошибка при вычислении библиотечным методом: " + e.getMessage());
        }
    }

    private void printSolution() {
        System.out.println("\n РЕШЕНИЕ:");
        for (int i = 0; i < size; i++) {
            System.out.printf("  x[%d] = %s%n", i + 1, FORMATTER.format(solution[i]));
        }
    }

    private void printResidual() {
        System.out.println("\n ВЕКТОР НЕВЯЗКИ (r = A·x - b):");
        double maxResidual = 0.0;
        for (int i = 0; i < size; i++) {
            System.out.printf("  r[%d] = %s%n", i + 1, FORMATTER.format(residual[i]));
            maxResidual = Math.max(maxResidual, Math.abs(residual[i]));
        }
        System.out.printf("  Максимальная невязка: %s%n", FORMATTER.format(maxResidual));
    }
}