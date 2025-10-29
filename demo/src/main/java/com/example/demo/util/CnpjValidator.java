package com.example.demo.util;

public class CnpjValidator {

    /**
     * Valida CNPJ verificando formato e dígitos verificadores
     * 
     * @param cnpj String contendo o CNPJ (pode ter pontos, barra e traço ou não)
     * @return true se o CNPJ for válido, false caso contrário
     */
    public static boolean isValid(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifica se tem 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 00.000.000/0000-00)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            // Calcula o primeiro dígito verificador
            int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
            }
            int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            // Verifica o primeiro dígito
            if (Character.getNumericValue(cnpj.charAt(12)) != primeiroDigito) {
                return false;
            }

            // Calcula o segundo dígito verificador
            int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
            }
            int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            // Verifica o segundo dígito
            return Character.getNumericValue(cnpj.charAt(13)) == segundoDigito;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formata o CNPJ para o padrão XX.XXX.XXX/XXXX-XX
     * 
     * @param cnpj String contendo apenas números
     * @return CNPJ formatado
     */
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

    /**
     * Remove a formatação do CNPJ deixando apenas os números
     * 
     * @param cnpj CNPJ formatado ou não
     * @return CNPJ apenas com números
     */
    public static String removeFormat(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        return cnpj.replaceAll("[^0-9]", "");
    }
}
