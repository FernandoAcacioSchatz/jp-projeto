package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ProdutoResponseDTO;
import com.example.demo.model.Produto;
import com.example.demo.service.ProdutoService;

@RestController
@RequestMapping(value = "/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService pService;

    @GetMapping
public ResponseEntity<List<ProdutoResponseDTO>> listarTodosProdutos() {
    List<ProdutoResponseDTO> produtosDTO = pService.listarTodosProdutos();
    
    return ResponseEntity.ok(produtosDTO); 
}

    @GetMapping(value = "/{idProduto}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorIdProduto(@PathVariable Integer idProduto) {

        Produto produto = pService.findById(idProduto);

        ProdutoResponseDTO responseDto = new ProdutoResponseDTO(produto);

        return ResponseEntity.ok(responseDto);
    }
}
