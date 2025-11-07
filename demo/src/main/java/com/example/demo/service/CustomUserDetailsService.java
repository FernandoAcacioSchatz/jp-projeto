package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

/**
 * Serviço customizado para carregar usuários do banco de dados
 * Usado pelo Spring Security para autenticação
 * 
 * DESABILITADO: Usando usuário em memória definido no application.properties
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carrega o usuário pelo email (usado como username)
     * Este método é chamado automaticamente pelo Spring Security durante o login
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // --- INÍCIO DA DEPURAÇÃO ---
        System.out.println("----------------------------------------------------");
        System.out.println("Tentando carregar usuário pelo email: " + email);
        System.out.println("----------------------------------------------------");
        // --- FIM DA DEPURAÇÃO ---
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com o email: " + email));
        
        return user; // User implementa UserDetails
    }
}
