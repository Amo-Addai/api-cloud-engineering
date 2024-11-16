using Xunit;
using System.Text;
using System.Net;
using System.Net.Http;
using System.Text.Json;
using System.Reflection;
using Sample.Slicer.API.Services;
using Sample.Slicer.API.Configuration;
using Sample.Slicer.API.Dtos.Projects;
using Sample.Slicer.API.Tests.Common;
using Moq;
using Moq.Protected;
using Microsoft.Extensions.Options;

using Sample.Slicer.API.Enums; // TODO: Move to CustomMock.cs

namespace Sample.Slicer.API.Tests.Services
{
    public class IIntegrationServiceTests
    {
        private readonly Mock<IIntegrationService> _serviceMock;

        public IIntegrationServiceTests()
        {
            _serviceMock = new Mock<IIntegrationService>();
        }
        
        [Fact]
        public async Task GetProjectAsync_ShouldBeCalled()
        {
            string projectId = "project-id";
            await _serviceMock.Object.GetProjectAsync(projectId);
            _serviceMock.Verify(s => s.GetProjectAsync(projectId), Times.Once);
        }
        
        [Fact]
        public async Task StartManufacturerReviewAsync_ShouldBeCalled()
        {
            string projectId = "project-id";
            var projectManufacturingUpdateInputDto =
                SampleProjectManufacturingUpdateInputDto.GetSampleProjectManufacturingUpdateInputDto();
            
            await _serviceMock.Object.StartManufacturerReviewAsync(projectId, projectManufacturingUpdateInputDto);
            _serviceMock.Verify(s => s.StartManufacturerReviewAsync(projectId, projectManufacturingUpdateInputDto), Times.Once);
        }
        
        [Fact]
        public async Task GetProjectFileAsync_ShouldBeCalled()
        {
            string projectId = "project-id";
            string fileId = "file-id";
            
            await _serviceMock.Object.GetProjectFileAsync(projectId, fileId);
            _serviceMock.Verify(s => s.GetProjectFileAsync(projectId, fileId), Times.Once);
        }
    }

    public class IntegrationServiceTests
    {
        private const string ValidApiUrl = "https://Sample-dev-private-api.blackocean-13874faf.eastus.azurecontainerapps.io";
            // TODO: "https://example.com";
        private const string ValidApiKey = "y8BIRLlAfFJ2JcJjdH0vSQYFdmM6WDnl";
            // TODO: "test-api-key";

        private readonly Mock<HttpMessageHandler> _httpMessageHandlerMock;
        private readonly HttpClient _httpClient;
        private readonly IntegrationService _service;
        private readonly TestIntegrationService _testService;

        public IntegrationServiceTests()
        {
            var apiVariablesMock = new Mock<IOptions<ApiVariables>>();
            
            // Configure the ApiVariables mock to return the expected API key
            apiVariablesMock.Setup(config => config.Value)
                .Returns(new ApiVariables { ApiUrl = ValidApiUrl, ApiKey = ValidApiKey });

            _httpMessageHandlerMock = new Mock<HttpMessageHandler>(); // TODO: MockBehavior.Strict
            
            _httpClient = new HttpClient(_httpMessageHandlerMock.Object)
            {
                BaseAddress = new Uri(apiVariablesMock.Object.Value.ApiUrl)
            };

			// TODO: TEST

            // _service = new IntegrationService(apiVariablesMock.Object);
            
			// Create an instance of IntegrationService
			_service = (IntegrationService) Activator.CreateInstance(
				typeof(IntegrationService),
				new object[] {
					apiVariablesMock.Object
				}
			);
            
            // TestService wrapper for testing private methods
            // _testService = new TestIntegrationService(apiVariablesMock.Object);
			_testService = (TestIntegrationService) Activator.CreateInstance(
				typeof(IntegrationService),
				new object[] {
					apiVariablesMock.Object
				}
			);
            
            // TODO: TEST
            // // _service.HttpClient = _httpClient;
            // // _testService.HttpClient = _httpClient;

			// Use reflection to set the private HttpClient field
			var httpClientField = typeof(IntegrationService)
				.GetField(
					"_httpClient",
					BindingFlags.NonPublic | BindingFlags.Instance
				);
	
			if (httpClientField == null)
			{
				throw new InvalidOperationException(
					String.Concat(
						"Field '_httpClient' not found in IntegrationService.\n",
						"Check field name or modify IntegrationService class properties"
					)
				);
			}

			// Set the IntegrationService private _httpClient field to IntegrationServiceTests mock _httpClient field
			httpClientField.SetValue(_service, _httpClient);
			httpClientField.SetValue(_testService, _httpClient);
        }

        #region GetProjectAsync Tests

        // Test for GetProjectAsync success case
        [Fact]
        public async Task GetProjectAsync_ShouldReturnProjectOutputDto_WhenProjectExists()
        {
            // Arrange
            string projectId = "project-id";
            string requestUrl = $"/projects/{projectId}";
			HttpMethod requestMethod = HttpMethod.Get;
            ProjectOutputDto expectedProject = SampleProjectOutputDto.GetSampleProjectOutputDto();
            string responseJson = JsonSerializer.Serialize<ProjectOutputDto>(expectedProject);
            
            // Mock HttpClient response to return a valid JSON response
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
			/* TODO: TEST
            _httpMessageHandlerMock
                .Setup(handler => handler.SendAsync(
                        It.Is<HttpRequestMessage>(
                            req =>
                                req.Method == requestMethod
                                && req.RequestUri.PathAndQuery.EndsWith(requestUrl) // .ToString().EndsWith(..)
                        ),
                        It.IsAny<CancellationToken>()
                    )
                )
			*/
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(responseJson)
                })
				.Verifiable();
            
            // TODO: TEST
            // // _service.HttpClient = _httpClient;

            // Act
            var result = await _service.GetProjectAsync(projectId);

            // Assert
            Assert.NotNull(result);
            Assert.IsType<ProjectOutputDto>(result);
            Assert.Equal(expectedProject, result, Helpers.JsonSerializerComparer<ProjectOutputDto>.Instance);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
			/*
            _httpMessageHandlerMock.Verify(
                handler => handler.SendAsync(
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                ),
                Times.Once
            );
			*/

        }
        
        // Test for GetProjectAsync failure case
        [Fact]
        public async Task GetProjectAsync_ShouldReturnNull_WhenProjectDoesNotExist()
        {
            // Arrange
            string projectId = "non-existent-project-id";
            string requestUrl = $"/projects/{projectId}";
            HttpMethod requestMethod = HttpMethod.Get;
            
            // Mock HttpClient response to return a valid JSON response
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();
            
            // _service.HttpClient = _httpClient;
            
            // Act
            var result = await _service.GetProjectAsync(projectId);
            
            // Assert
            Assert.Null(result);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);

        }
        
        #endregion GetProjectAsync Tests

        #region StartManufacturerReviewAsync Tests

        // Test for StartManufacturerReviewAsync success case
        [Fact]
        public async Task StartManufacturerReviewAsync_ShouldReturnTrue_WhenUpdateIsSuccessful()
        {
            // Arrange
            string projectId = "project-id";
            string requestUrl = $"/projects/{projectId}/manufacturer-review";
            HttpMethod requestMethod = HttpMethod.Patch;
            ProjectManufacturingUpdateInputDto projectManufacturingUpdateInputDto =
                SampleProjectManufacturingUpdateInputDto.GetSampleProjectManufacturingUpdateInputDto();
            
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(true.ToString())
                })
				.Verifiable();
            
            // _service.HttpClient = _httpClient;
            
            // Act
            var result = await _service.StartManufacturerReviewAsync(projectId, projectManufacturingUpdateInputDto);
            
            // Assert
            Assert.True(result);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for StartManufacturerReviewAsync failure case
        [Fact]
        public async Task StartManufacturerReviewAsync_ShouldReturnFalse_WhenUpdateFails()
        {
            // Arrange
            string projectId = "project-id";
            string requestUrl = $"/projects/{projectId}/manufacturer-review";
            HttpMethod requestMethod = HttpMethod.Patch;
            ProjectManufacturingUpdateInputDto projectManufacturingUpdateInputDto =
                SampleProjectManufacturingUpdateInputDto.GetSampleProjectManufacturingUpdateInputDto();

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.BadRequest
                })
				.Verifiable();
            
            // _service.HttpClient = _httpClient;
            
            // Act
            var result = await _service.StartManufacturerReviewAsync(projectId, projectManufacturingUpdateInputDto);

            // Assert
            Assert.False(result);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        #endregion StartManufacturerReviewAsync Tests

        #region GetProjectFileAsync Tests

        // Test for GetProjectFileAsync success case
        [Fact]
        public async Task GetProjectFileAsync_ShouldReturnProjectFileOutputDto_WhenFileExists()
        {
            // Arrange
            string projectId = "project-id";
            string fileId = "file-id";
            string requestUrl = $"/projects/{projectId}/files/{fileId}";
            HttpMethod requestMethod = HttpMethod.Get;
            ProjectFileOutputDto expectedFile = SampleProjectFileOutputDto.GetSampleProjectFileOutputDto();
            string responseJson = JsonSerializer.Serialize<ProjectFileOutputDto>(expectedFile);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(responseJson)
                })
				.Verifiable();
            
            // _service.HttpClient = _httpClient;
            
            // Act
            var result = await _service.GetProjectFileAsync(projectId, fileId);
            
            // Assert
            Assert.NotNull(result);
            Assert.IsType<ProjectFileOutputDto>(result);
            Assert.Equal(expectedFile, result, Helpers.JsonSerializerComparer<ProjectFileOutputDto>.Instance);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for GetProjectFileAsync failure case
        [Fact]
        public async Task GetProjectFileAsync_ShouldReturnNull_WhenFileDoesNotExist()
        {
            // Arrange
            string projectId = "project-id";
            string fileId = "non-existent-file-id";
            string requestUrl = $"/projects/{projectId}/files/{fileId}";
            HttpMethod requestMethod = HttpMethod.Get;
            
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();
            
            // _service.HttpClient = _httpClient;
            
            // Act
            var result = await _service.GetProjectFileAsync(projectId, fileId);
            
            // Assert
            Assert.Null(result);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        #endregion GetProjectFileAsync Tests

        #region SaveFileAsync Tests

        // Test for SaveFileAsync success case
        [Fact]
        public async Task SaveFileAsync_ShouldReturnTrue_WhenFileIsSavedSuccessfully()
        {
            // Arrange
            string fileName = "test-file.txt";
            string fileUrl = "https://example.com/test-file";
			HttpMethod requestMethod = HttpMethod.Get;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StreamContent(new MemoryStream(new byte[0]))
                })
				.Verifiable();
            
            // _testService.HttpClient = _httpClient;

            // Act
            var result = await _testService.SaveFileAsync(fileUrl, fileName, storagePath: ""); // todo: test with storagePath

            // Assert
            Assert.True(result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for SaveFileAsync failure case
        [Fact]
        public async Task SaveFileAsync_ShouldReturnFalse_WhenFileIsNotSavedSuccessfully()
        {
            // Arrange
            string fileName = "test-file.txt";
            string fileUrl = "https://example.com/test-file";
			HttpMethod requestMethod = HttpMethod.Get;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();
            
            // _testService.HttpClient = _httpClient;

            // Act
            var result = await _testService.SaveFileAsync(fileUrl, fileName);

            // Assert
            Assert.False(result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        #endregion SaveFileAsync Tests

        #region MakeRequestAsync Tests

        // Test for MakeRequestAsync Get success case
        [Fact]
        public async Task MakeRequestAsync_GetMethod_Success_ShouldReturnResponseString()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Get;
            string expectedContent = "{\"key\": \"value\"}";

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(expectedContent, Encoding.UTF8, "application/json")
                })
				.Verifiable();

            // TODO: _service / // _testService.HttpClient = _httpClient; ??

            // Act
            var result = await _testService.MakeRequestAsync(requestUrl, requestMethod);

            // Assert
            Assert.Equal(expectedContent, result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeRequestAsync Get failure case
        [Fact]
        public async Task MakeRequestAsync_GetMethod_Failure_ShouldReturnNull()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Get;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(requestUrl, requestMethod);

            // Assert
            Assert.Null(result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeRequestAsync Post success case
        [Fact]
        public async Task MakeRequestAsync_PostMethod_Success_ShouldReturnResponseString()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Post;
            var requestData = new { key = "value" };
            string serializedData = JsonSerializer.Serialize(requestData);
            string expectedContent = "{\"key\": \"value\"}";

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new StringContent(
                            expectedContent,
                            Encoding.UTF8,
                            "application/json"
                        )
                    }
                )
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(
                requestUrl,
                requestMethod,
                content: serializedData
            );

            // Assert
            Assert.Equal(expectedContent, result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeRequestAsync Post failure case
        [Fact]
        public async Task MakeRequestAsync_PostMethod_Failure_ShouldReturnNull()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Post;
            var requestData = new { key = "value" };
            string serializedData = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.BadRequest
                    }
                )
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(
                requestUrl,
                requestMethod,
                content: serializedData
            );

            // Assert
            Assert.Null(result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeRequestAsync Put success case
        [Fact]
        public async Task MakeRequestAsync_PutMethod_Success_ShouldReturnResponseString()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Put;
            var requestData = new { key = "new-value" };
            string serializedData = JsonSerializer.Serialize(requestData);
            string expectedContent = "{\"key\": \"value\"}";

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new StringContent(
                            expectedContent,
                            Encoding.UTF8,
                            "application/json"
                        )
                    }
                )
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(
                requestUrl,
                requestMethod,
                content: serializedData
            );

            // Assert
            Assert.Equal(expectedContent, result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeRequestAsync Put failure case
        [Fact]
        public async Task MakeRequestAsync_PutMethod_Failure_ShouldReturnNull()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Put;
            var requestData = new { key = "value" };
            string serializedData = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.NotFound
                    }
                )
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(
                requestUrl,
                requestMethod,
                content: serializedData
            );

            // Assert
            Assert.Null(result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeRequestAsync Patch success case
        [Fact]
        public async Task MakeRequestAsync_PatchMethod_Success_ShouldReturnResponseString()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Patch;
            var requestData = new { key = "patch-value" };
            string serializedData = JsonSerializer.Serialize(requestData);
            string expectedContent = "{\"key\": \"value\"}";

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new StringContent(
                            expectedContent,
                            Encoding.UTF8,
                            "application/json"
                        )
                    }
                )
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(
                requestUrl,
                requestMethod,
                content: serializedData
            );

            // Assert
            Assert.Equal(expectedContent, result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeRequestAsync Patch failure case
        [Fact]
        public async Task MakeRequestAsync_PatchMethod_Failure_ShouldReturnNull()
        {
            // Arrange
            string requestUrl = "/projects/123";
            HttpMethod requestMethod = HttpMethod.Patch;
            var requestData = new { key = "patch-value" };
            string serializedContent = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.BadRequest
                    }
                )
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(
                requestUrl,
                requestMethod,
                content: serializedContent
            );

            // Assert
            Assert.Null(result);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeRequestAsync Delete success case
        [Fact]
        public async Task MakeRequestAsync_DeleteMethod_Success_ShouldReturnResponseString()
        {
            // Arrange
            string requestUrl = "/projects/123";
			HttpMethod requestMethod = HttpMethod.Delete;
            string expectedContent = "{\"key\": \"value\"}";

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(expectedContent, Encoding.UTF8, "application/json")
                })
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(requestUrl, requestMethod);

            // Assert
            Assert.Equal(expectedContent, result);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeRequestAsync Delete failure case
        [Fact]
        public async Task MakeRequestAsync_DeleteMethod_Failure_ShouldReturnNull()
        {
            // Arrange
            string requestUrl = "/projects/123";
			HttpMethod requestMethod = HttpMethod.Delete;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage { StatusCode = HttpStatusCode.NotFound })
				.Verifiable();

            // Act
            var result = await _testService.MakeRequestAsync(requestUrl, requestMethod);

            // Assert
            Assert.Null(result);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        #endregion MakeRequestAsync Tests

        #region MakeFileRequestAsync Tests

        // Test for MakeFileRequestAsync Get success case
        [Fact]
        public async Task MakeFileRequestAsync_GetMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Get;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new ByteArrayContent(
                            Encoding.UTF8.GetBytes("file content")
                        )
                    }
                )
				.Verifiable();

			bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.True(result);
            Assert.True(callbackInvoked);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Get failure case
        [Fact]
        public async Task MakeFileRequestAsync_GetMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Get;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.NotFound
                    }
                )
				.Verifiable();
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;

            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                callback: saveFileCallback
            );

            // Assert
            Assert.False(result);
            Assert.False(callbackInvoked);

            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Post success case
        [Fact]
        public async Task MakeFileRequestAsync_PostMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Post;
            var requestData = new { file = "data" };
            string serializedData = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new ByteArrayContent(
                            Encoding.UTF8.GetBytes("file saved")
                        )
                    }
                )
				.Verifiable();
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                content: serializedData,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.True(result);
            Assert.True(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Post failure case
        [Fact]
        public async Task MakeFileRequestAsync_PostMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Post;
            var requestData = new { file = "data" };
            string serializedData = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.BadRequest
                    }
                )
				.Verifiable();
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                content: serializedData,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.False(result);
            Assert.False(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Put success case
        [Fact]
        public async Task MakeFileRequestAsync_PutMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Put;
            var requestData = new { file = "data" };
            string serializedData = JsonSerializer.Serialize(requestData);

			_httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new ByteArrayContent(
                            Encoding.UTF8.GetBytes("file updated")
                        )
                    }
                )
				.Verifiable();
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                content: serializedData,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.True(result);
            Assert.True(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Put failure case
        [Fact]
        public async Task MakeFileRequestAsync_PutMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Put;
            var requestData = new { file = "data" };
            string serializedData = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.NotFound
                    }
                )
				.Verifiable();
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                content: serializedData,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.False(result);
            Assert.False(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Patch success case
        [Fact]
        public async Task MakeFileRequestAsync_PatchMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Patch;
            var requestData = new { file = "data" };
            string serializedData = JsonSerializer.Serialize(requestData);

			_httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new ByteArrayContent(
                            Encoding.UTF8.GetBytes("file patched")
                        )
                    }
                )
				.Verifiable();

            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                content: serializedData,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.True(result);
            Assert.True(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Patch failure case
        [Fact]
        public async Task MakeFileRequestAsync_PatchMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Patch;
            var requestData = new { file = "data" };
            string serializedData = JsonSerializer.Serialize(requestData);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.BadRequest
                    }
                );
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> saveFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                content: serializedData,
                callback: saveFileCallback
            );
            
            // Assert
            Assert.False(result);
            Assert.False(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Delete success case
        [Fact]
        public async Task MakeFileRequestAsync_DeleteMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Delete;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.OK,
                        Content = new ByteArrayContent(
                            Encoding.UTF8.GetBytes("file deleted")
                        )
                    }
                );
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> deleteFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                callback: deleteFileCallback
            );
            
            // Assert
            Assert.True(result);
            Assert.True(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Delete failure case
        [Fact]
        public async Task MakeFileRequestAsync_DeleteMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            HttpMethod requestMethod = HttpMethod.Delete;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
                )
                .ReturnsAsync(
                    new HttpResponseMessage
                    {
                        StatusCode = HttpStatusCode.NotFound
                    }
                );
            
            bool callbackInvoked = false;
			Action<HttpResponseMessage> deleteFileCallback = response => callbackInvoked = true;
            
            // Act
            var result = await _testService.MakeFileRequestAsync(
                requestUrl,
                requestMethod,
                fileName,
                callback: deleteFileCallback
            );
            
            // Assert
            Assert.False(result);
            Assert.False(callbackInvoked);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					It.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    It.IsAny<CancellationToken>()
            	);
        }
        
        #endregion MakeFileRequestAsync Tests

    }
    
    
    // TODO: Setup Mock Files & Inject into IntegrationService (& other services
    
    
    public class SampleProjectManufacturingUpdateInputDto
    {
        public static ProjectManufacturingUpdateInputDto GetSampleProjectManufacturingUpdateInputDto()
        {
            var material = ManufacturingMaterial.Pla;
            var grams = 100;
            var time = TimeSpan.FromHours(2);
            double? customCost = 15.5;

            var materialsUsage = new[]
            {
                new ProjectMaterialUsageDto
                {
                    Material = material,
                    Grams = grams,
                    Time = time,
                    CustomCost = customCost
                },
                new ProjectMaterialUsageDto
                {
                    Material = material,
                    Grams = grams,
                    Time = time,
                    CustomCost = customCost
                }
            };

            var projectManufacturingUpdateInputDto = new ProjectManufacturingUpdateInputDto
            {
                MaterialsUsage = materialsUsage
            };

            return projectManufacturingUpdateInputDto;
        }
    }
    
    public class SampleProjectOutputDto
    {
        public static ProjectOutputDto GetSampleProjectOutputDto()
        {
            var id = Guid.NewGuid();
            var title = "Test Project";
            var category = "Category1";
            var description = "This is a test project.";
            var hashtags = new List<string> { "#test", "#project" };
            var files = new List<ProjectFileOutputDto>
            {
                new ProjectFileOutputDto
                {
                    Id = Guid.NewGuid(),
                    Name = "File1",
                    Extension = ".txt",
                    Size = 1024,
                    CreatedAt = DateTime.UtcNow,
                    UpdatedAt = DateTime.UtcNow,
                    Quantifier = 1
                }
            };
            var images = new List<ProjectImageOutputDto>
            {
                new ProjectImageOutputDto
                {
                    Id = Guid.NewGuid(),
                    Name = "Image1",
                    Extension = ".jpg",
                    Size = 2048,
                    IsProfileImage = true,
                    Url = "http://example.com/image.jpg",
                    CreatedAt = DateTime.UtcNow
                }
            };
            var status = "Active";
            var ownerId = Guid.NewGuid();
            var createdAt = DateTime.UtcNow;
            var updatedAt = DateTime.UtcNow;
            var manufacturingDescription = "Manufacturing description";
            var dimensions = new ProjectDimensionOutputDto
            {
                Width = 10.5,
                Height = 20.5,
                Depth = 30.5,
                UnitOfMeasurement = "cm"
            };
            var price = 99.99;
            var numberOfParts = 5;
            var minStrength = "High";
            var minQuality = "Premium";
            var materialsUsage = new List<MaterialUsageOutputDto>
            {
                new MaterialUsageOutputDto
                {
                    Material = "PLA",
                    Grams = 100,
                    Time = TimeSpan.FromHours(2),
                    CustomCost = 15.5,
                    ManufacturingCost = 20.0,
                    FinalPrice = 35.5
                }
            };
            var supportStructures = new SupportStructuresOutputDto
            {
                HasSupportStructures = true,
                SpecialInstructions = "Handle with care",
                SupportStructureType = "Type1"
            };
            var qualitySettings = new QualitySettingsOutputDto
            {
                LayerHeightUnitOfMeasurement = "mm",
                LayerHeight = 0.2,
                InfillPercentage = 20.0,
                InfillType = "Grid"
            };
            var postProcessingActions = new List<PostProcessingActionOutputDto>
            {
                new PostProcessingActionOutputDto
                {
                    Action = "Polishing",
                    Duration = TimeSpan.FromHours(1),
                    Cost = 10.0
                }
            };
            var overallAssessment = new OverallAssessmentOutputDto
            {
                EquipmentQuality = "Excellent",
                PotentialChanges = "None",
                AdditionalNotes = "No additional notes",
                Dimensions = dimensions,
                Weight = 500
            };
            var reviewerId = Guid.NewGuid();
            var adminActions = new List<AdminActionsOutputDto>
            {
                new AdminActionsOutputDto
                {
                    Action = "Approve",
                    Status = "Completed"
                }
            };

            var projectOutputDto = new ProjectOutputDto
            {
                Id = id,
                Title = title,
                Category = category,
                Description = description,
                Hashtags = hashtags,
                Files = files,
                Images = images,
                Status = status,
                OwnerId = ownerId,
                CreatedAt = createdAt,
                UpdatedAt = updatedAt,
                ManufacturingDescription = manufacturingDescription,
                Dimensions = dimensions,
                Price = price,
                NumberOfParts = numberOfParts,
                MinStrength = minStrength,
                MinQuality = minQuality,
                MaterialsUsage = materialsUsage,
                SupportStructures = supportStructures,
                QualitySettings = qualitySettings,
                PostProcessingActions = postProcessingActions,
                OverallAssessment = overallAssessment,
                ReviewerId = reviewerId,
                AdminActions = adminActions
            };

            return projectOutputDto;
        }
    }

    public class SampleProjectFileOutputDto
    {
        public static ProjectFileOutputDto GetSampleProjectFileOutputDto()
        {
            var id = Guid.NewGuid();
            var name = "Test Project";
            var extension = ".txt";
            var size = 1024L;
            var createdAt = DateTime.UtcNow;
            var updatedAt = DateTime.UtcNow;
            int? quantifier = 5;

            var projectFileOutputDto = new ProjectFileOutputDto
            {
                Id = id,
                Name = name,
                Extension = extension,
                Size = size,
                CreatedAt = createdAt,
                UpdatedAt = updatedAt,
                Quantifier = quantifier
            };

            return projectFileOutputDto;
        }
    }
    
}
