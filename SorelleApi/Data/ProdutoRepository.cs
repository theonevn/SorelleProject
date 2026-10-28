using Dapper;
using SorelleApi.Models;

namespace SorelleApi.Data;

public class ProdutoRepository
{
    private readonly Db _db;

    public ProdutoRepository(Db db)
    {
        _db = db;
    }

    public async Task<IEnumerable<Produto>> ListarAsync()
    {
        using var conn = _db.CreateConnection();

        const string sql = @"
            SELECT ID AS Id, Nome, Descricao, Preco,
                   Qtd_Arvores_Impacto AS Arvores,
                   Tag_Eco AS Tag
            FROM Produtos
            WHERE Ativo = 1
            ORDER BY ID";

        return await conn.QueryAsync<Produto>(sql);
    }
}