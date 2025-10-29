# Validação de CPF e CNPJ - Documentação

## 📋 Visão Geral

Implementação completa de validação de CPF e CNPJ com verificação de dígitos verificadores para a API REST.

## 🏗️ Arquitetura da Solução

### 1. Classes Utilitárias (`util`)
- **CpfValidator**: Valida CPF usando algoritmo de dígitos verificadores
- **CnpjValidator**: Valida CNPJ usando algoritmo de dígitos verificadores

### 2. Validadores Customizados Bean Validation (`validation`)
- **@ValidCpf**: Anotação para validar CPF automaticamente
- **@ValidCnpj**: Anotação para validar CNPJ automaticamente
- **CpfValidatorConstraint**: Implementação do validador de CPF
- **CnpjValidatorConstraint**: Implementação do validador de CNPJ

### 3. Integração nos Services
- **ClienteService**: Valida CPF antes de inserir cliente
- **FornecedorService**: Valida CNPJ antes de inserir fornecedor

### 4. Validação nos DTOs
- **ClienteRequestDTO**: Anotação @ValidCpf aplicada
- **FornecedorRequestDTO**: Anotação @ValidCnpj aplicada

## ✅ Validações Implementadas

### CPF
✔️ Verifica se tem exatamente 11 dígitos
✔️ Rejeita CPFs com todos os dígitos iguais (111.111.111-11, etc.)
✔️ Calcula e valida o primeiro dígito verificador
✔️ Calcula e valida o segundo dígito verificador
✔️ Aceita CPF com ou sem formatação (pontos e traço)
✔️ Armazena no banco sem formatação (apenas números)

### CNPJ
✔️ Verifica se tem exatamente 14 dígitos
✔️ Rejeita CNPJs com todos os dígitos iguais (00.000.000/0000-00, etc.)
✔️ Calcula e valida os dígitos verificadores usando pesos específicos
✔️ Aceita CNPJ com ou sem formatação
✔️ Armazena no banco sem formatação (apenas números)

## 🔍 Exemplos de Uso

### CPF Válidos
- `111.444.777-35` ✅
- `11144477735` ✅
- `123.456.789-09` ✅

### CPF Inválidos
- `111.444.777-36` ❌ (dígito verificador errado)
- `111.111.111-11` ❌ (todos os dígitos iguais)
- `123456` ❌ (tamanho incorreto)

### CNPJ Válidos
- `11.222.333/0001-81` ✅
- `11222333000181` ✅
- `06.990.590/0001-23` ✅

### CNPJ Inválidos
- `11.222.333/0001-80` ❌ (dígito verificador errado)
- `11.111.111/1111-11` ❌ (todos os dígitos iguais)
- `123456` ❌ (tamanho incorreto)

## 📝 Exemplo de Request

### Criar Cliente
```json
POST /cliente
{
  "nomeCliente": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123456",
  "telefone": "(11) 98765-4321",
  "cpf": "111.444.777-35"
}
```

**Respostas possíveis:**
- ✅ 201 Created - CPF válido
- ❌ 400 Bad Request - "CPF inválido. Verifique os dígitos informados."
- ❌ 400 Bad Request - "CPF já cadastrado no sistema."

### Criar Fornecedor
```json
POST /fornecedor
{
  "nome": "Empresa XYZ LTDA",
  "email": "contato@empresa.com",
  "senha": "senha123456",
  "telefone": "(11) 3333-4444",
  "cnpj": "11.222.333/0001-81"
}
```

**Respostas possíveis:**
- ✅ 201 Created - CNPJ válido
- ❌ 400 Bad Request - "CNPJ inválido. Verifique os dígitos informados."
- ❌ 400 Bad Request - "CNPJ já cadastrado no sistema."

## 🔐 Camadas de Validação

### 1️⃣ Validação no DTO (Bean Validation)
```java
@ValidCpf(message = "CPF inválido. Verifique os dígitos informados.")
String cpf
```
- Ocorre automaticamente quando o request chega no controller
- Retorna 400 Bad Request com a mensagem de erro

### 2️⃣ Validação no Service (Lógica de Negócio)
```java
String cpfLimpo = CpfValidator.removeFormat(dto.cpf());
if (!CpfValidator.isValid(cpfLimpo)) {
    throw new CpfException("CPF inválido...");
}
```
- Valida novamente para garantir (defesa em profundidade)
- Remove formatação antes de salvar no banco
- Verifica duplicação no banco de dados

## 🧪 Testes Unitários

Foram criados testes para validar todas as regras:

### CpfValidatorTest
- ✅ testCpfValido()
- ✅ testCpfInvalido()
- ✅ testCpfComTamanhoInvalido()
- ✅ testCpfNuloOuVazio()
- ✅ testFormatacao()
- ✅ testRemoverFormatacao()

### CnpjValidatorTest
- ✅ testCnpjValido()
- ✅ testCnpjInvalido()
- ✅ testCnpjComTamanhoInvalido()
- ✅ testCnpjNuloOuVazio()
- ✅ testFormatacao()
- ✅ testRemoverFormatacao()

Para executar os testes:
```bash
mvnw test -Dtest=CpfValidatorTest,CnpjValidatorTest
```

## 🎯 Benefícios da Implementação

1. **Validação Automática**: Bean Validation no DTO valida antes mesmo de entrar no service
2. **Reutilizável**: Classes utilitárias podem ser usadas em qualquer lugar
3. **Mensagens Claras**: Usuário recebe feedback específico sobre o erro
4. **Segurança**: Impede CPF/CNPJ inválidos de entrarem no sistema
5. **Consistência**: Dados armazenados sempre sem formatação
6. **Testado**: Testes unitários garantem a corretude do algoritmo

## 🔄 Fluxo de Validação

```
Request → @Valid → Bean Validation (@ValidCpf/@ValidCnpj)
                    ↓
                  Service → CpfValidator.isValid() / CnpjValidator.isValid()
                    ↓
                  Remove formatação
                    ↓
                  Verifica duplicação no banco
                    ↓
                  Salva (apenas números)
```

## 📌 Notas Importantes

- CPF/CNPJ são armazenados **sem formatação** no banco (apenas números)
- A validação aceita entrada **com ou sem formatação**
- CPF/CNPJ com todos os dígitos iguais são **rejeitados**
- A validação de dígitos verificadores segue o **algoritmo oficial**
- Mensagens de erro são **claras e amigáveis** ao usuário
