using System.Security.Claims;

namespace SorelleApi.Services;

public static class UserExtensions
{
    public static int? ObterUsuarioId(this ClaimsPrincipal user)
    {
        var valor = user.FindFirst("sub")?.Value;
        return int.TryParse(valor, out var id) ? id : null;
    }
}