using Dapper;
using SorelleApi.Models;

namespace SorelleApi.Data;

public class AdminRepository
{
    private readonly Db _db;

    public AdminRepository(Db db)
    {
        _db = db;
    }

    public async Task<ResumoAdminDto> ResumoAsync()
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            SELECT
                (SELECT COUNT(*) FROM Usuarios) AS TotalUsuarios,
                (SELECT COUNT(*) FROM Pedidos) AS TotalPedidos,
                ISNULL((SELECT SUM(Valor_Total) FROM Pedidos), 0) AS ValorVendido,
                ISNULL((SELECT SUM(Total_Arvores) FROM Pedidos), 0) AS TotalArvores";

        return await conn.QuerySingleAsync<ResumoAdminDto>(sql);
    }

    public async Task<IEnumerable<ProdutoAdminResponse>> ListarProdutosAsync()
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            SELECT ID AS Id, Nome, Descricao, Preco,
                   Qtd_Arvores_Impacto AS Arvores, Tag_Eco AS Tag, Ativo
            FROM Produtos
            ORDER BY ID";

        return await conn.QueryAsync<ProdutoAdminResponse>(sql);
    }

    public async Task<int> CriarProdutoAsync(ProdutoAdminRequest p)
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            INSERT INTO Produtos (Nome, Descricao, Preco, Qtd_Arvores_Impacto, Tag_Eco, Ativo)
            VALUES (@Nome, @Descricao, @Preco, @Arvores, @Tag, @Ativo);
            SELECT CAST(SCOPE_IDENTITY() AS INT);";

        return await conn.ExecuteScalarAsync<int>(sql, p);
    }

    public async Task<int> AtualizarProdutoAsync(int id, ProdutoAdminRequest p)
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            UPDATE Produtos
            SET Nome = @Nome, Descricao = @Descricao, Preco = @Preco,
                Qtd_Arvores_Impacto = @Arvores, Tag_Eco = @Tag, Ativo = @Ativo
            WHERE ID = @Id";

        return await conn.ExecuteAsync(sql,
            new { Id = id, p.Nome, p.Descricao, p.Preco, p.Arvores, p.Tag, p.Ativo });
    }

    public async Task<int> InativarProdutoAsync(int id)
    {
        using var conn = _db.CreateConnection();
        return await conn.ExecuteAsync("UPDATE Produtos SET Ativo = 0 WHERE ID = @Id", new { Id = id });
    }

    public async Task<IEnumerable<PedidoAdminDto>> ListarPedidosAsync()
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            SELECT p.ID AS Id, p.Usuario_ID AS UsuarioId, u.Nome AS UsuarioNome,
                   p.Data_Pedido AS DataPedido, p.Valor_Total AS ValorTotal,
                   p.Total_Arvores AS TotalArvores, c.Codigo_Diploma AS CodigoDiploma
            FROM Pedidos p
            JOIN Usuarios u ON u.ID = p.Usuario_ID
            LEFT JOIN Certificados c ON c.Pedido_ID = p.ID
            ORDER BY p.ID DESC";

        return await conn.QueryAsync<PedidoAdminDto>(sql);
    }

    public async Task<IEnumerable<UsuarioAdminDto>> ListarUsuariosAsync()
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            SELECT ID AS Id, Nome, Email, Perfil, DataCadastro
            FROM Usuarios
            ORDER BY ID";

        return await conn.QueryAsync<UsuarioAdminDto>(sql);
    }
}