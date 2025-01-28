using System.Text.Encodings.Web;
using Company.Slicer.API.Configuration;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;

namespace Company.Slicer.API.Authentication;

public class ApiKeyAuthenticationHandler : AuthenticationHandler<AuthenticationSchemeOptions>
{
    private const string ApiKeyHeaderName = "API-Key";
    
    private readonly ApiVariables _apiConfigurationVariables;
    
    public ApiKeyAuthenticationHandler(IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder, ISystemClock clock, IOptions<ApiVariables> apiConfigurationVariables)
        : base(options, logger, encoder, clock)
    {
        _apiConfigurationVariables = apiConfigurationVariables.Value;
    }
    
    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        return await Task.Run(() => 
        {
            if (!Request.Headers.TryGetValue(ApiKeyHeaderName, out var apiKeyHeaderValues))
            {
                return AuthenticateResult.Fail(("API-Key header not found"));
            }

            string providedApiKey = apiKeyHeaderValues.ToString();
            string? expectedApiKey = _apiConfigurationVariables.ApiKey;

            if (string.IsNullOrEmpty(expectedApiKey)
                || !string.Equals(providedApiKey, expectedApiKey))
            {
                return AuthenticateResult.Fail(("Invalid API Key"));
            }

            // Create a principal and ticket if the API key is valid
            var identity = new System.Security.Principal.GenericIdentity("APIKeyUser", "APIKey");
            var principal = new System.Security.Principal.GenericPrincipal(identity, null);
            
            var ticket = new AuthenticationTicket(principal, Scheme.Name);
        
            return AuthenticateResult.Success(ticket);
        });
    }
}