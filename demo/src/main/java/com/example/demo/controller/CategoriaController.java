package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CategoriaRequestDTO;
import com.example.demo.dto.CategoriaResponseDTO;
import com.example.demo.model.Categoria;
import com.example.demo.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarTodasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<CategoriaResponseDTO>> listarTodasPaginado(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CategoriaResponseDTO> categorias = categoriaService.listarTodasCategoriasPaginado(pageable);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Integer id) {
        Categoria categoria = categoriaService.findById(id);
        return ResponseEntity.ok(new CategoriaResponseDTO(categoria));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FORNECEDOR', 'ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria novaCategoria = categoriaService.inserirCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CategoriaResponseDTO(novaCategoria));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FORNECEDOR', 'ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria categoriaAtualizada = categoriaService.alterarCategoria(dto, id);
        return ResponseEntity.ok(new CategoriaResponseDTO(categoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
