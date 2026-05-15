package com.trainingmug.ecommerce.ui;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static byte getByte(String message) {
        System.out.print(message);
        return Byte.parseByte(scanner.nextLine());
    }
    public static int getInt(String message) {
        System.out.print(message);
        return Integer.parseInt(scanner.nextLine());
    }

    public static String getString(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public static boolean getBoolean(String message) {
        System.out.print(message);
        return Boolean.parseBoolean(scanner.nextLine());
    }

    public static float getFloat(String message) {
        System.out.print(message);
        return Float.parseFloat(scanner.nextLine());
    }
}
