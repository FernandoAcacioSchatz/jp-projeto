package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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

    @GetMapping(value = "/{idCliente}")
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

        // 4. RETORNA A RESPOSTA RESTful COMPLETA (201 + Location + Corpo)
        return ResponseEntity.created(uri).body(responseDto);
    }

    @PutMapping(value = "/{idCliente}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Integer id,
            @Valid @RequestBody ClienteRequestDTO dto) {

        Cliente clienteAtualizado = cService.alterarCliente(dto, id);

        ClienteResponseDTO cResponseDto = new ClienteResponseDTO(clienteAtualizado);

        return ResponseEntity.ok(cResponseDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Integer id) {

        cService.deletarCliente(id);

        return ResponseEntity.noContent().build();
    }
}
