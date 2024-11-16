using Sample.Slicer.API.Authentication;
using Sample.Slicer.API.Authentication.Schemes;
using Sample.Slicer.API.Configuration;
using Sample.Slicer.API.Services;
using Microsoft.AspNetCore.Authentication;
using NuGet.Protocol;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// * for IOptionsMonitor<CustomAuthenticationSchemeOptions> or IOptions<ApiVariables> to be used in ApiKeyAuthenticationHandler, 
// config-vars must be bound to services 1st (with either straight-binding or manual options.Prop setting)

builder.Services.ConfigureEnvironmentVariables(builder.Configuration);

// * or, configure single section as ApiVariables
// const string PrivateApiSectionName = "PrivateApi";
// builder.Services.Configure<ApiVariables>(builder.Configuration.GetSection(PrivateApiSectionName));

// todo: can also use custom Auth-Schemes without default AuthenticationSchemeOptions with binding authentication config-vars (for 'Options' auth-scheme prop, in ApiKeyAuthenticationHandler) 

builder.Services.AddAuthentication("ApiKey")
                // .AddScheme<AuthenticationSchemeOptions, ApiKeyAuthenticationHandler>("ApiKey", options =>
                .AddScheme<ApiAuthenticationSchemeOptions, ApiKeyAuthenticationHandler>("ApiKey", options =>
                { // todo: this callback is exec'd only once on 1st service-env-var call, so options.Prop can still be accessed by CustomAuthSchemeOptions implementer

                    const string PrivateApiSectionName = "PrivateApi";

                    Console.WriteLine(builder.Configuration.GetSection(PrivateApiSectionName).GetSection("ApiKey").ToJson());
                    Console.WriteLine(builder.Configuration.GetSection(PrivateApiSectionName).GetSection("ApiKey").Value);

                    // * both binding & manual options available

                    // Bind configuration section PrivateApiSectionName directly to options
                    // can work with both Env-AuthVars & Custom Auth-Scheme; doesn't require custom Auth-Scheme for Options.Props (no options.Prop setter)
                    builder.Configuration.GetSection(PrivateApiSectionName).Bind(options); // todo: straight-binding with Custom Auth-Scheme - best auth-strategy
                    
                    // * Manually Inject ApiVariables configuration
                    // can only work with Custom Auth-Scheme - requires custom Auth-Scheme for Options.Props (options.ApiKey)
                    // var apiVariables = builder.Configuration.GetSection(PrivateApiSectionName).Get<ApiVariables>();
                    // options.ApiKey = apiVariables?.ApiKey;
                    
                });

builder.Services.AddTransient<ICuraService, CuraService>();
builder.Services.AddTransient<IIoService, IoService>();

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
