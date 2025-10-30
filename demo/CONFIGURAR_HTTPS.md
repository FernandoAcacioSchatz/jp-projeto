# 🔐 GUIA DE CONFIGURAÇÃO HTTPS/SSL

## 📋 Visão Geral

Este guia explica como configurar HTTPS/SSL para a aplicação Spring Boot em produção. HTTPS é **ESSENCIAL** para proteger dados sensíveis em trânsito.

---

## 🎯 Por que HTTPS é Obrigatório?

✅ **Criptografa todo o tráfego** entre cliente e servidor  
✅ **Previne ataques Man-in-the-Middle (MITM)**  
✅ **Protege credenciais de login** (JWT tokens, senhas)  
✅ **Protege dados de cartão** durante transmissão  
✅ **Requisito PCI-DSS** para processar pagamentos  
✅ **Melhora SEO** (Google prioriza sites HTTPS)  
✅ **Confiança do usuário** (cadeado no navegador)

---

## 📝 Passo 1: Obter um Certificado SSL

### Opção A: Let's Encrypt (Gratuito, Recomendado)

```bash
# Instalar Certbot
sudo apt-get update
sudo apt-get install certbot

# Gerar certificado
sudo certbot certonly --standalone -d seudominio.com
```

Certificados ficam em: `/etc/letsencrypt/live/seudominio.com/`

### Opção B: Certificado Auto-Assinado (Apenas para Desenvolvimento)

```bash
# Gerar keystore auto-assinado (NÃO usar em produção!)
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 365 \
  -dname "CN=localhost, OU=IT, O=MyCompany, L=City, ST=State, C=BR" \
  -storepass changeit
```

### Opção C: Certificado Comercial

Comprar de autoridades certificadoras (DigiCert, Comodo, GoDaddy, etc.)

---

## 📝 Passo 2: Converter Certificado para PKCS12

Se você obteve certificado do Let's Encrypt:

```bash
# Converter PEM para PKCS12
sudo openssl pkcs12 -export \
  -in /etc/letsencrypt/live/seudominio.com/fullchain.pem \
  -inkey /etc/letsencrypt/live/seudominio.com/privkey.pem \
  -out keystore.p12 \
  -name tomcat \
  -passout pass:sua_senha_aqui
```

---

## 📝 Passo 3: Configurar application.properties

### Opção A: Certificado em Arquivo

Coloque o `keystore.p12` na pasta `src/main/resources/` e configure:

```properties
# Habilitar HTTPS
server.ssl.enabled=true

# Porta HTTPS (padrão: 8443)
server.port=8443

# Caminho do keystore
server.ssl.key-store=classpath:keystore.p12

# Senha do keystore
server.ssl.key-store-password=sua_senha_aqui

# Tipo do keystore
server.ssl.key-store-type=PKCS12

# Alias do certificado
server.ssl.key-alias=tomcat
```

### Opção B: Certificado em Caminho Absoluto

```properties
server.ssl.enabled=true
server.port=8443
server.ssl.key-store=/etc/ssl/certs/keystore.p12
server.ssl.key-store-password=${SERVER_SSL_KEY_STORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

---

## 📝 Passo 4: Configurar Variáveis de Ambiente (Produção)

**NUNCA commitar senhas no Git!** Use variáveis de ambiente:

```bash
# Linux/Mac (.bashrc ou .zshrc)
export SERVER_SSL_ENABLED=true
export SERVER_SSL_KEY_STORE=/etc/ssl/certs/keystore.p12
export SERVER_SSL_KEY_STORE_PASSWORD=sua_senha_super_secreta
export SERVER_SSL_KEY_STORE_TYPE=PKCS12
export SERVER_SSL_KEY_ALIAS=tomcat
```

```cmd
# Windows (CMD)
set SERVER_SSL_ENABLED=true
set SERVER_SSL_KEY_STORE=C:\ssl\keystore.p12
set SERVER_SSL_KEY_STORE_PASSWORD=sua_senha_super_secreta
```

---

## 📝 Passo 5: Habilitar HSTS no SecurityConfig

Descomente no `SecurityConfig.java`:

```java
.headers(headers -> headers
    .frameOptions(frame -> frame.deny())
    // ✅ DESCOMENTAR EM PRODUÇÃO:
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31536000)) // 1 ano
    .contentTypeOptions(contentType -> contentType.disable())
);
```

---

## 📝 Passo 6: Redirecionar HTTP → HTTPS (Opcional)

### Opção A: Via Nginx (Recomendado)

```nginx
server {
    listen 80;
    server_name seudominio.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name seudominio.com;

    ssl_certificate /etc/letsencrypt/live/seudominio.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/seudominio.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Opção B: Via Spring Boot (Connector Duplo)

Criar classe `HttpsRedirectConfig.java`:

```java
@Configuration
public class HttpsRedirectConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
```

---

## 🧪 Passo 7: Testar HTTPS

### Teste Local

```bash
# Iniciar aplicação
./mvnw spring-boot:run

# Testar HTTPS (aceitar certificado auto-assinado no navegador)
curl -k https://localhost:8443/
```

### Teste em Produção

```bash
# Verificar certificado
openssl s_client -connect seudominio.com:443

# Testar redirecionamento
curl -I http://seudominio.com
# Deve retornar: HTTP/1.1 301 Moved Permanently
```

### SSL Labs Test

Acesse: https://www.ssllabs.com/ssltest/analyze.html?d=seudominio.com

Objetivo: **Nota A ou A+**

---

## ✅ Checklist de Segurança HTTPS

- [ ] Certificado SSL válido instalado
- [ ] HTTPS habilitado no application.properties
- [ ] Porta 443 (ou 8443) aberta no firewall
- [ ] HSTS habilitado (max-age de 1 ano)
- [ ] Redirecionamento HTTP → HTTPS configurado
- [ ] Certificado renovado automaticamente (Let's Encrypt)
- [ ] TLS 1.2+ habilitado (desabilitar TLS 1.0/1.1)
- [ ] Cifras fortes configuradas
- [ ] OCSP Stapling habilitado
- [ ] Certificado intermediário incluído
- [ ] Testado no SSL Labs (nota A ou A+)

---

## 🔄 Renovação Automática (Let's Encrypt)

```bash
# Adicionar ao crontab
sudo crontab -e

# Renovar automaticamente a cada 80 dias
0 3 * * * certbot renew --quiet && systemctl restart spring-app
```

---

## 🚨 Troubleshooting

### Erro: "Unable to load key store"

```bash
# Verificar se o arquivo existe
ls -la /caminho/para/keystore.p12

# Verificar se a senha está correta
keytool -list -keystore keystore.p12 -storepass sua_senha
```

### Erro: "SSL handshake failed"

```bash
# Verificar versão do TLS
openssl s_client -connect localhost:8443 -tls1_2

# Verificar cifras suportadas
nmap --script ssl-enum-ciphers -p 8443 localhost
```

### Erro: "Certificate not trusted"

- Certificado auto-assinado: normal em desenvolvimento
- Produção: usar Let's Encrypt ou certificado comercial

---

## 📚 Referências

- [Spring Boot SSL Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver.configure-ssl)
- [Let's Encrypt Documentation](https://letsencrypt.org/docs/)
- [OWASP Transport Layer Protection](https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Protection_Cheat_Sheet.html)
- [Mozilla SSL Configuration Generator](https://ssl-config.mozilla.org/)
