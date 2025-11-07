package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CartaoRequestDTO;
import com.example.demo.dto.CartaoResponseDTO;
import com.example.demo.dto.CartaoResumoDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Cartao;
import com.example.demo.model.Cliente;
import com.example.demo.repository.CartaoRepository;
import com.example.demo.repository.ClienteRepository;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final ClienteRepository clienteRepository;

    public CartaoService(CartaoRepository cartaoRepository, ClienteRepository clienteRepository) {
        this.cartaoRepository = cartaoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public CartaoResponseDTO cadastrarCartao(Integer idCliente, CartaoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        // Validar validade do cartão
        validarValidade(dto.mesValidade(), dto.anoValidade());

        // Criar cartão
        Cartao cartao = new Cartao();
        cartao.setCliente(cliente);
        cartao.setNumeroMascarado("**** **** **** " + dto.ultimos4Digitos());
        cartao.setBandeira(dto.bandeira());
        cartao.setNomeTitular(dto.nomeTitular());
        cartao.setMesValidade(dto.mesValidade());
        cartao.setAnoValidade(dto.anoValidade());
        cartao.setApelido(dto.apelido());

        // Se é o primeiro cartão, define como principal
        List<Cartao> cartoesExistentes = cartaoRepository.findByCliente_IdCliente(idCliente);
        cartao.setIsPrincipal(cartoesExistentes.isEmpty());

        Cartao cartaoSalvo = cartaoRepository.save(cartao);

        return new CartaoResponseDTO(cartaoSalvo);
    }

    /**
     * Lista todos os cartões do cliente
     */
    public List<CartaoResumoDTO> listarCartoesDoCliente(Integer idCliente) {

        List<Cartao> cartoes = cartaoRepository.findByCliente_IdCliente(idCliente);

        return cartoes.stream()
                .map(CartaoResumoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca um cartão por ID
     */
    public CartaoResponseDTO buscarPorId(Integer idCartao, Integer idCliente) {

        Cartao cartao = cartaoRepository.findById(idCartao)
                .orElseThrow(() -> new RegraNegocioException("Cartão não encontrado."));

        // Validar se o cartão pertence ao cliente
        if (!cartao.getCliente().getIdCliente().equals(idCliente)) {
            throw new RegraNegocioException("Este cartão não pertence a você.");
        }

        return new CartaoResponseDTO(cartao);
    }

    /**
     * Define um cartão como principal
     */
    @Transactional
    public CartaoResponseDTO definirComoPrincipal(Integer idCartao, Integer idCliente) {

        Cartao cartao = cartaoRepository.findById(idCartao)
                .orElseThrow(() -> new RegraNegocioException("Cartão não encontrado."));

        // Validar se o cartão pertence ao cliente
        if (!cartao.getCliente().getIdCliente().equals(idCliente)) {
            throw new RegraNegocioException("Este cartão não pertence a você.");
        }

        // Validar se o cartão está vencido
        if (cartao.isVencido()) {
            throw new RegraNegocioException("Não é possível definir um cartão vencido como principal.");
        }

        // Remove a flag principal de todos os outros cartões
        List<Cartao> cartoes = cartaoRepository.findByCliente_IdCliente(idCliente);
        cartoes.forEach(c -> c.setIsPrincipal(false));
        cartaoRepository.saveAll(cartoes);

        // Define o cartão atual como principal
        cartao.setIsPrincipal(true);
        Cartao cartaoAtualizado = cartaoRepository.save(cartao);

        return new CartaoResponseDTO(cartaoAtualizado);
    }

    /**
     * Remove um cartão
     */
    @Transactional
    public void removerCartao(Integer idCartao, Integer idCliente) {

        Cartao cartao = cartaoRepository.findById(idCartao)
                .orElseThrow(() -> new RegraNegocioException("Cartão não encontrado."));

        // Validar se o cartão pertence ao cliente
        if (!cartao.getCliente().getIdCliente().equals(idCliente)) {
            throw new RegraNegocioException("Este cartão não pertence a você.");
        }

        boolean eraPrincipal = cartao.getIsPrincipal();

        cartaoRepository.delete(cartao);

        // Se era principal, define outro como principal
        if (eraPrincipal) {
            List<Cartao> cartoesRestantes = cartaoRepository.findByCliente_IdCliente(idCliente);
            if (!cartoesRestantes.isEmpty()) {
                Cartao novoCartaoPrincipal = cartoesRestantes.get(0);
                novoCartaoPrincipal.setIsPrincipal(true);
                cartaoRepository.save(novoCartaoPrincipal);
            }
        }
    }

    /**
     * Valida a validade do cartão
     */
    private void validarValidade(Integer mes, Integer ano) {
        LocalDate hoje = LocalDate.now();
        int anoAtual = hoje.getYear();
        int mesAtual = hoje.getMonthValue();

        if (ano < anoAtual) {
            throw new RegraNegocioException("O cartão está vencido.");
        }

        if (ano == anoAtual && mes < mesAtual) {
            throw new RegraNegocioException("O cartão está vencido.");
        }
    }
}
