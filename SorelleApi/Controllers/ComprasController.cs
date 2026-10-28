using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using SorelleApi.Data;
using SorelleApi.Models;
using SorelleApi.Services;

namespace SorelleApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class ComprasController : ControllerBase
{
    private readonly PedidoRepository _repo;

    public ComprasController(PedidoRepository repo)
    {
        _repo = repo;
    }

    [HttpPost]
    public async Task<ActionResult<CompraResponse>> Comprar(CompraRequest req)
    {
        var usuarioId = User.ObterUsuarioId();
        if (usuarioId is null)
            return Unauthorized();

        var nome = req.NomeCertificado?.Trim() ?? "";
        if (nome.Length < 2)
            return BadRequest(new { erro = "Informe o nome para o certificado." });

        if (req.Itens is null || req.Itens.Count == 0)
            return BadRequest(new { erro = "A sacola está vazia." });

        if (req.Itens.Any(i => i.ProdutoId <= 0 || i.Quantidade <= 0 || i.Quantidade > 99))
            return BadRequest(new { erro = "Itens inválidos na sacola." });

        try
        {
            var resposta = await _repo.RegistrarCompraAsync(usuarioId.Value, nome, req.Itens);
            return Ok(resposta);
        }
        catch (SqlException ex) when (ex.Number == 50001)
        {
            return BadRequest(new { erro = "Nenhum produto válido na compra." });
        }
    }
}