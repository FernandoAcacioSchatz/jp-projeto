package com.example.demo.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    
    private String nomeProduto;
    private Integer estoqueDisponivel;
    private Integer quantidadeSolicitada;
    
    public EstoqueInsuficienteException(String nomeProduto, Integer estoqueDisponivel, Integer quantidadeSolicitada) {
        super(String.format("Estoque insuficiente para o produto '%s'. Disponível: %d, Solicitado: %d", 
                nomeProduto, estoqueDisponivel, quantidadeSolicitada));
        this.nomeProduto = nomeProduto;
        this.estoqueDisponivel = estoqueDisponivel;
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getEstoqueDisponivel() {
        return estoqueDisponivel;
    }

    public Integer getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }
}
