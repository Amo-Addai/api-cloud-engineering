using Cadly.Slicer.API.Authentication;
using Cadly.Slicer.API.Configuration;
using Cadly.Slicer.API.Services;
using Microsoft.AspNetCore.Authentication;

namespace Cadly.Slicer.API;

public class Startup
{
    public Startup(IConfiguration configuration)
    {
        Configuration = configuration;
    }

    public IConfiguration Configuration { get; }

    // ConfigureServices is where you register application services
    public void ConfigureServices(IServiceCollection services)
    {
        // Add services to the container.

        services.Configure<ApiVariables>(Configuration.GetSection(ApiVariables.SectionName));

        services.AddAuthentication("ApiKey")
            .AddScheme<AuthenticationSchemeOptions, ApiKeyAuthenticationHandler>("ApiKey", options =>
            {
                Configuration.GetSection(ApiVariables.SectionName).Bind(options);
            });

        services.AddTransient<ICuraService, CuraService>();
        services.AddTransient<IIoService, IoService>();
        services.AddTransient<IIntegrationService, IntegrationService>();

        services.AddMemoryCache();

        // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
        services.AddEndpointsApiExplorer();
        services.AddSwaggerGen();

        services.AddControllers();
    }

    // Configure is where you define the request pipeline
    public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
    {
        // Configure the HTTP request pipeline.
        if (env.IsDevelopment())
        {
            app.UseSwagger();
            app.UseSwaggerUI();
        }

        app.UseCors(options => options.AllowAnyOrigin());

        app.UseAuthentication();

        app.UseAuthorization();

        app.UseEndpoints(endpoints =>
        {
            endpoints.MapControllers(); // Map attribute-routed controllers
        });
    }
}
