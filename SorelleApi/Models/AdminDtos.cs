namespace SorelleApi.Models;

public class ProdutoAdminRequest
{
    public string Nome { get; set; } = "";
    public string? Descricao { get; set; }
    public decimal Preco { get; set; }
    public int Arvores { get; set; }
    public string? Tag { get; set; }
    public bool Ativo { get; set; } = true;
}

public class ProdutoAdminResponse
{
    public int Id { get; set; }
    public string Nome { get; set; } = "";
    public string? Descricao { get; set; }
    public decimal Preco { get; set; }
    public int Arvores { get; set; }
    public string? Tag { get; set; }
    public bool Ativo { get; set; }
}

public class PedidoAdminDto
{
    public int Id { get; set; }
    public int UsuarioId { get; set; }
    public string UsuarioNome { get; set; } = "";
    public DateTime DataPedido { get; set; }
    public decimal ValorTotal { get; set; }
    public int TotalArvores { get; set; }
    public string? CodigoDiploma { get; set; }
}

public class UsuarioAdminDto
{
    public int Id { get; set; }
    public string Nome { get; set; } = "";
    public string Email { get; set; } = "";
    public string Perfil { get; set; } = "";
    public DateTime DataCadastro { get; set; }
}

public class ResumoAdminDto
{
    public int TotalUsuarios { get; set; }
    public int TotalPedidos { get; set; }
    public decimal ValorVendido { get; set; }
    public int TotalArvores { get; set; }
}