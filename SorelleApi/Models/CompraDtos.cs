namespace SorelleApi.Models;

// O que o app envia
public record ItemCompraRequest(int ProdutoId, int Quantidade);

public record CompraRequest(string NomeCertificado, List<ItemCompraRequest> Itens);

// O que a API devolve (classes comuns: o Dapper preenche as propriedades)
public class CompraResponse
{
    public int PedidoId { get; set; }
    public string CodigoDiploma { get; set; } = "";
    public int TotalArvores { get; set; }
    public decimal ValorTotal { get; set; }
}

public class CertificadoDto
{
    public string NomeCertificado { get; set; } = "";
    public string CodigoDiploma { get; set; } = "";
    public int TotalArvoresPlantadas { get; set; }
    public DateTime DataEmissao { get; set; }
}

public class ImpactoResponse
{
    public int TotalArvores { get; set; }
    public int Co2RetidoKg { get; set; }
    public string Nivel { get; set; } = "";
    public List<CertificadoDto> Certificados { get; set; } = new();
}