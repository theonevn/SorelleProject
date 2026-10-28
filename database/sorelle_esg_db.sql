IF DB_ID(N'SorelleESG_DB') IS NULL
    CREATE DATABASE SorelleESG_DB;
GO

USE SorelleESG_DB;
GO

CREATE TABLE Usuarios (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Senha VARCHAR(255) NOT NULL, -- Armazenará o hash da senha
    DataCadastro DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE Produtos (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Descricao VARCHAR(255),
    Preco DECIMAL(10,2) NOT NULL CHECK (Preco >= 0),
    Qtd_Arvores_Impacto INT NOT NULL CHECK (Qtd_Arvores_Impacto >= 0),
    Tag_Eco VARCHAR(50),
    Ativo BIT NOT NULL DEFAULT 1
);

CREATE TABLE Pedidos (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Usuario_ID INT NOT NULL FOREIGN KEY REFERENCES Usuarios(ID),
    Data_Pedido DATETIME NOT NULL DEFAULT GETDATE(),
    Valor_Total DECIMAL(10,2) NOT NULL,
    Total_Arvores INT NOT NULL
);

CREATE TABLE ItensPedido (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Pedido_ID INT NOT NULL FOREIGN KEY REFERENCES Pedidos(ID),
    Produto_ID INT NOT NULL FOREIGN KEY REFERENCES Produtos(ID),
    Quantidade INT NOT NULL CHECK (Quantidade > 0),
    Preco_Unitario DECIMAL(10,2) NOT NULL
);

CREATE TABLE Certificados (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Usuario_ID INT NOT NULL FOREIGN KEY REFERENCES Usuarios(ID),
    Pedido_ID INT NOT NULL FOREIGN KEY REFERENCES Pedidos(ID),
    Nome_Certificado VARCHAR(100) NOT NULL,
    Codigo_Diploma VARCHAR(50) UNIQUE NOT NULL,
    Total_Arvores_Plantadas INT NOT NULL,
    Valor_Total_Compra DECIMAL(10,2) NOT NULL,
    Data_Emissao DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE HistoricoPrecos (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Produto_ID INT NOT NULL FOREIGN KEY REFERENCES Produtos(ID),
    Preco_Antigo DECIMAL(10,2) NOT NULL,
    Preco_Novo DECIMAL(10,2) NOT NULL,
    Data_Alteracao DATETIME NOT NULL DEFAULT GETDATE()
);
GO

INSERT INTO Produtos (Nome, Descricao, Preco, Qtd_Arvores_Impacto, Tag_Eco) VALUES
('Sérum Açaí', 'Revitalização profunda com bioativos puros da floresta Amazônica.', 89.90, 1, 'Vegano'),
('Creme Castanha', 'Nutrição intensa e hidratação prolongada para a pele.', 55.90, 3, 'Reciclado'),
('Óleo Andiroba', 'Puro óleo essencial com alto poder regenerador natural.', 120.00, 1, 'Comunidade'),
('Kit Ritual da Mata', 'A experiência completa de spa sustentável e luxuoso em casa.', 210.50, 3, 'Impacto Triplo');
GO

-- TRIGGER: registra toda mudança de preço de um produto
CREATE TRIGGER trg_Produtos_HistoricoPreco
ON Produtos
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO HistoricoPrecos (Produto_ID, Preco_Antigo, Preco_Novo)
    SELECT d.ID, d.Preco, i.Preco
    FROM deleted d
    JOIN inserted i ON i.ID = d.ID
    WHERE i.Preco <> d.Preco;
END;
GO

-- TIPO: lista de itens que o app envia ao finalizar a compra
CREATE TYPE dbo.ItemCompraType AS TABLE (
    Produto_ID INT NOT NULL,
    Quantidade INT NOT NULL CHECK (Quantidade > 0)
);
GO

-- PROCEDURE: finaliza a compra (pedido + itens + certificado) de uma vez
CREATE PROCEDURE sp_RegistrarCompra
    @Usuario_ID INT,
    @Nome_Certificado VARCHAR(100),
    @Itens dbo.ItemCompraType READONLY
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @Valor DECIMAL(10,2), @Arvores INT, @PedidoID INT, @Codigo VARCHAR(50);

        SELECT @Valor = SUM(p.Preco * i.Quantidade),
               @Arvores = SUM(p.Qtd_Arvores_Impacto * i.Quantidade)
        FROM @Itens i
        JOIN Produtos p ON p.ID = i.Produto_ID AND p.Ativo = 1;

        IF @Valor IS NULL
            THROW 50001, 'Nenhum item válido na compra.', 1;

        INSERT INTO Pedidos (Usuario_ID, Valor_Total, Total_Arvores)
        VALUES (@Usuario_ID, @Valor, @Arvores);
        SET @PedidoID = SCOPE_IDENTITY();

        INSERT INTO ItensPedido (Pedido_ID, Produto_ID, Quantidade, Preco_Unitario)
        SELECT @PedidoID, p.ID, i.Quantidade, p.Preco
        FROM @Itens i
        JOIN Produtos p ON p.ID = i.Produto_ID AND p.Ativo = 1;

        SET @Codigo = 'SOR-' + CONVERT(VARCHAR(8), GETDATE(), 112) + '-'
                      + RIGHT('00000' + CAST(@PedidoID AS VARCHAR(10)), 5);

        INSERT INTO Certificados (Usuario_ID, Pedido_ID, Nome_Certificado,
                                  Codigo_Diploma, Total_Arvores_Plantadas, Valor_Total_Compra)
        VALUES (@Usuario_ID, @PedidoID, @Nome_Certificado, @Codigo, @Arvores, @Valor);

        COMMIT TRANSACTION;

        SELECT @PedidoID AS Pedido_ID, @Codigo AS Codigo_Diploma,
               @Arvores AS Total_Arvores, @Valor AS Valor_Total;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- PROCEDURE: dados do painel de impacto de um usuário
CREATE PROCEDURE sp_PainelImpacto
    @Usuario_ID INT
AS
BEGIN
    SET NOCOUNT ON;

    ;WITH T AS (
        SELECT ISNULL(SUM(Total_Arvores_Plantadas), 0) AS Arvores
        FROM Certificados
        WHERE Usuario_ID = @Usuario_ID
    )
    SELECT Arvores AS Total_Arvores,
           Arvores * 15 AS Co2_Retido_Kg,
           CASE WHEN Arvores >= 100 THEN 'Guardião da Mata'
                WHEN Arvores >= 50  THEN 'Protetor da Floresta'
                WHEN Arvores >= 10  THEN 'Semeador'
                ELSE 'Semente' END AS Nivel
    FROM T;

    SELECT Nome_Certificado, Codigo_Diploma, Total_Arvores_Plantadas, Data_Emissao
    FROM Certificados
    WHERE Usuario_ID = @Usuario_ID
    ORDER BY Data_Emissao DESC;
END;
GO