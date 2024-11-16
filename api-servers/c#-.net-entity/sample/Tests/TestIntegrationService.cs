using Sample.Slicer.API.Configuration;
using Sample.Slicer.API.Services;
using Microsoft.Extensions.Options;

namespace Sample.Slicer.API.Tests.Services
{
    public class TestIntegrationService : IntegrationService
    {
        public TestIntegrationService(IOptions<ApiVariables> apiVariables) // HttpClient httpClient
            : base(apiVariables)
        {
            // You can initialize the base class with necessary dependencies
        }
    
        public Task<bool> SaveFileAsync(string fileUrl, string fileName, string storagePath = "")
        {
            return base.SaveFileAsync(fileUrl, fileName, storagePath);
        }
    
        public Task<string?> MakeRequestAsync(string url, HttpMethod method, Dictionary<string, string>? headers = null,
            string contentType = "application/json", string? content = null)
        {
            return base.MakeRequestAsync(url, method, headers, contentType, content);
        }
    
        public Task<bool> MakeFileRequestAsync(string url, HttpMethod method, string fileName,
            Dictionary<string, string>? headers = null,
            string contentType = "multipart/*", // todo: file-stream
            string? content = null, Action<HttpResponseMessage>? callback = null)
        {
            return base.MakeFileRequestAsync(url, method, fileName, headers, contentType, content, callback);
        }
    }
}