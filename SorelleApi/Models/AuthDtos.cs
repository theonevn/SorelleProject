namespace SorelleApi.Models;

public record RegistroRequest(string Nome, string Email, string Senha);

public record LoginRequest(string Email, string Senha);

public record AuthResponse(string Token, int UsuarioId, string Nome, string Email);