package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.ProdutoRequestDTO;
import com.example.demo.dto.ProdutoResponseDTO;
import com.example.demo.model.Produto;
import com.example.demo.service.ProdutoService;

import jakarta.validation.Valid;

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

    
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodosProdutosPaginado(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        
        Page<ProdutoResponseDTO> produtosDTO = pService.listarTodosProdutosPaginado(pageable);
        
        return ResponseEntity.ok(produtosDTO);
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorNome(
            @RequestParam String nome) {
        
        List<ProdutoResponseDTO> produtosDTO = pService.buscarProdutosPorNome(nome);
        
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/buscar/paginado")
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarProdutosPorNomePaginado(
            @RequestParam String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        
        Page<ProdutoResponseDTO> produtosDTO = pService.buscarProdutosPorNomePaginado(nome, pageable);
        
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorCategoria(
            @PathVariable Integer idCategoria) {
        
        List<ProdutoResponseDTO> produtosDTO = pService.buscarProdutosPorCategoria(idCategoria);
        
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/categoria/{idCategoria}/paginado")
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarProdutosPorCategoriaPaginado(
            @PathVariable Integer idCategoria,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        
        Page<ProdutoResponseDTO> produtosDTO = pService.buscarProdutosPorCategoriaPaginado(idCategoria, pageable);
        
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping(value = "/{idProduto}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorIdProduto(@PathVariable Integer idProduto) {

        Produto produto = pService.findById(idProduto);

        ProdutoResponseDTO responseDto = new ProdutoResponseDTO(produto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO dto,
            Authentication authentication) {

        String emailUsuarioLogado = authentication.getName();

        ProdutoResponseDTO produtoCriado = pService.criarProduto(dto, emailUsuarioLogado);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(produtoCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(produtoCriado);
    }

    @PutMapping(value = "/{idProduto}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Integer idProduto,
            @Valid @RequestBody ProdutoRequestDTO dto,
            Authentication authentication) {

        String emailUsuarioLogado = authentication.getName();

        ProdutoResponseDTO produtoAtualizado = pService.atualizarProduto(idProduto, dto, emailUsuarioLogado);

        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping(value = "/{idProduto}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Integer idProduto,
            Authentication authentication) {

        String emailUsuarioLogado = authentication.getName();

        pService.deletarProduto(idProduto, emailUsuarioLogado);

        return ResponseEntity.noContent().build();
    }
}
