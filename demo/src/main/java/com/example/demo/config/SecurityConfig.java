package com.example.demo.config;

// 1. IMPORTS NECESSÁRIOS
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// IMPORTS NOVOS (PARA OS USUÁRIOS)
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) 
                .authorizeHttpRequests(authz -> authz
                        // Permite acesso à página inicial da API
                        .requestMatchers("/").permitAll()
                        
                        // Permite criar cliente e fornecedor
                        .requestMatchers(HttpMethod.POST, "/cliente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/fornecedor").permitAll()
                        
                        // Permite listar produtos sem autenticação
                        .requestMatchers(HttpMethod.GET, "/produto").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produto/**").permitAll()
                        
                        // Permite acesso à página de login
                        .requestMatchers("/login").permitAll() 
                        
                        // Exige autenticação para qualquer outra coisa
                        .anyRequest().authenticated()
                )
                // Autenticação via HTTP Basic (REST API)
                .httpBasic(withDefaults())
                // Também permite login por formulário
                .formLogin(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 3. ADICIONE ESTE MÉTODO INTEIRO
    //    Este método cria os usuários em memória (já que o .properties foi desabilitado)
    @Bean
    public UserDetailsService userDetailsService() {
        
        // Pega o encoder (BCrypt) que definimos acima
        PasswordEncoder encoder = passwordEncoder();

        // Cria o usuário "root" com a senha "aluno"
        // A senha DEVE ser codificada aqui
        UserDetails admin = User.builder()
                .username("root")
                .password(encoder.encode("aluno")) 
                .roles("ADMIN", "USER") // Define as "funções" do usuário
                .build();

        // Você pode adicionar mais usuários se quiser
        // UserDetails user = User.builder()...

        return new InMemoryUserDetailsManager(admin); // Retorna o gerenciador com o usuário admin
    }
}