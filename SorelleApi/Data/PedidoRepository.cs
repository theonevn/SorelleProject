using System.Data;
using Dapper;
using SorelleApi.Models;

namespace SorelleApi.Data;

public class PedidoRepository
{
    private readonly Db _db;

    public PedidoRepository(Db db)
    {
        _db = db;
    }

    public async Task<CompraResponse> RegistrarCompraAsync(
        int usuarioId, string nomeCertificado, IEnumerable<ItemCompraRequest> itens)
    {
        var tabela = new DataTable();
        tabela.Columns.Add("Produto_ID", typeof(int));
        tabela.Columns.Add("Quantidade", typeof(int));
        foreach (var item in itens)
            tabela.Rows.Add(item.ProdutoId, item.Quantidade);

        var parametros = new DynamicParameters();
        parametros.Add("@Usuario_ID", usuarioId);
        parametros.Add("@Nome_Certificado", nomeCertificado);
        parametros.Add("@Itens", tabela.AsTableValuedParameter("dbo.ItemCompraType"));

        using var conn = _db.CreateConnection();
        return await conn.QuerySingleAsync<CompraResponse>(
            "sp_RegistrarCompra", parametros, commandType: CommandType.StoredProcedure);
    }

    public async Task<ImpactoResponse> ObterImpactoAsync(int usuarioId)
    {
        using var conn = _db.CreateConnection();
        using var multi = await conn.QueryMultipleAsync(
            "sp_PainelImpacto", new { Usuario_ID = usuarioId },
            commandType: CommandType.StoredProcedure);

        var impacto = await multi.ReadSingleAsync<ImpactoResponse>();
        impacto.Certificados = (await multi.ReadAsync<CertificadoDto>()).ToList();
        return impacto;
    }
}