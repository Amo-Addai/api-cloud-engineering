using System.Text;
using System.Text.Json;
using System.Net.Http;
using System.Net.Http.Headers;
using Cadly.Slicer.API.Configuration;
using Cadly.Slicer.API.Dtos.Projects;
using Microsoft.Extensions.Options;

namespace Cadly.Slicer.API.Services
{
    public interface IIntegrationService
    {
        public Task<ProjectOutputDto?> GetProjectAsync(Guid projectId);
        
        public Task<bool> StartManufacturerReviewAsync(Guid projectId, ProjectManufacturingUpdateInputDto projectManufacturingUpdateInputDto);
        
        public Task<(ProjectFileOutputDto?, string?)> GetProjectFileAsync(Guid projectId, Guid fileId);
    }
    
    public class IntegrationService : IIntegrationService
    {
        private readonly ApiVariables _apiVariables;
        private readonly HttpClient _httpClient;
        
        private readonly ILogger<IntegrationService> _logger;

        public IntegrationService(IOptions<ApiVariables> apiVariables, ILogger<IntegrationService> logger)
        {
            _apiVariables = apiVariables.Value;
            _httpClient = new HttpClient()
            {
                BaseAddress = new Uri(_apiVariables.ApiUrl)
            };
            _logger = logger;
        }
        
        public async Task<ProjectOutputDto?> GetProjectAsync(Guid projectId)
        {
            string? response = await MakeRequestAsync($"/projects/{projectId}", HttpMethod.Get);

            if (response != null)
            {
                ProjectOutputDto? project = JsonSerializer.Deserialize<ProjectOutputDto>(response);
                return project;
            }
            return null;
        }

        public async Task<bool> StartManufacturerReviewAsync(Guid projectId, ProjectManufacturingUpdateInputDto projectManufacturingUpdateInputDto)
        {
            string? content = JsonSerializer.Serialize<ProjectManufacturingUpdateInputDto>(projectManufacturingUpdateInputDto);
            
            string? response = await MakeRequestAsync($"/projects/{projectId}/manufacturer-review", HttpMethod.Patch, content: content);

            if (response != null)
            {
                return response == "True"; // TODO: confirm response content from Private Api
            }
            return false;
        }
        
        public async Task<(ProjectFileOutputDto?, string?)> GetProjectFileAsync(Guid projectId, Guid fileId)
        {
            string? response = await MakeRequestAsync($"/projects/{projectId}/files/{fileId}", HttpMethod.Get, contentType: "text/plain");
            
            if (response != null)
            {
                ProjectFileOutputDto? file = JsonSerializer.Deserialize<ProjectFileOutputDto>(response);

                if (file != null)
                {
                    // TODO: Get file download-url
                    string filePath = await SaveFileAsync(fileId, $"{file.Name}.{file.Extension}");
                
                    return (file, filePath);
                }
            }
            return (null, null);
        }
        
        protected async Task<string> SaveFileAsync(Guid fileId, string fileName, string storagePath = "")
        {
            Dictionary<string, string> headers = new Dictionary<string, string>
            {
                { "API-Key", _apiVariables.ApiKey }
            };
            
            Action<HttpResponseMessage> saveFile = async response => // todo: ensure test-coverage
            {
                _logger.LogInformation(response.ToString());
                
                // Store file locally
                storagePath = $"{storagePath}/{fileName}";
                
                using (var fileStream = new FileStream(storagePath, FileMode.Create, FileAccess.Write, FileShare.None))
                using (var responseStream = await response.Content.ReadAsStreamAsync())
                {
                    // Copy the response stream directly to the file stream
                    await responseStream.CopyToAsync(fileStream);
                }
            };

            await MakeFileRequestAsync($"/files/{fileId}", HttpMethod.Get, headers: headers, callback: saveFile);
            
            return storagePath;
        }
        
        protected async Task<string?> MakeRequestAsync(
            string url, HttpMethod method,
            Dictionary<string, string>? headers = null,
            string contentType = "application/json",
            string? content = null
        )
        {
            string? result = null;

            _httpClient.DefaultRequestHeaders.Accept.Clear();
            _httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue(contentType));
        
            var request = new HttpRequestMessage(method, url);

            request.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue(contentType));
        
            if (headers != null)
            {
                headers.ToList().ForEach(h => request.Headers.Add(h.Key, h.Value));
            }

            if (content != null)
            {
                request.Content = new StringContent(content, Encoding.UTF8, contentType);
            }
            
            HttpResponseMessage response = await _httpClient.SendAsync(request);
            
            if (response != null && response.IsSuccessStatusCode)
            {
                result = await response.Content.ReadAsStringAsync();
            }
            return result;
        }

        protected async Task<bool> MakeFileRequestAsync(
            string url, HttpMethod method,
            Dictionary<string, string>? headers = null,
            string contentType = "multipart/*", // todo: file-stream
            (MultipartContent? multipart, MultipartFormDataContent? formData)? content = null,
            Action<HttpResponseMessage>? callback = null
        )
        {
            bool result = false;

            _httpClient.DefaultRequestHeaders.Accept.Clear();
            _httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue(contentType));

            var request = new HttpRequestMessage(method, url);

            request.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue(contentType));

            if (headers != null)
            {
                headers.ToList().ForEach(h => request.Headers.Add(h.Key, h.Value));
            }

            if (content != null)
            {
                request.Content = String.Equals(contentType, "multipart/form-data")
                    ? content.Value.formData
                    : content.Value.multipart;
            }
            
            HttpResponseMessage response =
                await _httpClient.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
            
            if (response != null && response.IsSuccessStatusCode)
            {
                callback?.Invoke(response);
                result = true;
            }
            return result;
        }
    }
}
