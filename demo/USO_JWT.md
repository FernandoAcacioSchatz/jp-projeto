# 🔑 GUIA DE USO - JWT AUTHENTICATION

## 📋 Visão Geral

A API agora usa **JWT (JSON Web Tokens)** para autenticação segura. Você precisa obter um token fazendo login antes de acessar endpoints protegidos.

---

## 🚀 Fluxo de Autenticação

```
1. Cliente faz login → Recebe access_token + refresh_token
2. Cliente usa access_token no header de todas as requisições
3. Quando access_token expira (24h) → Usa refresh_token para renovar
4. Refresh_token válido por 7 dias
```

---

## 📝 Endpoints de Autenticação

### 1️⃣ Login (Obter Token)

**POST** `/auth/login`

#### Request Body:
```json
{
  "email": "cliente@example.com",
  "password": "senha123"
}
```

#### Response (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "cliente@example.com",
  "role": "ROLE_CLIENTE"
}
```

#### Response Errors:
```json
// 401 Unauthorized - Email ou senha incorretos
{
  "timestamp": "2025-10-29T10:30:00",
  "status": 401,
  "error": "Não Autorizado",
  "message": "Email ou senha incorretos",
  "path": "/auth/login"
}

// 400 Bad Request - Validação falhou
{
  "timestamp": "2025-10-29T10:30:00",
  "status": 400,
  "errors": {
    "email": "Email inválido",
    "password": "Senha é obrigatória"
  }
}
```

---

### 2️⃣ Renovar Token (Refresh)

**POST** `/auth/refresh`

#### Request Body:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk..."
}
```

#### Response (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.NEW_TOKEN_HERE...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.SAME_REFRESH_TOKEN...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "cliente@example.com",
  "role": "ROLE_CLIENTE"
}
```

#### Response Errors:
```json
// 401 Unauthorized - Refresh token inválido/expirado
{
  "timestamp": "2025-10-29T10:30:00",
  "status": 401,
  "error": "Não Autorizado",
  "message": "Refresh token inválido ou expirado",
  "path": "/auth/refresh"
}
```

---

## 🔐 Como Usar o Token

### Todas as requisições protegidas devem incluir o header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk...
```

---

## 📚 Exemplos de Uso

### 🟢 JavaScript (Fetch API)

```javascript
// 1. Login
async function login(email, password) {
  const response = await fetch('http://localhost:8080/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
    throw new Error('Login falhou');
  }

  const data = await response.json();
  
  // Salvar tokens no localStorage
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  
  return data;
}

// 2. Fazer requisição autenticada
async function buscarProdutos() {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:8080/produto', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (response.status === 401) {
    // Token expirado, tentar renovar
    await refreshToken();
    return buscarProdutos(); // Retry
  }

  return response.json();
}

// 3. Renovar token
async function refreshToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('http://localhost:8080/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ refreshToken })
  });

  if (!response.ok) {
    // Refresh token expirado, redirecionar para login
    localStorage.clear();
    window.location.href = '/login';
    return;
  }

  const data = await response.json();
  localStorage.setItem('accessToken', data.accessToken);
}
```

---

### 🔵 cURL (Terminal)

```bash
# 1. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@example.com",
    "password": "senha123"
  }'

# Salvar token em variável
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk..."

# 2. Usar token em requisição
curl -X GET http://localhost:8080/cartao \
  -H "Authorization: Bearer $TOKEN"

# 3. Renovar token
REFRESH_TOKEN="eyJhbGciOiJIUzI1NiJ9.REFRESH_TOKEN_HERE..."

curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\": \"$REFRESH_TOKEN\"}"
```

---

### 🟡 Python (Requests)

```python
import requests

# 1. Login
def login(email, password):
    response = requests.post(
        'http://localhost:8080/auth/login',
        json={
            'email': email,
            'password': password
        }
    )
    
    if response.status_code == 200:
        data = response.json()
        return data['accessToken'], data['refreshToken']
    else:
        raise Exception('Login falhou')

# 2. Fazer requisição autenticada
def buscar_produtos(token):
    response = requests.get(
        'http://localhost:8080/produto',
        headers={
            'Authorization': f'Bearer {token}'
        }
    )
    
    if response.status_code == 401:
        # Token expirado, renovar
        novo_token = refresh_token(refresh_token_value)
        return buscar_produtos(novo_token)
    
    return response.json()

# 3. Renovar token
def refresh_token(refresh_token):
    response = requests.post(
        'http://localhost:8080/auth/refresh',
        json={
            'refreshToken': refresh_token
        }
    )
    
    if response.status_code == 200:
        data = response.json()
        return data['accessToken']
    else:
        raise Exception('Refresh token inválido')

# Uso
access_token, refresh_token_value = login('cliente@example.com', 'senha123')
produtos = buscar_produtos(access_token)
print(produtos)
```

---

### 🟣 Postman

1. **Fazer Login:**
   - Method: `POST`
   - URL: `http://localhost:8080/auth/login`
   - Body → raw → JSON:
     ```json
     {
       "email": "cliente@example.com",
       "password": "senha123"
     }
     ```
   - Copiar o `accessToken` da resposta

2. **Configurar Token:**
   - Na coleção ou requisição → Authorization
   - Type: `Bearer Token`
   - Token: `<colar accessToken aqui>`

3. **Ou usar Header manual:**
   - Headers → Add:
     - Key: `Authorization`
     - Value: `Bearer <accessToken>`

---

## ⚠️ Erros Comuns

### 401 Unauthorized

```json
{
  "timestamp": "2025-10-29T10:30:00",
  "status": 401,
  "error": "Não Autorizado",
  "message": "Token de autenticação ausente ou inválido",
  "path": "/cartao"
}
```

**Causas:**
- Token não foi enviado no header
- Token expirado (24h)
- Token inválido/corrompido
- Formato incorreto (falta "Bearer ")

**Solução:**
- Fazer login novamente
- Ou usar refresh token para renovar

---

### 403 Forbidden

```json
{
  "timestamp": "2025-10-29T10:30:00",
  "status": 403,
  "error": "Acesso Negado",
  "message": "Você não tem permissão para acessar este recurso",
  "path": "/admin/relatorios"
}
```

**Causas:**
- Usuário autenticado mas sem permissão (role incorreta)
- Tentando acessar endpoint de ADMIN com role CLIENTE

**Solução:**
- Verificar se o usuário tem a role correta
- Endpoints `/admin/*` requerem `ROLE_ADMIN`

---

## 🔄 Ciclo de Vida do Token

```
Dia 1 (Login):
├─ Access Token válido por 24h
└─ Refresh Token válido por 7 dias

Dia 2 (Access Token expira):
├─ Usar Refresh Token para obter novo Access Token
├─ Novo Access Token válido por mais 24h
└─ Refresh Token continua o mesmo (expira em 6 dias)

Dia 8 (Refresh Token expira):
└─ Fazer login novamente
```

---

## 🔐 Segurança

### ✅ Boas Práticas:

1. **NUNCA** armazene tokens em cookies sem `httpOnly`
2. **NUNCA** compartilhe tokens em URLs
3. **SEMPRE** use HTTPS em produção
4. **SEMPRE** limpe tokens ao fazer logout
5. **SEMPRE** verifique expiração do token

### ⚠️ Armazenamento de Tokens:

| Local              | Segurança | Persistência | Recomendação |
|--------------------|-----------|--------------|--------------|
| **localStorage**   | Médio     | Persistente  | ✅ OK para SPAs |
| **sessionStorage** | Médio     | Sessão       | ✅ OK (mais seguro) |
| **Cookie httpOnly**| Alto      | Configurável | ✅ Melhor opção |
| **Variável JS**    | Baixo     | Volátil      | ❌ Evitar |

---

## 📞 Suporte

Dúvidas? Problemas com autenticação?
- Verifique o `SEGURANCA.md` para mais informações
- Verifique os logs da aplicação
- Teste com Postman primeiro

**Token expirado?** Use o endpoint `/auth/refresh`!
