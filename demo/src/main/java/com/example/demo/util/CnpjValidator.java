package com.example.demo.util;

public class CnpjValidator {

    public static boolean isValid(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        cnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifica se tem 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }

        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            int[] pesosPrimeiro = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
            }
            int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            // Verifica o primeiro dígito
            if (Character.getNumericValue(cnpj.charAt(12)) != primeiroDigito) {
                return false;
            }

            int[] pesosSegundo = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
            }
            int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            return Character.getNumericValue(cnpj.charAt(13)) == segundoDigito;

        } catch (Exception e) {
            return false;
        }
    }

    public static String format(String cnpj) {
        if (cnpj == null) {
            return null;
        }

        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14) {
            return cnpj;
        }

        return cnpj.substring(0, 2) + "." +
                cnpj.substring(2, 5) + "." +
                cnpj.substring(5, 8) + "/" +
                cnpj.substring(8, 12) + "-" +
                cnpj.substring(12, 14);
    }

    public static String removeFormat(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        return cnpj.replaceAll("[^0-9]", "");
    }
}
