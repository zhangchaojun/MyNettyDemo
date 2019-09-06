package com.example.mynettydemo.scanner;

import java.util.Scanner;

public class ScanTest1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                String str1 = scanner.next();
                System.out.println("Input:" + str1);
            }
        }

//        scanner.close();
    }
}