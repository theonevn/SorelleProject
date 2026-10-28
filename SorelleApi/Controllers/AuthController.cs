using System.Net.Mail;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using SorelleApi.Data;
using SorelleApi.Models;
using SorelleApi.Services;

namespace SorelleApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly UsuarioRepository _repo;
    private readonly TokenService _tokens;
    private readonly PasswordHasher<Usuario> _hasher = new();

    public AuthController(UsuarioRepository repo, TokenService tokens)
    {
        _repo = repo;
        _tokens = tokens;
    }

    [HttpPost("registrar")]
    public async Task<ActionResult<AuthResponse>> Registrar(RegistroRequest req)
    {
        var nome = req.Nome?.Trim() ?? "";
        var email = req.Email?.Trim().ToLowerInvariant() ?? "";
        var senha = req.Senha ?? "";

        if (nome.Length < 2)
            return BadRequest(new { erro = "Informe seu nome." });
        if (!MailAddress.TryCreate(email, out var endereco) || endereco.Address != email)
            return BadRequest(new { erro = "E-mail inválido." });
        if (senha.Length < 6)
            return BadRequest(new { erro = "A senha precisa ter no mínimo 6 caracteres." });

        if (await _repo.BuscarPorEmailAsync(email) is not null)
            return Conflict(new { erro = "Já existe uma conta com esse e-mail." });

        var usuario = new Usuario { Nome = nome, Email = email };
        var hash = _hasher.HashPassword(usuario, senha);
        usuario.Id = await _repo.CriarAsync(nome, email, hash);

        return Ok(new AuthResponse(_tokens.Gerar(usuario), usuario.Id, usuario.Nome, usuario.Email));
    }

    [HttpPost("login")]
    public async Task<ActionResult<AuthResponse>> Login(LoginRequest req)
    {
        var email = req.Email?.Trim().ToLowerInvariant() ?? "";
        var usuario = await _repo.BuscarPorEmailAsync(email);

        if (usuario is null)
            return Unauthorized(new { erro = "E-mail ou senha incorretos." });

        var resultado = _hasher.VerifyHashedPassword(usuario, usuario.Senha, req.Senha ?? "");
        if (resultado == PasswordVerificationResult.Failed)
            return Unauthorized(new { erro = "E-mail ou senha incorretos." });

        return Ok(new AuthResponse(_tokens.Gerar(usuario), usuario.Id, usuario.Nome, usuario.Email));
    }

    [Authorize]
    [HttpGet("eu")]
    public IActionResult Eu()
    {
        return Ok(new { autenticado = true, nome = User.FindFirst("nome")?.Value });
    }
}