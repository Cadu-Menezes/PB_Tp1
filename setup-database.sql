-- ============================================================================
-- Script de Configuração Inicial do Banco de Dados PostgreSQL
-- Sistema CRUD de Produtos -- Criado com auxilio do ChatGPT
-- ============================================================================

-- Criar banco de dados (executar como superusuário postgres)
-- psql -U postgres -c "CREATE DATABASE produtos_db;"

-- Conectar ao banco produtos_db
\c produtos_db;

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- Para busca por texto

-- Criar schema (opcional, usar public é suficiente para este projeto)
-- CREATE SCHEMA IF NOT EXISTS produtos;
-- SET search_path TO produtos, public;

-- ============================================================================
-- Criar tabela de produtos
-- ============================================================================
DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL DEFAULT '',
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category VARCHAR(50) NOT NULL,
    stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Criar índices para otimizar consultas
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_stock ON products(stock_quantity);
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products(created_at);

-- Índice para busca de estoque baixo
CREATE INDEX IF NOT EXISTS idx_products_low_stock ON products(stock_quantity) WHERE stock_quantity < 10;

-- Índice para busca de produtos sem estoque
CREATE INDEX IF NOT EXISTS idx_products_out_of_stock ON products(stock_quantity) WHERE stock_quantity = 0;

-- ============================================================================
-- Trigger para atualizar updated_at automaticamente
-- ============================================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_products_updated_at ON products;
CREATE TRIGGER update_products_updated_at 
    BEFORE UPDATE ON products 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Inserir dados de exemplo (opcional)
-- ============================================================================
INSERT INTO products (name, description, price, category, stock_quantity) VALUES
('iPhone 15 Pro', 'Smartphone Apple iPhone 15 Pro com 256GB de armazenamento', 8999.90, 'ELETRONICOS', 15),
('Samsung Galaxy S24', 'Smartphone Samsung Galaxy S24 Ultra 512GB', 6499.00, 'ELETRONICOS', 8),
('MacBook Air M2', 'Notebook Apple MacBook Air com chip M2, 8GB RAM, 256GB SSD', 10999.90, 'ELETRONICOS', 5),
('Camiseta Polo', 'Camiseta Polo masculina 100% algodão', 79.90, 'ROUPAS', 50),
('Jeans Skinny', 'Calça jeans skinny feminina azul escuro', 129.90, 'ROUPAS', 30),
('Clean Code', 'Livro Clean Code - Robert C. Martin', 89.90, 'LIVROS', 25),
('Design Patterns', 'Livro sobre padrões de design em programação', 149.90, 'LIVROS', 12),
('Cafeteira Elétrica', 'Cafeteira elétrica programável para 12 xícaras', 299.90, 'CASA_E_JARDIM', 18),
('Aspirador de Pó', 'Aspirador de pó sem fio 2 em 1', 459.90, 'CASA_E_JARDIM', 7),
('Tênis de Corrida', 'Tênis masculino para corrida com tecnologia de amortecimento', 249.90, 'ESPORTES', 22),
('Bicicleta MTB', 'Bicicleta mountain bike aro 29 com 21 marchas', 1299.90, 'ESPORTES', 3),
('Shampoo Anticaspa', 'Shampoo anticaspa para cabelos oleosos 400ml', 24.90, 'BELEZA', 40),
('Perfume Masculino', 'Perfume masculino amadeirado 100ml', 189.90, 'BELEZA', 15),
('Chocolate 70% Cacau', 'Barra de chocolate amargo 70% cacau 100g', 12.90, 'ALIMENTACAO', 100),
('Café Gourmet', 'Café gourmet torrado e moído 500g', 32.90, 'ALIMENTACAO', 60),
('Lego Creator', 'Set Lego Creator Expert com 2000+ peças', 899.90, 'BRINQUEDOS', 4),
('Boneca Barbie', 'Boneca Barbie Dreamhouse Adventures', 149.90, 'BRINQUEDOS', 20),
('Furadeira de Impacto', 'Furadeira de impacto elétrica 500W com maleta', 189.90, 'FERRAMENTAS', 8),
('Chave de Fenda Set', 'Conjunto de chaves de fenda com 12 peças', 45.90, 'FERRAMENTAS', 35),
('Mochila Executiva', 'Mochila executiva para notebook até 15.6 polegadas', 159.90, 'OUTROS', 25);

-- ============================================================================
-- Verificar dados inseridos
-- ============================================================================
SELECT 'Total de produtos inseridos: ' || COUNT(*) FROM products;
SELECT 'Produtos por categoria:' as info;
SELECT 
    category,
    COUNT(*) as quantidade,
    AVG(price) as preco_medio,
    SUM(stock_quantity) as estoque_total
FROM products 
GROUP BY category 
ORDER BY category;

-- ============================================================================
-- Views úteis para relatórios
-- ============================================================================

-- View para produtos com estoque baixo
CREATE OR REPLACE VIEW v_produtos_estoque_baixo AS
SELECT 
    id,
    name,
    category,
    stock_quantity,
    price,
    (price * stock_quantity) as valor_em_estoque
FROM products 
WHERE stock_quantity < 10
ORDER BY stock_quantity ASC, name;

-- View para produtos sem estoque
CREATE OR REPLACE VIEW v_produtos_sem_estoque AS
SELECT 
    id,
    name,
    category,
    price,
    created_at,
    updated_at
FROM products 
WHERE stock_quantity = 0
ORDER BY updated_at DESC;

-- View para estatísticas por categoria
CREATE OR REPLACE VIEW v_estatisticas_categoria AS
SELECT 
    category,
    COUNT(*) as total_produtos,
    AVG(price) as preco_medio,
    MIN(price) as menor_preco,
    MAX(price) as maior_preco,
    SUM(stock_quantity) as estoque_total,
    SUM(price * stock_quantity) as valor_total_estoque
FROM products 
GROUP BY category
ORDER BY valor_total_estoque DESC;

-- View para produtos mais caros
CREATE OR REPLACE VIEW v_produtos_premium AS
SELECT 
    id,
    name,
    description,
    price,
    category,
    stock_quantity
FROM products 
WHERE price > 1000.00
ORDER BY price DESC;

-- ============================================================================
-- Funções úteis
-- ============================================================================

-- Função para calcular valor total em estoque
CREATE OR REPLACE FUNCTION calcular_valor_estoque()
RETURNS DECIMAL(15,2) AS $$
BEGIN
    RETURN (SELECT COALESCE(SUM(price * stock_quantity), 0) FROM products);
END;
$$ LANGUAGE plpgsql;

-- Função para obter produtos em falta (sem estoque)
CREATE OR REPLACE FUNCTION produtos_em_falta()
RETURNS TABLE(
    produto_id UUID,
    nome VARCHAR(100),
    categoria VARCHAR(50),
    preco DECIMAL(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT id, name, category, price
    FROM products
    WHERE stock_quantity = 0
    ORDER BY name;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- Grants de permissão (ajustar conforme necessário)
-- ============================================================================

-- Criar usuário específico para a aplicação (opcional)
-- CREATE USER produtos_app WITH ENCRYPTED PASSWORD 'senha_segura_aqui';
-- GRANT CONNECT ON DATABASE produtos_db TO produtos_app;
-- GRANT USAGE ON SCHEMA public TO produtos_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON products TO produtos_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO produtos_app;

-- ============================================================================
-- Verificações finais
-- ============================================================================
\echo 'Configuração do banco de dados concluída!'
\echo 'Executando verificações finais...'

SELECT 'Tabelas criadas:' as info;
\dt

SELECT 'Índices criados:' as info;
\di

SELECT 'Views criadas:' as info;
\dv

SELECT 'Funções criadas:' as info;
\df

SELECT 'Valor total em estoque: R$ ' || calcular_valor_estoque() as valor_estoque;

\echo 'Banco de dados pronto para uso!'
\echo 'Para conectar a aplicação, use as configurações:'
\echo 'URL: jdbc:postgresql://localhost:5432/produtos_db'
\echo 'User: postgres (ou o usuário que você criou)'
\echo 'Password: (sua senha do PostgreSQL)'