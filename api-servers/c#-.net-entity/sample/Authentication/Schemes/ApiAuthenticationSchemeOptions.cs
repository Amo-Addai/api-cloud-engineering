using Microsoft.AspNetCore.Authentication;

namespace Sample.Slicer.API.Authentication.Schemes;

public class ApiAuthenticationSchemeOptions : AuthenticationSchemeOptions
{
    public string ApiKey { get; set; }
}
