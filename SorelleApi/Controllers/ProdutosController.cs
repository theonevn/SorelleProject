using Microsoft.AspNetCore.Mvc;
using SorelleApi.Data;
using SorelleApi.Models;

namespace SorelleApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ProdutosController : ControllerBase
{
    private readonly ProdutoRepository _repo;

    public ProdutosController(ProdutoRepository repo)
    {
        _repo = repo;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Produto>>> Listar()
    {
        return Ok(await _repo.ListarAsync());
    }
}