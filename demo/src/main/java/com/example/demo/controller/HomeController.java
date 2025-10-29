package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para a página inicial (raiz) da API
 */
@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * Endpoint público da raiz da API
     * Retorna informações sobre a API e endpoints disponíveis
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("nome", "API REST - Sistema de Vendas");
        response.put("versao", "1.0.0");
        response.put("status", "Online");
        
        Map<String, String> endpointsPublicos = new HashMap<>();
        endpointsPublicos.put("Criar Cliente", "POST /cliente");
        endpointsPublicos.put("Criar Fornecedor", "POST /fornecedor");
        endpointsPublicos.put("Listar Produtos", "GET /produto");
        endpointsPublicos.put("Ver Produto", "GET /produto/{id}");
        endpointsPublicos.put("Listar Produtos Paginado", "GET /produto/paginado");
        endpointsPublicos.put("Buscar Produto por Nome", "GET /produto/buscar?nome=texto");
        endpointsPublicos.put("Buscar Produto por Nome Paginado", "GET /produto/buscar/paginado?nome=texto&page=0");
        
        response.put("endpoints_publicos", endpointsPublicos);
        
        Map<String, String> endpointsProtegidos = new HashMap<>();
        endpointsProtegidos.put("Listar Clientes", "GET /cliente (requer autenticação)");
        endpointsProtegidos.put("Listar Fornecedores", "GET /fornecedor (requer autenticação)");
        endpointsProtegidos.put("Criar Produto", "POST /produto (apenas fornecedor)");
        endpointsProtegidos.put("Atualizar Produto", "PUT /produto/{id} (apenas fornecedor dono)");
        endpointsProtegidos.put("Deletar Produto", "DELETE /produto/{id} (apenas fornecedor dono)");
        
        // Carrinho de Compras (apenas clientes)
        endpointsProtegidos.put("Ver Carrinho", "GET /carrinho (apenas cliente)");
        endpointsProtegidos.put("Adicionar ao Carrinho", "POST /carrinho/item (apenas cliente)");
        endpointsProtegidos.put("Atualizar Item Carrinho", "PATCH /carrinho/item/{id}?quantidade=X (apenas cliente)");
        endpointsProtegidos.put("Remover Item Carrinho", "DELETE /carrinho/item/{id} (apenas cliente)");
        endpointsProtegidos.put("Limpar Carrinho", "DELETE /carrinho (apenas cliente)");
        
        // Pedidos
        endpointsProtegidos.put("Criar Pedido", "POST /pedido (cliente - cria do carrinho)");
        endpointsProtegidos.put("Ver Pedido", "GET /pedido/{id} (cliente/fornecedor)");
        endpointsProtegidos.put("Meus Pedidos", "GET /pedido/meus-pedidos (apenas cliente)");
        endpointsProtegidos.put("Vendas", "GET /pedido/vendas (apenas fornecedor)");
        endpointsProtegidos.put("Atualizar Status", "PATCH /pedido/{id}/status?status=PAGO (apenas cliente)");
        endpointsProtegidos.put("Cancelar Pedido", "POST /pedido/{id}/cancelar (apenas cliente)");
        
        response.put("endpoints_protegidos", endpointsProtegidos);
        
        Map<String, String> autenticacao = new HashMap<>();
        autenticacao.put("tipo", "HTTP Basic Authentication");
        autenticacao.put("usuario", "root");
        autenticacao.put("senha", "aluno");
        
        response.put("autenticacao", autenticacao);
        
        return ResponseEntity.ok(response);
    }
}
