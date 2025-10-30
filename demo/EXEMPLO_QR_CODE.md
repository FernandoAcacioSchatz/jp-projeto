# 📦 EXEMPLO DE CONTEÚDO DO QR CODE

Quando você escanear o QR Code com o celular, verá algo assim:

```
═══════════════════════════════════════
       RASTREAMENTO DE ENCOMENDA       
═══════════════════════════════════════

📦 CÓDIGO DE RASTREAMENTO:
   PED0001-ITEM001

🏢 FORNECEDOR (REMETENTE):
   Nome: Tech Solutions Ltda
   CNPJ: 12.345.678/0001-90
   Telefone: (11) 98765-4321
   Estado: SP

📦 PRODUTO:
   Nome: Notebook Dell Inspiron 15
   Descrição: Notebook com Intel i7, 16GB RAM, SSD 512GB
   Quantidade: 1 un

👤 CLIENTE (DESTINATÁRIO):
   Nome: Fernando Acácio
   CPF: 123.456.789-00
   Telefone: (11) 91234-5678
   Endereço: Rua das Flores, 123 - Centro - São Paulo/SP - CEP: 01234-567

🔢 INFORMAÇÕES DO PEDIDO:
   Nº Pedido: 1
   Nº Item: 1
   Data: 28/12/2024 14:30
   Status: PENDENTE

═══════════════════════════════════════
   Acesse: /rastreamento/PED0001-ITEM001
═══════════════════════════════════════
```

## 🎯 Casos de Uso

### 1. Cliente Recebe Email de Confirmação
```
Olá Fernando!

Seu pedido #1 foi confirmado! 

Produtos:
- Notebook Dell Inspiron 15 (1x)
  
Para rastrear este item, escaneie o QR Code abaixo
ou acesse: https://seusite.com/rastreamento/PED0001-ITEM001

[QR CODE IMAGE]
```

### 2. Etiqueta de Envio (Fornecedor)
```
┌─────────────────────────────────────┐
│  DE: Tech Solutions Ltda            │
│      São Paulo/SP                   │
│                                     │
│  PARA: Fernando Acácio              │
│        Rua das Flores, 123          │
│        Centro - São Paulo/SP        │
│        CEP: 01234-567               │
│                                     │
│  [QR CODE 400x400px]                │
│                                     │
│  CÓDIGO: PED0001-ITEM001            │
└─────────────────────────────────────┘
```

### 3. Aplicativo Mobile de Rastreamento
```javascript
// Cliente escaneia QR Code
fetch('/rastreamento/PED0001-ITEM001')
  .then(res => res.json())
  .then(data => {
    // Exibe na tela:
    // - Imagem do QR Code (Base64)
    // - Status atual
    // - Previsão de entrega
    // - Botão "Baixar QR Code"
  });
```

### 4. Dashboard do Admin
```
GET /rastreamento/pedido/1
Authorization: Bearer <token>

Resposta:
[
  {
    "codigoRastreamento": "PED0001-ITEM001",
    "qrcodeBase64": "data:image/png;base64,...",
    "urlRastreamento": "/rastreamento/PED0001-ITEM001",
    "urlDownload": "/rastreamento/PED0001-ITEM001/download"
  },
  {
    "codigoRastreamento": "PED0001-ITEM002",
    "qrcodeBase64": "data:image/png;base64,...",
    "urlRastreamento": "/rastreamento/PED0001-ITEM002",
    "urlDownload": "/rastreamento/PED0001-ITEM002/download"
  }
]
```

## 🔧 Configurações Personalizáveis

### application.properties
```properties
# Diretório onde salvar QR Codes
qrcode.diretorio=qrcodes

# Tamanho da imagem (alterar em QRCodeService.java)
# Atualmente: 400x400 pixels
# Para mudar: bitMatrix = qrCodeWriter.encode(..., 600, 600)

# Formato do arquivo (alterar em QRCodeService.java)
# Atualmente: PNG
# Opções: PNG, JPG, BMP, GIF
```

## 📊 Estatísticas Esperadas

Para um pedido com 3 produtos:
- **3 QR Codes gerados** (1 por item)
- **3 arquivos PNG** salvos (~50KB cada)
- **~150KB de Base64** armazenado no banco
- **Tempo de geração**: ~500ms total
- **Tamanho médio**: 400x400px = 160.000 pixels
