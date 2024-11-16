
namespace Sample.Slicer.API.Configuration;

public record ApiVariables
{
    public required string ApiKey { get; init; }
}

public static class ConfigurationExtension
{
    private const string PrivateApiSectionName = "PrivateApi";

    public static IServiceCollection ConfigureEnvironmentVariables(this IServiceCollection services, IConfiguration configuration)
    {
        services.Configure<ApiVariables>(configuration.GetSection(PrivateApiSectionName));
        
        // * execs only once, on any 1st IService service call for env-vars
        // Console.WriteLine(configuration.GetSection(PrivateApiSectionName).ToJson());
        
        /* // *  todo: BAD for 'env-auth' (method for configuring env-vars only)
        services.AddAuthentication("ApiKey")
            // .AddScheme<AuthenticationSchemeOptions, ApiKeyAuthenticationHandler>("ApiKey", options =>
            .AddScheme<ApiAuthenticationSchemeOptions, ApiKeyAuthenticationHandler>("ApiKey", options =>
            { // todo: if false-forced, this callback is also exec'd only once on 1st service-env-var call, so options.Prop can still be accessed by CustomAuthSchemeOptions implementer
                
                // * both binding & manual options available
                
                // Bind configuration section PrivateApiSectionName directly to options
                // can work with both Env-AuthVars & Custom Auth-Scheme; doesn't require custom Auth-Scheme for Options.Props (no options.Prop setter)
                // builder.Configuration.GetSection(PrivateApiSectionName).Bind(options); // todo: straight-binding with Custom Auth-Scheme - best auth-strategy
                
                // * Manually Inject ApiVariables configuration
                // can only work with Custom Auth-Scheme - requires custom Auth-Scheme for Options.Props (options.ApiKey)
                // var apiVariables = builder.Configuration.GetSection(PrivateApiSectionName).Get<ApiVariables>();
                // options.ApiKey = apiVariables?.ApiKey;
            });
        */

        return services;
    }
}