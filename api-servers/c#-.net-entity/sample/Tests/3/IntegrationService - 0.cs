using System.Text;
using System.Text.Json;
using System.Net.Http;
using System.Net.Http.Headers;
using Company.Slicer.API.Configuration;
using Company.Slicer.API.Dtos.Projects;
using Microsoft.Extensions.Options;

namespace Company.Slicer.API.Services
{
    public interface IIntegrationService
    {
        public Task<ProjectOutputDto?> GetProjectAsync(string projectId);
        
        public Task<bool> StartManufacturerReviewAsync(string projectId, ProjectManufacturingUpdateInputDto projectManufacturingUpdateInputDto);
        
        public Task<ProjectFileOutputDto?> GetProjectFileAsync(string projectId, string fileId);
    }
    
    public class IntegrationService : IIntegrationService
    {
        private readonly ApiVariables _apiVariables;
        private readonly HttpClient _httpClient;

        public IntegrationService(IOptions<ApiVariables> apiVariables)
        {
            _apiVariables = apiVariables.Value;
            _httpClient = new HttpClient()
            {
                BaseAddress = new Uri(_apiVariables.ApiUrl)
            };
        }
        
        public async Task<ProjectOutputDto?> GetProjectAsync(string projectId)
        {
            try // TODO: remove try-block & thrown Exceptions after debugging - backup 1st
            {

                string? response = await MakeRequestAsync($"/projects/{projectId}", HttpMethod.Get);

                Console.WriteLine(response); // todo: remove

                if (response != null)
                {
                    ProjectOutputDto? project = JsonSerializer.Deserialize<ProjectOutputDto>(response);
                    Console.WriteLine(project?.ToString());
                    return project;
                }
                else throw new Exception("No Response");

            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);

                Array.ForEach(
                    new string[] { projectId },
                    Console.WriteLine
                );
            }
            return null;
        }

        public async Task<bool> StartManufacturerReviewAsync(string projectId, ProjectManufacturingUpdateInputDto projectManufacturingUpdateInputDto)
        {
            try // todo: remove
            {

                string? content = JsonSerializer.Serialize<ProjectManufacturingUpdateInputDto>(projectManufacturingUpdateInputDto);
                
                Console.WriteLine(content); // todo: remove

                string? response = await MakeRequestAsync($"/projects/{projectId}/manufacturer-review", HttpMethod.Patch, content: content);

                Console.WriteLine(response); // todo: remove

                if (response != null)
                {
                    return response == "True"; // TODO: confirm response content from Private Api
                }
                else throw new Exception("No Response");

            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);

                Array.ForEach(
                    new string[] { projectId, JsonSerializer.Serialize<ProjectManufacturingUpdateInputDto>(projectManufacturingUpdateInputDto) },
                    Console.WriteLine
                );
            }
            return false;
        }
        
        public async Task<ProjectFileOutputDto?> GetProjectFileAsync(string projectId, string fileId)
        {
            try // todo: remove
            {

                string? response = await MakeRequestAsync($"/projects/{projectId}/files/{fileId}", HttpMethod.Get, contentType: "text/plain"); // todo:
                
                Console.WriteLine(response); // todo: remove
                
                if (response != null)
                {
                    ProjectFileOutputDto? file = JsonSerializer.Deserialize<ProjectFileOutputDto>(response);

                    Console.WriteLine(file?.ToString()); // todo: remove
                    
                    if (file != null)
                    {
                        // TODO: Get file download-url
                        _ = SaveFileAsync("", $"{file.Name}.{file.Extension}");
                    
                        return file;
                    }
                    else throw new Exception($"No Project file output: {file.ToString()}");

                }
                else throw new Exception("No Response");
                
            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);

                Array.ForEach(
                    new string[] { projectId, fileId },
                    Console.WriteLine
                );
            }
            return null;
        }
        
        protected async Task<bool> SaveFileAsync(string fileUrl, string fileName, string storagePath = "")
        {
            try // todo: remove
            {

                Dictionary<string, string> headers = new Dictionary<string, string>
                {
                    { "API-Key", _apiVariables.ApiKey }
                };
                
                Action<HttpResponseMessage> saveFile = async response => // todo: ensure test-coverage
                {
                    // Store file locally
                    string filePath = $"{storagePath}/{fileName}";
                    
                    using (var fileStream = new FileStream(filePath, FileMode.Create, FileAccess.Write, FileShare.None))
                    using (var responseStream = await response.Content.ReadAsStreamAsync())
                    {
                        // Copy the response stream directly to the file stream
                        await responseStream.CopyToAsync(fileStream);
                    }
                };
                
                return await MakeFileRequestAsync(fileUrl, HttpMethod.Get, fileName, headers: headers, callback: saveFile);

            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);

                Array.ForEach(
                    new string[] { fileUrl, fileName, storagePath },
                    Console.WriteLine
                );
            }
            return false;
        }
        
        protected async Task<string?> MakeRequestAsync(
            string url, HttpMethod method,
            Dictionary<string, string>? headers = null,
            string contentType = "application/json",
            string? content = null
        )
        {
            string? result = null;

            try // todo: remove try-block after debug
            {
                
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
                
                Console.WriteLine($"Request - {request.ToString()}"); // todo: remove

                HttpResponseMessage response = await _httpClient.SendAsync(request);
                
                Console.WriteLine($"Response - {response.ToString()}"); // todo: remove

                if (response != null && response.IsSuccessStatusCode)
                {
                    result = await response.Content.ReadAsStringAsync();
                    Console.WriteLine(result);
                }
                else throw new Exception(response?.ReasonPhrase ?? "No Response");

            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);

                Array.ForEach(
                    new string[] {
                        url, method.ToString(),
                        headers != null 
                        ? string.Join(", ", headers.Select(h => $"{h.Key}: {h.Value}"))
                        : "",
                        contentType, content ?? ""
                    },
                    Console.WriteLine
                );
            }
            
            return result;
        }

        protected async Task<bool> MakeFileRequestAsync(
            string url, HttpMethod method, string fileName,
            Dictionary<string, string>? headers = null,
            string contentType = "multipart/*", // todo: file-stream
            (MultipartContent? multipart, MultipartFormDataContent? formData)? content = null,
            Action<HttpResponseMessage>? callback = null
        )
        {
            bool result = false;

            try // todo: remove
            {

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
                
                Console.WriteLine($"Request - {request.ToString()}"); // todo: remove

                HttpResponseMessage response =
                    await _httpClient.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
                
                Console.WriteLine($"Response - {response.ToString()}"); // todo: done *
                
                if (response != null && response.IsSuccessStatusCode)
                {
                    callback?.Invoke(response);
                    result = true;
                }
                else throw new Exception(response?.ReasonPhrase ?? "No Response");

            }
            catch (Exception exception)
            {
                Console.WriteLine(exception);

                Array.ForEach(
                    new string[] {
                        url, method.ToString(), fileName,
                        headers != null 
                        ? string.Join(", ", headers.Select(h => $"{h.Key}: {h.Value}"))
                        : "",
                        contentType,
                        content != null
                        ? content.Value.multipart?.ToString() ?? ""
                        : "",
                        content != null
                        ? content.Value.formData?.ToString() ?? ""
                        : ""
                    },
                    Console.WriteLine
                );
            }
            
            return result;
        }
    }
}
