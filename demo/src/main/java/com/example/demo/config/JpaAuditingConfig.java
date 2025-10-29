package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuração para habilitar auditoria automática do JPA
 * Permite o preenchimento automático de @CreatedDate e @LastModifiedDate
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
