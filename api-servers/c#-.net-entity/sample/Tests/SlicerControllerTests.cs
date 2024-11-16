using Sample.Slicer.API.Dtos;
using Sample.Slicer.API.Services;
using Sample.Slicer.API.Controllers;
using Xunit;
using Moq;
using System.Text;
using System.Net;
using System.Net.Http.Headers;
using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.DependencyInjection;


// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Sample.Slicer.API.Tests.Controllers
{
    public class SlicerControllerTests : IClassFixture<WebApplicationFactory<Program>>
    {
        private readonly HttpClient _httpClient;

        public SlicerControllerTests(WebApplicationFactory<Program> factory)
        {
            // Configure the factory to use mock injected services
            factory.WithWebHostBuilder(builder =>
            {
                builder.ConfigureServices(services =>
                {
                    // Remove the existing service registrations (if any)
                    Type[] serviceTypes = new[]
                    {
                        typeof(ICuraService),
                        typeof(IIoService),
                        typeof(ILogger),
                        typeof(IMemoryCache)
                    };
                    
                    serviceTypes.ToList().ForEach(type =>
                    {
                        var descriptor = services.SingleOrDefault(d => d.ServiceType == type);
                        if (descriptor != null)
                        {
                            services.Remove(descriptor);
                        }
                    });
                    
                    // Set up new Mock Services
                    var _curaServiceMock = new Mock<ICuraService>();
                    var _ioServiceMock = new Mock<IIoService>();
                    var loggerMock = new Mock<ILogger>();
                    var memoryCacheMock = new Mock<IMemoryCache>();
                    
            		// string returnedPath = string.Empty; // Variable to capture the actual path returned by _ioServiceMock.SaveFileAsync when called
            		Task<string>? returnedPathTask = null; // Variable to capture the actual path returned by _ioServiceMock.SaveFileAsync when called

                    // Setup IoService mock service method to capture returned value
                    _ioServiceMock.Setup(s => s.SaveFileAsync(It.IsAny<IFormFile>()))
                        // .Callback<IFormFile>(async file =>
                        .Callback<IFormFile>(file =>
                        {
                            // Call the actual SaveFileAsync implementation to get the actual path.
                            // returnedPath = s.SaveFileAsync(file);
                            // returnedPath = await _ioServiceMock.Object.SaveFileAsync(file).Result;
                            // returnedPath = _ioServiceMock.Object.SaveFileAsync(file).Result;
                            returnedPathTask = _ioServiceMock.Object.SaveFileAsync(file);
                        })
                        // .Returns(() => Task.FromResult(returnedPath)); // Return the captured path value
                        // .Returns(() => returnedPath); // Return the captured path value
                        .Returns(async () => await returnedPathTask!); // Return the captured path value
			
                    // Replace real services with mocks
                    services.AddSingleton(_curaServiceMock.Object);
                    services.AddSingleton(_ioServiceMock.Object);
                    services.AddSingleton(loggerMock.Object);
                    services.AddSingleton(memoryCacheMock.Object);
                });
            });
            
            // Create a test client to make Http requests to the api
            _httpClient = factory.CreateClient();
        }
        
        [Fact]
        public async Task PostResources_ShouldReturnOk()
        {
            var _curaServiceMock = new Mock<ICuraService>();
            var _ioServiceMock = new Mock<IIoService>();
            var loggerMock = new Mock<ILogger<SlicerController>>();
            var memoryCacheMock = new Mock<IMemoryCache>();
            
            var controller = new SlicerController(
                _curaServiceMock.Object,
                _ioServiceMock.Object,
                loggerMock.Object,
                memoryCacheMock.Object);
            
            Assert.IsType<SlicerController>(controller);
            
            // string? returnedPath = null; // string.Empty; // Variable to capture the actual path returned by _ioServiceMock.SaveFileAsync when called
            Task<string>? returnedPathTask = null; // string.Empty; // Variable to capture the actual path returned by _ioServiceMock.SaveFileAsync when called

            // Setup IoService mock service method to capture returned value
            _ioServiceMock.Setup(s => s.SaveFileAsync(It.IsAny<IFormFile>()))
                // .Callback<IFormFile>(async file =>
                .Callback<IFormFile>(file =>
                {
                    // Call the actual SaveFileAsync implementation to get the actual path.
                    // returnedPath = s.SaveFileAsync(file);
                    // returnedPath = await _ioServiceMock.Object.SaveFileAsync(file).Result;
                    // returnedPath = _ioServiceMock.Object.SaveFileAsync(file).Result;
                    returnedPathTask = _ioServiceMock.Object.SaveFileAsync(file);
                })
                // .Returns(() => Task.FromResult(returnedPath)); // Return the captured path value
                // .Returns(() => returnedPath); // Return the captured path value
                .Returns(() => returnedPathTask!); // Return the captured path value
			
			string returnedPath = await returnedPathTask!;
            
            // Arrange request-handler mock data
            var mockFile = new Mock<IFormFile>();
            var content = "This is a test file.";
            var fileName = "test.txt";
            var stream = new MemoryStream(Encoding.UTF8.GetBytes(content));

            mockFile.Setup(f => f.OpenReadStream()).Returns(stream);
            mockFile.Setup(f => f.FileName).Returns(fileName);
            mockFile.Setup(f => f.Length).Returns(stream.Length);
            mockFile.Setup(f => f.ContentType).Returns("text/plain");

            double layerHeight = 0.2;
            double infill = 20.0;
            
            // Act
            var result = await controller.Resources(mockFile.Object, layerHeight, infill);

			// Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            
            const double expectedPrice = 0.0;
            
            // const PostResources_ResponseDto expectedResponse = new PostResources_ResponseDto { Prop = expectedPrice };
            var expectedResponse = new { Prop = expectedPrice };
            
            Assert.Equal(expectedResponse, okResult.Value);
            // Assert.Equal(expectedPrice, okResult.Value?.Prop);

            // Verify that the service's SaveFileAsync method was called with expected arguments
            _ioServiceMock.Verify(s => s.SaveFileAsync(mockFile.Object), Times.Once);
            
            // Verify that the service's SliceAsync method was called with expected arguments
            _curaServiceMock.Verify(s => s.SliceAsync(returnedPath, layerHeight, infill), Times.Once);
            
            // Verify that the service's DeleteFileAsync method was called with expected arguments
            _ioServiceMock.Verify(s => s.DeleteFileAsync(returnedPath), Times.Once);
        }

        [Fact]
        public async Task Post_ToRoute_Resources_ShouldReturnSuccessStatusCode_AndValidJsonResponse()
        {
            // Arrange Multipart FormData mock data
            var content = new MultipartFormDataContent();
            
            // Mock an IFormFile as part of the request content
            var fileContent = new ByteArrayContent(new byte[] { 1, 2, 3 });
            fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/octet-stream");
            content.Add(fileContent, "file", "test.stl");
            
            // Add other form data
            content.Add(new StringContent("0.2"), "layer_height");
            content.Add(new StringContent("20"), "infill");
            
            // Act
            var response = await _httpClient.PostAsync("slicer/resources", content);
            
            // Assert response
            Assert.Equal(HttpStatusCode.OK, response.StatusCode);
            
            // Assert response content
            var responseBody = await response.Content.ReadAsStringAsync();

            const double expectedPrice = 0.0;
            
            var responseJson = JsonSerializer.Deserialize<PostResources_ResponseDto>(
                responseBody,
                new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                });
            
            Assert.NotNull(responseJson);
            Assert.Equal(expectedPrice, responseJson.Prop);
        }

        // Sample response Dto // todo: move out of scope
        public record PostResources_ResponseDto
        {
            public double Prop { get; set; }
        }

        [Fact]
        public async Task Post_ToRoute_Resources_ShouldReturnBadRequest_WithInvalidFileExtension()
        {
            // Arrange Multipart FormData mock data
            var content = new MultipartFormDataContent();
            
            // Mock an IFormFile as part of the request content
            var fileContent = new ByteArrayContent(new byte[] { 1, 2, 3 });
            fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/octet-stream");
            
            // Here, intentionally provide wrong file's extension, with valid values

            // Only file extensions allowed: { ".stl", ".obj", ".x3d", ".3mf" }
            content.Add(fileContent, "file", "test.txt");
            
            // Add other form data
            content.Add(new StringContent("0.2"), "layer_height");
            content.Add(new StringContent("20"), "infill");
            
            // Act
            var response = await _httpClient.PostAsync("slicer/resources", content);
            
            // Assert
            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
            
            // Deserialize the error response body
            var responseBody = await response.Content.ReadAsStringAsync();
            
            var errorResponse = JsonSerializer.Deserialize<ErrorDto>(
                responseBody,
                new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                });

            const string expectedErrorMessage = "INVALID_FILE_EXTENSION";
            
            // Verify error message
            Assert.NotNull(errorResponse);
            Assert.NotEmpty(errorResponse.Error);
            Assert.Equal(expectedErrorMessage, errorResponse.Error);
        }
        
        [Fact]
        public async Task Post_ToRoute_Resources_ShouldReturnBadRequest()
        {
            // Arrange Multipart FormData mock data
            var content = new MultipartFormDataContent();
            
            // Mock an IFormFile as part of the request content
            var fileContent = new ByteArrayContent(new byte[] { 1, 2, 3 });
            fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/octet-stream");
            
            // Here, intentionally add invalid or missing data to trigger and exception.
            // For example, provide file with allowed extension, but alongside invalid values

            // Only file extensions allowed: { ".stl", ".obj", ".x3d", ".3mf" }
            content.Add(fileContent, "file", "test.stl");
            
            // Add other form data
            content.Add(new StringContent("-1"), "layer_height");
            content.Add(new StringContent("200"), "infill");
            
            // Act
            var response = await _httpClient.PostAsync("slicer/resources", content);
            
            // Assert
            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
            
            // Deserialize the error response body
            var responseBody = await response.Content.ReadAsStringAsync();
            
            var errorResponse = JsonSerializer.Deserialize<ErrorDto>(
                responseBody,
                new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                });

            const string expectedErrorMessage = "???"; // TODO:
            
            // Verify error message
            Assert.NotNull(errorResponse);
            Assert.NotEmpty(errorResponse.Error);
            Assert.Equal(expectedErrorMessage, errorResponse.Error);
        }
        
        /*
        
        private readonly ICuraService _curaService;
        private readonly IIoService _ioService;
        private readonly IMemoryCache _memoryCache;

        private readonly ILogger<SlicerController> _logger;

        private readonly IEnumerable<string> _allowedFileExtensions;

        public SlicerController(ICuraService curaService, IIoService ioService, ILogger<SlicerController> logger, IMemoryCache memoryCache)
        {
            _curaService = curaService;
            _ioService = ioService;
            _memoryCache = memoryCache;
            _logger = logger;

            _allowedFileExtensions = new List<string> { ".stl", ".obj", ".x3d", ".3mf" };
        }

        [Route("resources")]
        [HttpPost()]
        public async Task<IActionResult> Resources(IFormFile file, [FromForm(Name = "layer_height")] double layerHeight, [FromForm(Name = "infill")] double infill)
        {
            try
            {
                var fileExtension = Path.GetExtension(file.FileName);

                if (!_allowedFileExtensions.Any(extension => extension == fileExtension.ToLower()))
                {
                    return BadRequest(new ErrorDto("INVALID_FILE_EXTENSION"));
                }

                var path = await _ioService.SaveFileAsync(file);

                var (time, material) = await _curaService.SliceAsync(path, layerHeight, infill);

                await _ioService.DeleteFileAsync(path);

                var price = 0.0; // material * pricingEntity.PricePerGram + time.TotalSeconds * pricingEntity.PricePerSecond;

                var dto = new { Prop = price };

                return Ok(dto);
            }
            catch (CuraException ex)
            {
                _logger.LogWarning(ex.Message);

                return BadRequest(new ErrorDto(ex.Message));
            }
        }
        
        */

    }

}

