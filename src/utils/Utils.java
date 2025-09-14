package utils;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utils {
    private static final Scanner sc = new Scanner(System.in);
    public static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter MY = DateTimeFormatter.ofPattern("MM/yyyy");

    public static int getAnInteger(String msg, int min, int max, boolean allowEmpty) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (s.isEmpty() && allowEmpty) return -1;
            try {
                int n = Integer.parseInt(s);
                if (n < min || n > max) {
                    System.out.printf("Enter a number in [%d..%d]%n", min, max);
                } else return n;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer.");
            }
        }
    }

    public static String getString(String msg, String regex, String err) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (regex == null || s.matches(regex)) return s;
            System.out.println(err);
        }
    }

    public static LocalDate getDate(String msg, boolean futureOnly) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                LocalDate d = LocalDate.parse(s, DMY);
                if (futureOnly && !d.isAfter(LocalDate.now())) {
                    System.out.println("Date must be in the future.");
                } else return d;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use dd/MM/yyyy.");
            }
        }
    }

    public static boolean confirm(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim().toUpperCase();
            if (s.equals("Y")) return true;
            if (s.equals("N")) return false;
            System.out.println("Please enter Y/N.");
        }
    }
}