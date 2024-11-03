using Sample.API.Dtos;
using Sample.API.Exceptions;
using Sample.API.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Caching.Memory;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Sample.API.Controllers
{
    [ApiController]
    [Route("[controller]")]
    [Authorize(AuthenticationSchemes = "ApiKey")]
    public class Sample1Controller : ControllerBase
    {
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

        [HttpGet("greet")]
        public IActionResult Greet()
        {
            return Ok(new { Message = "Hello from the API!" });
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

    }

}

