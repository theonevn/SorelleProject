using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SorelleApi.Data;
using SorelleApi.Models;

namespace SorelleApi.Controllers;

[ApiController]
[Route("api/admin")]
[Authorize(Roles = "Admin")]
public class AdminController : ControllerBase
{
    private readonly AdminRepository _repo;

    public AdminController(AdminRepository repo)
    {
        _repo = repo;
    }

    [HttpGet("resumo")]
    public async Task<ActionResult<ResumoAdminDto>> Resumo()
        => Ok(await _repo.ResumoAsync());

    [HttpGet("produtos")]
    public async Task<ActionResult<IEnumerable<ProdutoAdminResponse>>> ListarProdutos()
        => Ok(await _repo.ListarProdutosAsync());

    [HttpPost("produtos")]
    public async Task<IActionResult> CriarProduto(ProdutoAdminRequest p)
    {
        var erro = Validar(p);
        if (erro is not null) return BadRequest(new { erro });

        var id = await _repo.CriarProdutoAsync(p);
        return Ok(new { id });
    }

    [HttpPut("produtos/{id:int}")]
    public async Task<IActionResult> AtualizarProduto(int id, ProdutoAdminRequest p)
    {
        var erro = Validar(p);
        if (erro is not null) return BadRequest(new { erro });

        var linhas = await _repo.AtualizarProdutoAsync(id, p);
        return linhas == 0 ? NotFound(new { erro = "Produto não encontrado." }) : NoContent();
    }

    [HttpDelete("produtos/{id:int}")]
    public async Task<IActionResult> InativarProduto(int id)
    {
        var linhas = await _repo.InativarProdutoAsync(id);
        return linhas == 0 ? NotFound(new { erro = "Produto não encontrado." }) : NoContent();
    }

    [HttpGet("pedidos")]
    public async Task<ActionResult<IEnumerable<PedidoAdminDto>>> ListarPedidos()
        => Ok(await _repo.ListarPedidosAsync());

    [HttpGet("usuarios")]
    public async Task<ActionResult<IEnumerable<UsuarioAdminDto>>> ListarUsuarios()
        => Ok(await _repo.ListarUsuariosAsync());

    private static string? Validar(ProdutoAdminRequest p)
    {
        if (string.IsNullOrWhiteSpace(p.Nome) || p.Nome.Trim().Length < 2)
            return "Informe o nome do produto.";
        if (p.Preco < 0)
            return "O preço não pode ser negativo.";
        if (p.Arvores < 0)
            return "A quantidade de árvores não pode ser negativa.";
        return null;
    }
}