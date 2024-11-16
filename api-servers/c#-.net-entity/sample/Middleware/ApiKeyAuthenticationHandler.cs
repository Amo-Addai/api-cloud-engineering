using System.Text.Encodings.Web;
using Sample.Slicer.API.Configuration;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;

namespace Sample.Slicer.API.Middlewares;

public class ApiKeyAuthenticationHandler
    : AuthenticationHandler<AuthenticationSchemeOptions>
{
    private const string ApiKeyHeaderName = "API-Key";
    private const string ApiKeyEnvName = "API_KEY";
    
    private readonly ApiVariables ApiConfigurationVariables;
    
    public ApiKeyAuthenticationHandler(IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder, ISystemClock clock, IOptionsMonitor<ApiVariables> apiConfigurationVariables)
        : base(options, logger, encoder, clock)
    {
        ApiConfigurationVariables = apiConfigurationVariables.CurrentValue;
    }

    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        if (!Request.Headers.TryGetValue(ApiKeyHeaderName, out var apiKeyHeaderValues))
        {
            return AuthenticateResult.Fail(("API-Key header not found"));
        }

        string providedApiKey = apiKeyHeaderValues.ToString();
        string? expectedApiKey = ApiConfigurationVariables.ApiKey ?? Environment.GetEnvironmentVariable(ApiKeyEnvName);

        if (string.IsNullOrEmpty(expectedApiKey)
            || !string.Equals(providedApiKey, expectedApiKey))
        {
            return AuthenticateResult.Fail(("Invalid API Key"));
        }

        var identity = new System.Security.Principal.GenericIdentity("APIKeyUser", "APIKey");
        var principal = new System.Security.Principal.GenericPrincipal(identity, null);
        /* todo: 
        var identity1 = new System.Security.Claims.ClaimsIdentity("APIKey");
        var principal1 = new System.Security.Claims.ClaimsPrincipal(identity1);
        */
        
        var ticket = new AuthenticationTicket(principal, Scheme.Name);
        return AuthenticateResult.Success(ticket);
    }
}