# ═══════════════════════════════════════════════════════════════
# 🧪 TESTES DO SISTEMA DE QR CODE DE RASTREAMENTO
# ═══════════════════════════════════════════════════════════════

## PASSO 1: Criar um pedido (QR Codes serão gerados automaticamente)
POST http://localhost:8080/pedido?idCliente=1
Content-Type: application/json

{
  "tipoPagamento": "PIX",
  "idEnderecoEntrega": 1
}

# Resposta esperada: JSON com itens contendo campo "qrCode"
# Copie um "codigoRastreamento" da resposta (ex: PED0001-ITEM001)


## PASSO 2: Buscar rastreamento pelo código
GET http://localhost:8080/rastreamento/PED0001-ITEM001

# Resposta: JSON com Base64 do QR Code + URLs


## PASSO 3: Visualizar conteúdo completo do QR Code
GET http://localhost:8080/rastreamento/PED0001-ITEM001/conteudo

# Resposta: Texto formatado com TODAS as informações de envio


## PASSO 4: Download do arquivo PNG
GET http://localhost:8080/rastreamento/PED0001-ITEM001/download

# Resposta: Download automático do arquivo PNG


## PASSO 5: Listar todos QR Codes de um pedido
GET http://localhost:8080/rastreamento/pedido/1
Authorization: Bearer <seu_token>

# Resposta: Array com todos os QR Codes do pedido


# ═══════════════════════════════════════════════════════════════
# 📂 VERIFICAÇÃO DOS ARQUIVOS FÍSICOS
# ═══════════════════════════════════════════════════════════════

# Os arquivos PNG estarão salvos em:
# ./qrcodes/PED0001-ITEM001.png
# ./qrcodes/PED0001-ITEM002.png
# etc...

# Você pode abrir diretamente essas imagens e escanear com celular!
