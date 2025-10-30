# 🔄 FLUXO COMPLETO DO SISTEMA DE QR CODE DE RASTREAMENTO

## 📊 Diagrama do Processo

```
┌─────────────────────────────────────────────────────────────────┐
│  1️⃣  CLIENTE CRIA PEDIDO                                        │
│  POST /pedido?idCliente=1                                       │
│  Body: { tipoPagamento, idEnderecoEntrega }                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  2️⃣  PedidoService.criarPedidoDoCarrinho()                      │
│  • Valida carrinho e estoque                                    │
│  • Cria Pedido e ItemPedido                                     │
│  • Decrementa estoque                                           │
│  • SALVA PEDIDO NO BANCO                                        │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  3️⃣  QRCodeService.gerarQRCodeParaItem() - PARA CADA ITEM      │
│                                                                 │
│  A) Coleta informações:                                         │
│     • Fornecedor (nome, CNPJ, telefone, estado)                 │
│     • Produto (nome, descrição, quantidade)                     │
│     • Cliente (nome, CPF, telefone)                             │
│     • Endereço completo de entrega                              │
│     • Dados do pedido (ID, data, status)                        │
│                                                                 │
│  B) Gera código de rastreamento:                                │
│     PED0001-ITEM001                                             │
│                                                                 │
│  C) Monta texto formatado (conteudoQRCode)                      │
│                                                                 │
│  D) Usa ZXing para gerar QR Code:                               │
│     • BitMatrix 400x400 pixels                                  │
│     • Formato: PNG                                              │
│                                                                 │
│  E) Converte para Base64:                                       │
│     "data:image/png;base64,iVBORw0KGgoAAAANS..."               │
│                                                                 │
│  F) Salva arquivo físico:                                       │
│     ./qrcodes/PED0001-ITEM001.png                               │
│                                                                 │
│  G) Persiste no banco (tb_qrcodes):                             │
│     • id                                                        │
│     • item_pedido_id (FK)                                       │
│     • codigo_rastreamento (UNIQUE)                              │
│     • conteudo_qrcode (TEXT)                                    │
│     • qrcode_base64 (LONGTEXT) ← Para API                       │
│     • qrcode_caminho_arquivo (VARCHAR 255) ← Backup físico      │
│     • created_at, updated_at                                    │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  4️⃣  RESPOSTA DA API (PedidoResponseDTO)                        │
│                                                                 │
│  {                                                              │
│    "id": 1,                                                     │
│    "cliente": {...},                                            │
│    "itens": [                                                   │
│      {                                                          │
│        "id": 1,                                                 │
│        "produto": {...},                                        │
│        "quantidade": 2,                                         │
│        "qrCode": {                                              │
│          "codigoRastreamento": "PED0001-ITEM001",               │
│          "qrcodeBase64": "data:image/png;base64,...",           │
│          "urlRastreamento": "/rastreamento/PED0001-ITEM001",    │
│          "urlDownload": "/rastreamento/PED0001-ITEM001/download"│
│        }                                                        │
│      }                                                          │
│    ],                                                           │
│    ...                                                          │
│  }                                                              │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  5️⃣  CLIENTE PODE RASTREAR                                      │
│                                                                 │
│  📱 Opção 1: Escanear QR Code com celular                       │
│     → Lê todas as informações de envio                          │
│                                                                 │
│  🌐 Opção 2: API /rastreamento/{codigo}                         │
│     → Retorna JSON com Base64 + URLs                            │
│                                                                 │
│  📥 Opção 3: Download PNG físico                                │
│     → GET /rastreamento/{codigo}/download                       │
│                                                                 │
│  📄 Opção 4: Visualizar texto completo                          │
│     → GET /rastreamento/{codigo}/conteudo                       │
│                                                                 │
│  📦 Opção 5: Listar todos QR Codes do pedido                    │
│     → GET /rastreamento/pedido/{idPedido}                       │
└─────────────────────────────────────────────────────────────────┘
```

## 🗄️ Estrutura de Armazenamento

### Banco de Dados (MariaDB)
```
tb_qrcodes
├── id (PK)
├── item_pedido_id (FK) → tb_itens_pedido
├── codigo_rastreamento (UNIQUE) "PED0001-ITEM001"
├── conteudo_qrcode (TEXT) ← Texto completo formatado
├── qrcode_base64 (LONGTEXT) ← "data:image/png;base64,..."
├── qrcode_caminho_arquivo ← "./qrcodes/PED0001-ITEM001.png"
├── created_at
└── updated_at
```

### Sistema de Arquivos
```
projeto/
└── qrcodes/
    ├── PED0001-ITEM001.png  ← 400x400 pixels
    ├── PED0001-ITEM002.png
    ├── PED0002-ITEM001.png
    └── ...
```

## 🎯 Benefícios da Implementação

✅ **Rastreamento Individual**: Cada produto tem seu próprio QR Code  
✅ **Informações Completas**: Fornecedor, produto, cliente, endereços  
✅ **Duplo Backup**: Base64 no banco + arquivo físico  
✅ **API RESTful**: 4 endpoints para diferentes necessidades  
✅ **Formato Universal**: PNG escaneável por qualquer celular  
✅ **Código Único**: Impossível duplicar rastreamentos  
✅ **Auditável**: Timestamps de criação/atualização  

## 🔐 Segurança

- ✅ Endpoint `/rastreamento/{codigo}` é **público** (qualquer um com código pode rastrear)
- ✅ Endpoint `/rastreamento/pedido/{idPedido}` requer **autenticação** (ROLE_CLIENTE ou ROLE_ADMIN)
- ✅ Códigos de rastreamento são únicos e não sequenciais
- ✅ Validação de propriedade do endereço ao criar pedido

## 📱 Experiência do Usuário

1. **Cliente faz pedido** → Recebe QR Codes na resposta da API
2. **Cliente escaneia QR Code** → Vê todas as informações de envio
3. **Cliente acompanha pedido** → Pode baixar PNG ou consultar via API
4. **Fornecedor prepara envio** → Imprime QR Code da etiqueta
5. **Transportadora escaneia** → Registra movimentação (futuro)
