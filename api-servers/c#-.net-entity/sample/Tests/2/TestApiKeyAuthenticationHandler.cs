using System.Text.Encodings.Web;
using Cadly.Slicer.API.Configuration;
using Cadly.Slicer.API.Authentication;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;
using Microsoft.Extensions.Logging;

namespace Cadly.Slicer.API.Tests.Authentication
{
    public class TestApiKeyAuthenticationHandler : ApiKeyAuthenticationHandler
    {
        public TestApiKeyAuthenticationHandler(
            IOptionsMonitor<AuthenticationSchemeOptions> options,
            ILoggerFactory logger,
            UrlEncoder encoder,
            ISystemClock clock,
            IOptions<ApiVariables> config
        )
            : base(options, logger, encoder, clock, config)
        {
            // You can initialize the base class with necessary dependencies
        }

        public new Task<AuthenticateResult> TestHandleAuthenticateAsync()
        {
            return base.HandleAuthenticateAsync();
        }
    }
}