using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using SorelleApi.Data;
using SorelleApi.Services;

Dapper.DefaultTypeMap.MatchNamesWithUnderscores = true;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddSingleton<Db>();
builder.Services.AddScoped<ProdutoRepository>();
builder.Services.AddScoped<UsuarioRepository>();
builder.Services.AddScoped<PedidoRepository>();
builder.Services.AddScoped<AdminRepository>();
builder.Services.AddSingleton<TokenService>();

// CORS: só estes endereços (os do site) podem chamar a API pelo navegador
builder.Services.AddCors(options =>
{
    options.AddPolicy("Site", policy => policy
        .WithOrigins(
            "http://localhost:5173",
            "http://localhost:3000",
            "https://sorellems.netlify.app")
        .AllowAnyHeader()
        .AllowAnyMethod());
});

var jwt = builder.Configuration.GetSection("Jwt");

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.MapInboundClaims = false;
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = jwt["Issuer"],
            ValidAudience = jwt["Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwt["Key"]!)),
            RoleClaimType = "role"
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

app.UseCors("Site");
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

app.Run();