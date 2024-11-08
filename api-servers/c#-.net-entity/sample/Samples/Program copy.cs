using Cadly.Slicer.API.Authentication;
using Cadly.Slicer.API.Configuration;
using Cadly.Slicer.API.Services;
using Microsoft.AspNetCore.Authentication;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.Configure<ApiVariables>(builder.Configuration.GetSection(ApiVariables.SectionName));

builder.Services.AddAuthentication("ApiKey")
                .AddScheme<AuthenticationSchemeOptions, ApiKeyAuthenticationHandler>("ApiKey", options =>
                {
                    builder.Configuration.GetSection(ApiVariables.SectionName).Bind(options);
                });

builder.Services.AddTransient<ICuraService, CuraService>();
builder.Services.AddTransient<IIoService, IoService>();
builder.Services.AddTransient<IIntegrationService, IntegrationService>();

builder.Services.AddMemoryCache();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddControllers();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors(options => options.AllowAnyOrigin());

app.UseAuthentication();

app.UseAuthorization();

app.MapControllers();

app.Run();
