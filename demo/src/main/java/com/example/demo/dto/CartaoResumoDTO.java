package com.example.demo.dto;

import com.example.demo.model.BandeiraCartao;
import com.example.demo.model.Cartao;

public record CartaoResumoDTO(
        Integer id,
        String numeroMascarado,
        BandeiraCartao bandeira,
        Boolean isPrincipal,
        String apelido,
        Boolean isVencido
) {
    public CartaoResumoDTO(Cartao cartao) {
        this(
                cartao.getId(),
                cartao.getNumeroMascarado(),
                cartao.getBandeira(),
                cartao.getIsPrincipal(),
                cartao.getApelido(),
                cartao.isVencido()
        );
    }
}
