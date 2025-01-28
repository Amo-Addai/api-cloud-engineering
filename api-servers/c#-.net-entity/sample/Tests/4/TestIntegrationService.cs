using Company.Slicer.API.Configuration;
using Company.Slicer.API.Services;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;

namespace Company.Slicer.API.Tests.Services
{
    public class TestIntegrationService : IntegrationService
    {
        public TestIntegrationService(IOptions<ApiVariables> apiVariables, ILogger<IntegrationService> logger)
            : base(apiVariables, logger)
        {
            // You can initialize the base class with necessary dependencies
        }
    
        public new Task<string> SaveFileAsync(Guid fileId, string fileName, string storagePath = "")
        {
            return base.SaveFileAsync(fileId, fileName, storagePath);
        }
    
        public new Task<string?> MakeRequestAsync(
			string url, HttpMethod method,
			Dictionary<string, string>? headers = null,
            string contentType = "application/json",
			string? content = null
		)
        {
            return base.MakeRequestAsync(url, method, headers, contentType, content);
        }
    
        public new Task<bool> MakeFileRequestAsync(
			string url, HttpMethod method,
            Dictionary<string, string>? headers = null,
            string contentType = "multipart/*",
            (MultipartContent? multipart, MultipartFormDataContent? formData)? content = null,
			Action<HttpResponseMessage>? callback = null
		)
        {
            return base.MakeFileRequestAsync(url, method, headers, contentType, content, callback);
        }
    }
}