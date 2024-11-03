using Microsoft.AspNetCore.Mvc;

namespace Sample.API.Controllers;

[ApiController]
[Route("[controller]")]
public class SampleController : ControllerBase
{
    
    public SampleController() {}

    [Route("sample")]
    [HttpGet]
    public IActionResult Get()
    {
        return Ok("res");
    }

    [HttpGet("samples")]
    public async Task<IActionResult> Get1()
    {
        return await Task.Run(() => Ok("res"));
    }
    
}