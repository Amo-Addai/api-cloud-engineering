using Xunit;
using System.Text;
using System.Net;
using System.Net.Http;
using System.Text.Json;
using System.Reflection;
using Company.Slicer.API.Services;
using Company.Slicer.API.Configuration;
using Company.Slicer.API.Dtos.Projects;
using Company.Slicer.API.Tests.Common;
using Company.Slicer.API.Tests.Services.Common;
using Moq;
using Moq.Protected;
using Microsoft.Extensions.Options;

namespace Company.Slicer.API.Tests.Services
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
                Dto_Utils.SampleProjectManufacturingUpdateInputDto.GetSampleProjectManufacturingUpdateInputDto();
            
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
        private const string ValidApiUrl = "https://example.com";
        private const string ValidApiKey = "test-api-key";

        private readonly Mock<HttpMessageHandler> _httpMessageHandlerMock;
        private readonly HttpClient _httpClient;

        private IntegrationService _service;
        private TestIntegrationService _testService;

		private delegate Task RequestLogger(
			HttpRequestMessage req,
			CancellationToken? token = null,
			string url = null,
			HttpMethod method = null,
			string contentType = null,
			object content = null,
			bool shouldLog = false // TODO: false by default (toggle - true for debug; or per test-method)
		);

        private RequestLogger LogRequest = // * test-debug
			async (req, token, url, method, contentType, content, shouldLog) => {
				if (shouldLog)
				{
				    Console.WriteLine("Logging Request Object");
	                
				    Array.ForEach(
				    	new string[] {
				    		$"Request Object: {req?.ToString() ?? "-"}",
				    		$"Request Url: {req?.RequestUri?.PathAndQuery?.ToString() ?? "-"}",
				    		$"Url: {url ?? "-"}",
				    		$"Request Method: {req?.Method?.ToString() ?? "-"}",
				    		$"Method: {method?.ToString() ?? "-"}",
				    		$"Request Content Type: {req?.Content?.Headers?.ContentType?.MediaType ?? "-"}",
				    		$"Content Type: {contentType ?? "-"}",
				    		$"Request Content: {(await req?.Content?.ReadAsStringAsync()) ?? "-"}",
				    		$"Content: {content?.ToString() ?? "-"}",
							
				    		$"Content Match: {
								(
								    content != null
								    ? ( ReferenceEquals(req?.Content, content).ToString() ?? "-" )
								    : "-"
								)
							}",
							
							$"Request Match: {
						        (
				    	        	req?.Method == method
                                    && ( req?.RequestUri?.PathAndQuery?.EndsWith(url) ?? false )
						        	&& (
						        		(
						                	method != null
						                	&& contentType != null
						                	&& content != null
						                )
				                        ? req?.Content?.Headers?.ContentType?.MediaType == contentType
				                            && ReferenceEquals(req?.Content, content)
						                : true
						        	)
				    	        ).ToString()
							}",

				    		$"CancellationToken: {
								(
									token != null
									? ( token.ToString() ?? "-" )
									: "-"
								)
							}",
                    		
				    	},
				    	Console.WriteLine
				    );
					
					
				}
			};

        public IntegrationServiceTests()
        {
            var apiVariablesMock = new Mock<IOptions<ApiVariables>>();
            
            // Configure the ApiVariables mock to return the expected API key
            apiVariablesMock.Setup(config => config.Value)
                .Returns(new ApiVariables { ApiUrl = ValidApiUrl, ApiKey = ValidApiKey });

            _httpMessageHandlerMock = new Mock<HttpMessageHandler>(MockBehavior.Strict);
            
            _httpClient = new HttpClient(_httpMessageHandlerMock.Object)
            {
                BaseAddress = new Uri(apiVariablesMock.Object.Value.ApiUrl)
            };

			// Instead of instantiatio, Create a Reflection instance of IntegrationService
			_service = (IntegrationService) Activator.CreateInstance(
				typeof(IntegrationService),
				new object[] {
					apiVariablesMock.Object
				}
			)!; // Force reflection-instance; property requires a non-null value

			_testService = (TestIntegrationService) Activator.CreateInstance(
				typeof(TestIntegrationService),
				new object[] {
					apiVariablesMock.Object
				}
			)!;

			// Use reflection to set the private HttpClient field
			var httpClientField = typeof(IntegrationService) // Will also set reflection of Test sub-class (TestIntegrationService) private-field
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
			httpClientField.SetValue(_testService, _httpClient); // Also set reflection of Test sub-class private-field
        }

		[Fact]
		public void TestIntegrationServices_Instantiated()
		{
			Assert.NotNull(_httpMessageHandlerMock);
			Assert.NotNull(_httpClient);
            Assert.IsType<IntegrationService>(_service);
            Assert.IsType<TestIntegrationService>(_testService);
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
            ProjectOutputDto expectedProject = Dto_Utils.SampleProjectOutputDto.GetSampleProjectOutputDto();
            string expectedContent = JsonSerializer.Serialize<ProjectOutputDto>(expectedProject);
            
            // Mock HttpClient response to return a valid JSON response
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(
                        expectedContent,
                        Encoding.UTF8,
                        "application/json"
                    )
                })
				.Verifiable();
            
            // Act
            var result = await _service.GetProjectAsync(projectId);

            // Assert
            Assert.NotNull(result);
            Assert.IsType<ProjectOutputDto>(result);
            Assert.Equal(expectedContent, JsonSerializer.Serialize<ProjectOutputDto>(result));
            Assert.Equal(expectedProject, result, Utils.JsonSerializerComparer<ProjectOutputDto>.Instance);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();
                        
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                Dto_Utils.SampleProjectManufacturingUpdateInputDto.GetSampleProjectManufacturingUpdateInputDto();
            
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
					// TODO: pass in remaining (default-valued) arguments; & toggle 'shouldLog' true if required
                    (req, token) => LogRequest(req) // for other method invocations too
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(true.ToString())
                })
				.Verifiable();
                        
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                Dto_Utils.SampleProjectManufacturingUpdateInputDto.GetSampleProjectManufacturingUpdateInputDto();

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.BadRequest
                })
				.Verifiable();
                        
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
            ProjectFileOutputDto expectedFile = Dto_Utils.SampleProjectFileOutputDto.GetSampleProjectFileOutputDto();
            string expectedContent = JsonSerializer.Serialize<ProjectFileOutputDto>(expectedFile);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StringContent(
                        expectedContent,
                        Encoding.UTF8,
                        "application/json"
                    )
                })
				.Verifiable();
                        
            // Act
            var result = await _service.GetProjectFileAsync(projectId, fileId);
            
            // Assert
            Assert.NotNull(result);
            Assert.IsType<ProjectFileOutputDto>(result);
            Assert.Equal(expectedContent, JsonSerializer.Serialize<ProjectFileOutputDto>(result));
            Assert.Equal(expectedFile, result, Utils.JsonSerializerComparer<ProjectFileOutputDto>.Instance);
            
            // Verify that the mock HttpClient request was made
            _httpMessageHandlerMock
				.Protected()
				.Verify(
					"SendAsync",
					Times.Once(),
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();
                        
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
            string fileUrl = $"/files/{fileName}";
			HttpMethod requestMethod = HttpMethod.Get;

			// Mock request handler for MakeFileRequestAsync
            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.OK,
                    Content = new StreamContent(new MemoryStream(new byte[0]))
                })
				.Verifiable();
            
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }
        
        // Test for SaveFileAsync failure case
        [Fact]
        public async Task SaveFileAsync_ShouldReturnFalse_WhenFileIsNotSavedSuccessfully()
        {
            // Arrange
            string fileName = "test-file.txt";
            string fileUrl = $"/files/{fileName}";
			HttpMethod requestMethod = HttpMethod.Get;

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
                )
                .ReturnsAsync(new HttpResponseMessage
                {
                    StatusCode = HttpStatusCode.NotFound
                })
				.Verifiable();
            
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(fileUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& req.Content!.ReadAsStringAsync().Result == serializedData
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Post success case
        [Fact]
        public async Task MakeFileRequestAsync_PostMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            string fileContent = "Sample file content";
            string requestContentType = "multipart/form-data";
            
            HttpMethod requestMethod = HttpMethod.Post;
            
            var fileStream = new MemoryStream(
                Encoding.UTF8.GetBytes(fileContent)
            );
            var formDataContent = new MultipartFormDataContent
            {
                // Add file Content
                { new StreamContent(fileStream), "file", fileName },
                
                // Add additional form fields
                { new StringContent("value1"), "key1" },
                { new StringContent("value2"), "key2" }
            };

			Func<HttpRequestMessage, bool> validateMultipartContent =
				req =>
					req.Content is MultipartFormDataContent content
					&& content.Headers.ContentType.MediaType == requestContentType
					&& ReferenceEquals(content, formDataContent); // Check for exact instance

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& validateMultipartContent(req)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
				contentType: requestContentType,
                content: (null, formDataContent),
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Post failure case
        [Fact]
        public async Task MakeFileRequestAsync_PostMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            string fileContent = "Sample file content";
            string requestContentType = "multipart/form-data";
            
            HttpMethod requestMethod = HttpMethod.Post;
            
            var fileStream = new MemoryStream(
                Encoding.UTF8.GetBytes(fileContent)
            );
            var formDataContent = new MultipartFormDataContent
            {
                // Add file Content
                { new StreamContent(fileStream), "file", fileName },
                
                // Add additional form fields
                { new StringContent("value1"), "key1" },
                { new StringContent("value2"), "key2" }
            };

			Func<HttpRequestMessage, bool> validateMultipartContent =
				req =>
					req.Content is MultipartFormDataContent content
					&& content.Headers.ContentType.MediaType == requestContentType
					&& ReferenceEquals(content, formDataContent);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& validateMultipartContent(req)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
				contentType: requestContentType,
                content: (null, formDataContent),
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Put success case
        [Fact]
        public async Task MakeFileRequestAsync_PutMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            string fileContent = "Sample file content";
            string requestContentType = "multipart/*";
            
            HttpMethod requestMethod = HttpMethod.Put;

            var multipartContent = new MultipartContent("*")
            {
                // Add fields
                { new StringContent(fileName, Encoding.UTF8, "text/plain") },
                { new ByteArrayContent(Encoding.UTF8.GetBytes(fileContent)) }
            };

			Func<HttpRequestMessage, bool> validateMultipartContent =
				req =>
					req.Content is MultipartContent content
					&& content.Headers.ContentType.MediaType == requestContentType
					&& ReferenceEquals(content, multipartContent);

			_httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& validateMultipartContent(req)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
				contentType: requestContentType,
                content: (multipartContent, null),
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Put failure case
        [Fact]
        public async Task MakeFileRequestAsync_PutMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            string fileContent = "Sample file content";
            string requestContentType = "multipart/*";
            
            HttpMethod requestMethod = HttpMethod.Put;

            var multipartContent = new MultipartContent("*")
            {
                // Add fields
                { new StringContent(fileName, Encoding.UTF8, "text/plain") },
                { new ByteArrayContent(Encoding.UTF8.GetBytes(fileContent)) }
            };

			Func<HttpRequestMessage, bool> validateMultipartContent =
				req =>
					req.Content is MultipartContent content
					&& content.Headers.ContentType.MediaType == requestContentType
					&& ReferenceEquals(content, multipartContent);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
							&& validateMultipartContent(req)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
				contentType: requestContentType,
                content: (multipartContent, null),
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }

        // Test for MakeFileRequestAsync Patch success case
        [Fact]
        public async Task MakeFileRequestAsync_PatchMethod_Success_ShouldReturnTrue()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            string fileContent = "Sample file content";
            string requestContentType = "multipart/*";
            
            HttpMethod requestMethod = HttpMethod.Patch;

            var multipartContent = new MultipartContent("*")
            {
                // Add fields
                { new StringContent(fileName, Encoding.UTF8, "text/plain") },
                { new ByteArrayContent(Encoding.UTF8.GetBytes(fileContent)) }
            };

			Func<HttpRequestMessage, bool> validateMultipartContent =
				req =>
					req.Content is MultipartContent content
					&& content.Headers.ContentType.MediaType == requestContentType
					&& ReferenceEquals(content, multipartContent);
			
			_httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                            && validateMultipartContent(req)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
				// requestContentType, // * no content-type - defaults-to "multipart/*"
                content: (multipartContent, null),
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }
        
        // Test for MakeFileRequestAsync Patch failure case
        [Fact]
        public async Task MakeFileRequestAsync_PatchMethod_Failure_ShouldReturnFalse()
        {
            // Arrange
            string requestUrl = "/projects/123/files";
            string fileName = "test-file.txt";
            string fileContent = "Sample file content";
            string requestContentType = "multipart/*";
            
            HttpMethod requestMethod = HttpMethod.Patch;

            var multipartContent = new MultipartContent("*")
            {
                // Add fields
                { new StringContent(fileName, Encoding.UTF8, "text/plain") },
                { new ByteArrayContent(Encoding.UTF8.GetBytes(fileContent)) }
            };

			Func<HttpRequestMessage, bool> validateMultipartContent =
				req =>
					req.Content is MultipartContent content
					&& content.Headers.ContentType.MediaType == requestContentType
					&& ReferenceEquals(content, multipartContent);

            _httpMessageHandlerMock
				.Protected()
				.Setup<Task<HttpResponseMessage>>(
					"SendAsync",
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                            && validateMultipartContent(req)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
				// requestContentType, // * no content-type - defaults-to "multipart/*"
                content: (multipartContent, null),
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
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
                    ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
                )
                .Callback<HttpRequestMessage, CancellationToken>(
                    (req, token) => LogRequest(req)
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
					ItExpr.Is<HttpRequestMessage>(
                        req =>
                            req.Method == requestMethod
                            && req.RequestUri.PathAndQuery.EndsWith(requestUrl)
                    ),
                    ItExpr.IsAny<CancellationToken>()
            	);
        }
        
        #endregion MakeFileRequestAsync Tests

    }

}
