package org.example;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        runSolver(scanner);
        scanner.close();
    }

    private static void runSolver(Scanner scanner) {
        InputHandler inputHandler = new InputHandler(scanner);
        InputData inputData = inputHandler.getInputData();
        if (inputData == null) {
            System.out.println("Не удалось получить входные данные. Программа завершена.");
            return;
        }
        System.out.println("\nРЕШЕНИЕ СИСТЕМЫ ЛИНЕЙНЫХ УРАВНЕНИЙ МЕТОДОМ ГАУССА");

        if (MatrixUtils.isPotentiallySingular(inputData.getMatrix(), inputData.getSize())) {
            System.out.println("\nВНИМАНИЕ: Матрица может быть вырожденной или плохо обусловленной!");
        }

        GaussSolver solver = new GaussSolver(inputData);
        solver.solve();
    }
}