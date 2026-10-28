using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SorelleApi.Data;
using SorelleApi.Models;
using SorelleApi.Services;

namespace SorelleApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]
public class ImpactoController : ControllerBase
{
    private readonly PedidoRepository _repo;

    public ImpactoController(PedidoRepository repo)
    {
        _repo = repo;
    }

    [HttpGet]
    public async Task<ActionResult<ImpactoResponse>> Obter()
    {
        var usuarioId = User.ObterUsuarioId();
        if (usuarioId is null)
            return Unauthorized();

        return Ok(await _repo.ObterImpactoAsync(usuarioId.Value));
    }
}
