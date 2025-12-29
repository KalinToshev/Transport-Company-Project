package org.informatics.transportcompany;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ConsoleHelper {

    private static final Scanner scanner = new Scanner(System.in);

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public long readLong(String prompt) {
        String line = readLine(prompt).trim();
        try {
            return Long.parseLong(line);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number: " + line);
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        String line = readLine(prompt).trim();
        try {
            return LocalDateTime.parse(line);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid date-time format: " + line);
        }
    }

    public int readInt(String prompt) {
        String line = readLine(prompt).trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer: " + line);
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        String line = readLine(prompt).trim();
        try {
            return new BigDecimal(line);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid decimal: " + line);
        }
    }
}
