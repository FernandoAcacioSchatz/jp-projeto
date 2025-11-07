package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.AlterarSenhaDTO;
import com.example.demo.dto.FornecedorRequestDTO;
import com.example.demo.dto.FornecedorResponseDTO;
import com.example.demo.model.Fornecedor;
import com.example.demo.service.FornecedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/fornecedor")
public class FornecedorController {

    @Autowired
    private FornecedorService fService;

    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> findAll() {

        List<FornecedorResponseDTO> fornecedoresDTO = fService.listarTodosFornecedores();

        return ResponseEntity.ok(fornecedoresDTO);

    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<FornecedorResponseDTO>> findAllPaginado(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<FornecedorResponseDTO> fornecedoresDTO = fService.listarTodosFornecedoresPaginado(pageable);

        return ResponseEntity.ok(fornecedoresDTO);
    }

    @GetMapping(value = "/{idFornecedor}")
    @PreAuthorize("hasRole('FORNECEDOR') and @fornecedorService.isOwner(authentication, #idFornecedor)")
    public ResponseEntity<FornecedorResponseDTO> buscarPorIdFornecedor(@PathVariable Integer idFornecedor) {

        Fornecedor fornecedor = fService.findById(idFornecedor);

        FornecedorResponseDTO fResponseDto = new FornecedorResponseDTO(fornecedor);

        return ResponseEntity.ok(fResponseDto);
    }

    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> criarFornecedor(@Valid @RequestBody FornecedorRequestDTO dto) {

        Fornecedor novoFornecedor = fService.inserirFornecedor(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoFornecedor.getIdFornecedor())
                .toUri();

        FornecedorResponseDTO responseDto = new FornecedorResponseDTO(novoFornecedor);

        return ResponseEntity.created(uri).body(responseDto);

    }

    @PutMapping(value = "/{idFornecedor}")
    @PreAuthorize("hasRole('FORNECEDOR') and @fornecedorService.isOwner(authentication, #idFornecedor)")
    public ResponseEntity<FornecedorResponseDTO> atualizarFornecedor(@PathVariable Integer idFornecedor,
            @Valid @RequestBody FornecedorRequestDTO dto) {

        Fornecedor fornecedorAtualizado = fService.alteraFornecedor(dto, idFornecedor);

        FornecedorResponseDTO fResponseDto = new FornecedorResponseDTO(fornecedorAtualizado);

        return ResponseEntity.ok(fResponseDto);
    }

    @DeleteMapping(value = "/{idFornecedor}")
    @PreAuthorize("hasRole('FORNECEDOR') and @fornecedorService.isOwner(authentication, #idFornecedor)")
    public ResponseEntity<Void> deletarFornecedor(@PathVariable Integer idFornecedor) {

        fService.deletarFornecedor(idFornecedor);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{idFornecedor}/alterar-senha")
    @PreAuthorize("hasRole('FORNECEDOR') and @fornecedorService.isOwner(authentication, #idFornecedor)")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable Integer idFornecedor,
            @Valid @RequestBody AlterarSenhaDTO dto) {

        if (!dto.senhasConferem()) {
            throw new com.example.demo.exception.RegraNegocioException(
                    "A nova senha e a confirmação não conferem.");
        }

        fService.alterarSenhaComValidacao(dto.senhaAtual(), dto.novaSenha(), idFornecedor);

        return ResponseEntity.ok().build();
    }

}
