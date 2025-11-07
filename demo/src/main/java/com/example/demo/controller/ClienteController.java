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
import com.example.demo.dto.ClienteResponseDTO;
import com.example.demo.model.Cliente;

import com.example.demo.service.ClienteService;
import com.example.demo.dto.ClienteRequestDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/cliente")
public class ClienteController {

    @Autowired
    private ClienteService cService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> findAll() {

        List<ClienteResponseDTO> clientesDTO = cService.listarTodosClientes();

        return ResponseEntity.ok(clientesDTO);

    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ClienteResponseDTO>> findAllPaginado(
            @PageableDefault(size = 10, sort = "nomeCliente") Pageable pageable) {

        Page<ClienteResponseDTO> clientesDTO = cService.listarTodosClientesPaginado(pageable);

        return ResponseEntity.ok(clientesDTO);
    }

    @GetMapping(value = "/{idCliente}")
    @PreAuthorize("hasRole('CLIENTE') and @clienteService.isOwner(authentication, #idCliente)")
    public ResponseEntity<ClienteResponseDTO> buscarPorIdCliente(@PathVariable Integer idCliente) {

        Cliente cliente = cService.findById(idCliente);

        ClienteResponseDTO cResponseDto = new ClienteResponseDTO(cliente);

        return ResponseEntity.ok(cResponseDto);
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criarCliente(@Valid @RequestBody ClienteRequestDTO dto) {

        Cliente novoCliente = cService.inserirCliente(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoCliente.getIdCliente())
                .toUri();

        ClienteResponseDTO responseDto = new ClienteResponseDTO(novoCliente);

        return ResponseEntity.created(uri).body(responseDto);
    }

    @PutMapping(value = "/{idCliente}")
    @PreAuthorize("hasRole('CLIENTE') and @clienteService.isOwner(authentication, #idCliente)")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Integer idCliente,
            @Valid @RequestBody ClienteRequestDTO dto) {

        Cliente clienteAtualizado = cService.alterarCliente(dto, idCliente);

        ClienteResponseDTO cResponseDto = new ClienteResponseDTO(clienteAtualizado);

        return ResponseEntity.ok(cResponseDto);
    }

    @DeleteMapping(value = "/{idCliente}")
    @PreAuthorize("hasRole('CLIENTE') and @clienteService.isOwner(authentication, #idCliente)")
    public ResponseEntity<Void> deletarCliente(@PathVariable Integer idCliente) {

        cService.deletarCliente(idCliente);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{idCliente}/alterar-senha")
    @PreAuthorize("hasRole('CLIENTE') and @clienteService.isOwner(authentication, #idCliente)")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable Integer idCliente,
            @Valid @RequestBody AlterarSenhaDTO dto) {

        if (!dto.senhasConferem()) {
            throw new com.example.demo.exception.RegraNegocioException(
                    "A nova senha e a confirmação não conferem.");
        }

        cService.alterarSenhaComValidacao(dto.senhaAtual(), dto.novaSenha(), idCliente);

        return ResponseEntity.ok().build();
    }
}
