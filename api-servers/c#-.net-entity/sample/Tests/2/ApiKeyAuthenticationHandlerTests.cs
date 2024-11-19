using Xunit;
using System.Text.Encodings.Web;
using Cadly.Slicer.API.Configuration;
using Cadly.Slicer.API.Authentication;
using Moq;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;
using Microsoft.Extensions.Logging;

namespace Cadly.Slicer.API.Tests.Authentication
{
    public class ApiKeyAuthenticationHandlerTests
    {
        private const string ApiKeyHeaderName = "API-Key";
        private const string ValidApiKey = "test-api-key";
        
        private readonly AuthenticationScheme _scheme;
        private readonly DefaultHttpContext _defaultContext;
        
        private TestApiKeyAuthenticationHandler _handler;

        public ApiKeyAuthenticationHandlerTests()
        {
            var optionsMonitorMock = new Mock<IOptionsMonitor<AuthenticationSchemeOptions>>();
            var loggerMock = new Mock<ILoggerFactory>();
            var encoderMock = new Mock<UrlEncoder>();
            var clockMock = new Mock<ISystemClock>();
            var apiConfigurationVariables = new Mock<IOptions<ApiVariables>>();

            // Configure the ApiVariables mock to return the expected API key
            apiConfigurationVariables.Setup(config => config.Value)
                .Returns(new ApiVariables { ApiKey = ValidApiKey, ApiUrl = "not required for tests" });
            
            _handler = new TestApiKeyAuthenticationHandler(
                optionsMonitorMock.Object,
                loggerMock.Object,
                UrlEncoder.Default, // encoderMock.Object,
                clockMock.Object,
                apiConfigurationVariables.Object);
            
			// Create an Authentication Scheme for Authentication handler
            _scheme = new AuthenticationScheme(
				"ApiKey", "ApiKey", 
				typeof(TestApiKeyAuthenticationHandler)
			);
			
            // Create an HttpContext with the required API key header
            _defaultContext = new DefaultHttpContext();
            _defaultContext.Request.Headers[ApiKeyHeaderName] = ValidApiKey;
        }

		[Fact]
		public void TestApiKeyAuthenticationHandler_Instantiated()
		{
			Assert.NotNull(_scheme);
			Assert.NotNull(_defaultContext);
			Assert.Equal(_scheme.Name, "ApiKey");
			Assert.Equal(_scheme.DisplayName, "ApiKey");
			Assert.Equal(_defaultContext.Request.Headers[ApiKeyHeaderName], ValidApiKey);
            Assert.IsType<TestApiKeyAuthenticationHandler>(_handler);
		}
        
        [Fact]
        public async Task TestHandleAuthenticateAsync_ShouldReturn_AuthenticateSuccess()
        {
            // Assign the context to the handler
            _handler.InitializeAsync(_scheme, _defaultContext);
            
            // Act
            var result = await _handler.TestHandleAuthenticateAsync();
        
            // Assert: Correct type
            Assert.IsType<AuthenticateResult>(result);

            // Assert
            Assert.True(result.Succeeded);
            Assert.NotNull(result.Principal);
            Assert.Equal("APIKeyUser", result.Principal.Identity?.Name);
        }
        
        [Fact]
        public async Task TestHandleAuthenticateAsync_ShouldReturn_AuthenticateSuccess_WithTicket()
        {
            // Assign the context to the handler
            _handler.InitializeAsync(_scheme, _defaultContext);
            
            // Act
            var result = await _handler.TestHandleAuthenticateAsync();
            
            // Assert
            Assert.True(result.Succeeded);
            Assert.NotNull(result.Ticket); // Ensure ticket is not null
            Assert.Equal("APIKeyUser", result.Ticket?.Principal.Identity?.Name); // Check principal name

            // Additional checks on ticket
            var ticket = result.Ticket;
            Assert.NotNull(ticket);
            Assert.Equal("ApiKey", ticket?.AuthenticationScheme);
            Assert.NotNull(ticket?.Principal);
        }
        
        [Fact]
        public async Task TestHandleAuthenticateAsync_ShouldReturnSuccess_WhenApiKeyIsValid()
        {
            // Create an HttpContext with the required API key header
            var context = new DefaultHttpContext();
            context.Request.Headers[ApiKeyHeaderName] = ValidApiKey;
            
            // Assign the context to the handler
            _handler.InitializeAsync(_scheme, context);
            
            // Act
            var result = await _handler.TestHandleAuthenticateAsync();

            // Assert
            Assert.True(result.Succeeded);
            Assert.NotNull(result.Principal);
            Assert.Equal("APIKeyUser", result.Principal.Identity?.Name);
        }

        [Fact]
        public async Task TestHandleAuthenticateAsync_ShouldReturnFail_WhenApiKeyIsInvalid()
        {
            // Create an HttpContext with an invalid API key
            var context = new DefaultHttpContext();
            context.Request.Headers[ApiKeyHeaderName] = "invalid-api-key";
            
            // Assign the context to the handler
            _handler.InitializeAsync(_scheme, context);

            // Act
            var result = await _handler.TestHandleAuthenticateAsync();

            // Assert
            Assert.False(result.Succeeded);
            Assert.Equal("Invalid API Key", result.Failure?.Message);
        }

        [Fact]
        public async Task TestHandleAuthenticateAsync_ShouldReturnFail_WhenApiKeyHeaderIsMissing()
        {
            // Create an HttpContext without the API key header
            var context = new DefaultHttpContext();
            
            // Assign the context to the handler
            _handler.InitializeAsync(_scheme, context);

            // Act
            var result = await _handler.TestHandleAuthenticateAsync();

            // Assert
            Assert.False(result.Succeeded);
            Assert.Equal("API-Key header not found", result.Failure?.Message);
        }
    }
}

