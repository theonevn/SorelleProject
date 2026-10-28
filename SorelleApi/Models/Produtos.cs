namespace SorelleApi.Models;

public class Produto
{
    public int Id { get; set; }
    public string Nome { get; set; } = "";
    public string? Descricao { get; set; }
    public decimal Preco { get; set; }
    public int Arvores { get; set; }
    public string? Tag { get; set; }
}