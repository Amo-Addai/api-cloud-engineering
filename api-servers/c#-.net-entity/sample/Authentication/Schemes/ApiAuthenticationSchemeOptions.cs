using Microsoft.AspNetCore.Authentication;

namespace Cadly.Slicer.API.Authentication.Schemes;

public class ApiAuthenticationSchemeOptions : AuthenticationSchemeOptions
{
    public string ApiKey { get; set; }
}
