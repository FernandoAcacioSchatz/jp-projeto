package com.example.demo.dto;

import com.example.demo.model.BandeiraCartao;
import com.example.demo.model.Cartao;


public record CartaoResponseDTO(
        Integer id,
        String numeroMascarado,
        BandeiraCartao bandeira,
        String nomeTitular,
        Integer mesValidade,
        Integer anoValidade,
        Boolean isPrincipal,
        String apelido,
        Boolean isVencido,
        String descricaoCompleta
) {
    public CartaoResponseDTO(Cartao cartao) {
        this(
                cartao.getId(),
                cartao.getNumeroMascarado(),
                cartao.getBandeira(),
                cartao.getNomeTitular(),
                cartao.getMesValidade(),
                cartao.getAnoValidade(),
                cartao.getIsPrincipal(),
                cartao.getApelido(),
                cartao.isVencido(),
                cartao.getDescricaoCompleta()
        );
    }
}
