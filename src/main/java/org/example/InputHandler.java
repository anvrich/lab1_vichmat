package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHandler {
    private static final int MAX_SIZE = 20;
    private final Scanner scanner;
    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public InputData getInputData() {
        printWelcomeMsg();
        int choice = getChoiceFromUser();

        return switch (choice) {
            case 1 -> readFromKeyboard();
            case 2 -> readFromFile();
            default -> {
                System.out.println("Неверный выбор. Пожалуйста, выберите 1 или 2");
                yield null;
            }
        };
    }

    private void printWelcomeMsg() {
        System.out.println("=".repeat(70));
        System.out.println("   РЕШЕНИЕ СИСТЕМ ЛИНЕЙНЫХ УРАВНЕНИЙ МЕТОДОМ ГАУССА");
        System.out.println("=".repeat(70));
        System.out.println("Выберите способ ввода данных:");
        System.out.println("  1 - Ввести матрицу с консоли");
        System.out.println("  2 - Ввести матрицу с файла");
    }

    private int getChoiceFromUser() {
        while (true) {
            try {
                System.out.print("\nВаш выбор (1 или 2): ");
                int choice = scanner.nextInt();
                if (choice == 1 || choice == 2) {
                    return choice;
                }
                System.out.println("Ошибка: введите 1 или 2");
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите число (1 или 2)");
                scanner.next();
            }
        }
    }

    private InputData readFromKeyboard() {
        int size = getMatrixSize();

        double[][] matrix = new double[size][size];
        double[] vector = new double[size];

        readMatrixFromKeyboard(matrix, size);
        readVectorFromKeyboard(vector, size);

        return new InputData(size, matrix, vector);
    }

    private int getMatrixSize() {
        while (true) {
            try {
                System.out.printf("Введите размерность матрицы (1-%d): ", MAX_SIZE);
                int size = scanner.nextInt();
                if (size >= 1 && size <= MAX_SIZE) {
                    return size;
                }
                System.out.printf("Размер должен быть от 1 до %d%n", MAX_SIZE);
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите целое число");
                scanner.next();
            }
        }
    }
    private void readMatrixFromKeyboard(double[][] matrix, int size) {
        System.out.println("Введите коэффициенты матрицы A построчно:");
        for (int i = 0; i < size; i++) {
            System.out.printf("Строка %d (введите %d чисел через пробел): ", i + 1, size);
            for (int j = 0; j < size; j++) {
                matrix[i][j] = readDoubleSafely();
            }
        }
    }
    private void readVectorFromKeyboard(double[] vector, int size) {
        System.out.println("Введите вектор правой части B:");
        for (int i = 0; i < size; i++) {
            System.out.printf("B[%d]: ", i + 1);
            vector[i] = readDoubleSafely();
        }
    }

    private double readDoubleSafely() {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.print("Ошибка: введите число. Повторите: ");
                scanner.next();
            }
        }
    }

    private InputData readFromFile() {
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.next();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            if (!fileScanner.hasNextInt()) {
                throw new InputMismatchException("Файл не содержит размерности матрицы");
            }
            int size = fileScanner.nextInt();

            if (size < 1 || size > MAX_SIZE) {
                System.out.printf("Размер должен быть от 1 до %d%n", MAX_SIZE);
                return null;
            }

            double[][] matrix = new double[size][size];
            double[] vector = new double[size];

            System.out.printf("Чтение матрицы %dx%d\n", size, size);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (!fileScanner.hasNextDouble()) {
                        throw new InputMismatchException(
                                String.format("Недостаточно данных в файле (ошибка в позиции [%d,%d])", i + 1, j + 1)
                        );
                    }
                    matrix[i][j] = fileScanner.nextDouble();
                }
            }

            for (int i = 0; i < size; i++) {
                if (!fileScanner.hasNextDouble()) {
                    throw new InputMismatchException(
                            String.format("Недостаточно данных в файле (ошибка в B[%d])", i + 1)
                    );
                }
                vector[i] = fileScanner.nextDouble();
            }
            return new InputData(size, matrix, vector);

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + filePath);
            return null;
        } catch (InputMismatchException e) {
            System.out.println("Ошибка формата данных в файле:");
            System.out.println("   " + e.getMessage());
            System.out.println("   Убедитесь, что файл содержит числа в правильном формате");
            return null;
        } catch (Exception e) {
            System.out.println("Непредвиденная ошибка при чтении файла:");
            System.out.println("   " + e.getMessage());
            return null;
        }
    }
}