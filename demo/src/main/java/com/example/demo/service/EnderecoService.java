package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.EnderecoRequestDTO;
import com.example.demo.dto.EnderecoResponseDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.EnderecoRepository;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    public EnderecoService(EnderecoRepository enderecoRepository, ClienteRepository clienteRepository) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
    }

    public Endereco findById(Integer idEndereco) {
        return enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new NoSuchElementException(
                        "Endereço " + idEndereco + " não encontrado!"));
    }

    public List<EnderecoResponseDTO> listarEnderecosPorCliente(Integer idCliente) {
        List<Endereco> enderecos = enderecoRepository.findByCliente_IdCliente(idCliente);
        return enderecos.stream()
                .map(EnderecoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Endereco inserirEndereco(EnderecoRequestDTO dto, Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com ID: " + idCliente));

        Boolean isPrincipal = dto.isPrincipal() != null ? dto.isPrincipal() : false;

        if (isPrincipal) {
            enderecoRepository.findByCliente_IdClienteAndIsPrincipalTrue(idCliente)
                    .ifPresent(e -> {
                        e.setIsPrincipal(false);
                        enderecoRepository.save(e);
                    });
        } else if (!enderecoRepository.existsEnderecoPrincipalByCliente(idCliente)) {
            isPrincipal = true;
        }

        Endereco novoEndereco = new Endereco();
        novoEndereco.setApelido(dto.apelido());
        novoEndereco.setCep(dto.cep().replaceAll("-", ""));
        novoEndereco.setRua(dto.rua());
        novoEndereco.setNumero(dto.numero());
        novoEndereco.setComplemento(dto.complemento());
        novoEndereco.setBairro(dto.bairro());
        novoEndereco.setCidade(dto.cidade());
        novoEndereco.setEstado(dto.estado());
        novoEndereco.setIsPrincipal(isPrincipal);
        novoEndereco.setCliente(cliente);

        return enderecoRepository.save(novoEndereco);
    }

    @Transactional
    public Endereco alterarEndereco(EnderecoRequestDTO dto, Integer idEndereco, Integer idCliente) {
        Endereco enderecoExistente = findById(idEndereco);

        if (!enderecoExistente.getCliente().getIdCliente().equals(idCliente)) {
            throw new RegraNegocioException("Este endereço não pertence a você.");
        }

        if (dto.apelido() != null) enderecoExistente.setApelido(dto.apelido());
        if (dto.cep() != null) enderecoExistente.setCep(dto.cep().replaceAll("-", ""));
        if (dto.rua() != null) enderecoExistente.setRua(dto.rua());
        if (dto.numero() != null) enderecoExistente.setNumero(dto.numero());
        if (dto.complemento() != null) enderecoExistente.setComplemento(dto.complemento());
        if (dto.bairro() != null) enderecoExistente.setBairro(dto.bairro());
        if (dto.cidade() != null) enderecoExistente.setCidade(dto.cidade());
        if (dto.estado() != null) enderecoExistente.setEstado(dto.estado());

        if (dto.isPrincipal() != null && dto.isPrincipal() && !enderecoExistente.getIsPrincipal()) {
            enderecoRepository.findByCliente_IdClienteAndIsPrincipalTrue(idCliente)
                    .ifPresent(e -> {
                        e.setIsPrincipal(false);
                        enderecoRepository.save(e);
                    });
            enderecoExistente.setIsPrincipal(true);
        }

        return enderecoRepository.save(enderecoExistente);
    }

    @Transactional
    public void deletarEndereco(Integer idEndereco, Integer idCliente) {
        Endereco endereco = findById(idEndereco);

        if (!endereco.getCliente().getIdCliente().equals(idCliente)) {
            throw new RegraNegocioException("Este endereço não pertence a você.");
        }

        if (endereco.getIsPrincipal()) {
            throw new RegraNegocioException("Não é possível deletar o endereço principal. Defina outro endereço como principal primeiro.");
        }

        endereco.markAsDeleted();
        enderecoRepository.save(endereco);
    }
}
