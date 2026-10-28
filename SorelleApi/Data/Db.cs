    using Microsoft.Data.SqlClient;

namespace SorelleApi.Data;

public class Db
{
    private readonly string _connectionString;

    public Db(IConfiguration config)
    {
        _connectionString = config.GetConnectionString("SorelleDb")
            ?? throw new InvalidOperationException("Connection string 'SorelleDb' não encontrada.");
    }

    public SqlConnection CreateConnection() => new SqlConnection(_connectionString);
}