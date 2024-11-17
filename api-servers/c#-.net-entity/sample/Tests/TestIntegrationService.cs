using Cadly.Slicer.API.Configuration;
using Cadly.Slicer.API.Services;
using Microsoft.Extensions.Options;

namespace Cadly.Slicer.API.Tests.Services
{
    public class TestIntegrationService : IntegrationService
    {
        public TestIntegrationService(IOptions<ApiVariables> apiVariables)
            : base(apiVariables)
        {
            // You can initialize the base class with necessary dependencies
        }
    
        public new Task<bool> SaveFileAsync(string fileUrl, string fileName, string storagePath = "")
        {
            return base.SaveFileAsync(fileUrl, fileName, storagePath);
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
			string url, HttpMethod method, string fileName,
            Dictionary<string, string>? headers = null,
            string contentType = "multipart/*",
            (MultipartContent? multipart, MultipartFormDataContent? formData)? content = null,
			Action<HttpResponseMessage>? callback = null
		)
        {
            return base.MakeFileRequestAsync(url, method, fileName, headers, contentType, content, callback);
        }
    }
}