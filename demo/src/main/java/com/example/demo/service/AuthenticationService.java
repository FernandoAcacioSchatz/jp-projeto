package com.example.demo.service;

import com.example.demo.config.JwtService;
import com.example.demo.dto.AuthenticationRequestDTO;
import com.example.demo.dto.AuthenticationResponseDTO;
import com.example.demo.dto.RefreshTokenRequestDTO;
import com.example.demo.exception.CredenciaisInvalidasException;
import com.example.demo.exception.TokenInvalidoException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final AccountLockoutService accountLockoutService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        String email = request.getEmail();

        if (accountLockoutService.isAccountLocked(email)) {
            throw new CredenciaisInvalidasException(
                    "Conta temporariamente bloqueada devido a múltiplas tentativas de login falhadas. " +
                            "Tente novamente em 15 minutos.");
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            accountLockoutService.resetAttempts(email);

        } catch (AuthenticationException e) {

            accountLockoutService.registerFailedAttempt(email);

            int remainingAttempts = accountLockoutService.getRemainingAttempts(email);
            if (remainingAttempts > 0) {
                throw new CredenciaisInvalidasException(
                        "Email ou senha incorretos. Tentativas restantes: " + remainingAttempts);
            } else {
                throw new CredenciaisInvalidasException(
                        "Conta bloqueada devido a múltiplas tentativas falhadas. Tente novamente em 15 minutos.");
            }
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário não encontrado"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String roleName = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getNomePapel();

        return AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .username(user.getEmail())
                .role(roleName)
                .build();
    }

    public AuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        try {

            String userEmail = jwtService.extractUsername(refreshToken);

            if (userEmail != null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(refreshToken, userDetails)) {

                    String accessToken = jwtService.generateToken(userDetails);

                    User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new TokenInvalidoException("Usuário não encontrado"));

                    String roleName = user.getRoles().isEmpty() ? "USER"
                            : user.getRoles().iterator().next().getNomePapel();

                    return AuthenticationResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken) // Mantém o mesmo refresh token
                            .tokenType("Bearer")
                            .expiresIn(jwtExpiration)
                            .username(user.getEmail())
                            .role(roleName)
                            .build();
                }
            }
        } catch (Exception e) {
            throw new TokenInvalidoException("Refresh token inválido ou expirado");
        }

        throw new TokenInvalidoException("Refresh token inválido");
    }
}
