using Sample.Slicer.API.Dtos.Projects;
using Xunit;

namespace Sample.Slicer.API.Tests.Dtos.Projects
{
    public class ProjectOutputDtoTests
    {
        [Fact]
        public void Should_CreateDto_WithDefaultValues()
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

            var dto = new ProjectOutputDto
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

            Assert.Equal(id, dto.Id);
            Assert.Equal(title, dto.Title);
            Assert.Equal(category, dto.Category);
            Assert.Equal(description, dto.Description);
            Assert.Equal(hashtags, dto.Hashtags);
            Assert.Equal(files, dto.Files);
            Assert.Equal(images, dto.Images);
            Assert.Equal(status, dto.Status);
            Assert.Equal(ownerId, dto.OwnerId);
            Assert.Equal(createdAt, dto.CreatedAt);
            Assert.Equal(updatedAt, dto.UpdatedAt);
            Assert.Equal(manufacturingDescription, dto.ManufacturingDescription);
            Assert.Equal(dimensions, dto.Dimensions);
            Assert.Equal(price, dto.Price);
            Assert.Equal(numberOfParts, dto.NumberOfParts);
            Assert.Equal(minStrength, dto.MinStrength);
            Assert.Equal(minQuality, dto.MinQuality);
            Assert.Equal(materialsUsage, dto.MaterialsUsage);
            Assert.Equal(supportStructures, dto.SupportStructures);
            Assert.Equal(qualitySettings, dto.QualitySettings);
            Assert.Equal(postProcessingActions, dto.PostProcessingActions);
            Assert.Equal(overallAssessment, dto.OverallAssessment);
            Assert.Equal(reviewerId, dto.ReviewerId);
            Assert.Equal(adminActions, dto.AdminActions);
        }
    }
}

/*

using System.Text.Json.Serialization;

namespace Sample.Slicer.API.Dtos.Projects;

public record ProjectOutputDto
{
    [JsonPropertyName("id")]
    public required Guid Id { get; init; }

    [JsonPropertyName("title")]
    public required string Title { get; init; }

    [JsonPropertyName("category")]
    public required string Category { get; init; }

    [JsonPropertyName("description")]
    public required string Description { get; init; }

    [JsonPropertyName("hashtags")]
    public required IEnumerable<string> Hashtags { get; init; }

    [JsonPropertyName("files")]
    public required IEnumerable<ProjectFileOutputDto> Files { get; init; }

    [JsonPropertyName("images")]
    public required IEnumerable<ProjectImageOutputDto> Images { get; init; }

    [JsonPropertyName("status")]
    public required string Status { get; init; }

    [JsonPropertyName("ownerId")]
    public required Guid OwnerId { get; init; }

    [JsonPropertyName("createdAt")]
    public required DateTime CreatedAt { get; init; }

    [JsonPropertyName("updatedAt")]
    public required DateTime UpdatedAt { get; init; }

    [JsonPropertyName("manufacturing_description")]
    public string? ManufacturingDescription { get; init; }

    [JsonPropertyName("dimensions")]
    public ProjectDimensionOutputDto? Dimensions { get; init; }

    [JsonPropertyName("price")]
    public double? Price { get; init; }

    [JsonPropertyName("number_of_parts")]
    public required int NumberOfParts { get; init; }

    [JsonPropertyName("min_strength")]
    public required string MinStrength { get; init; }

    [JsonPropertyName("min_quality")]
    public required string MinQuality { get; init; }

    [JsonPropertyName("materials_usage")]
    public IEnumerable<MaterialUsageOutputDto>? MaterialsUsage { get; init; }

    [JsonPropertyName("support_structures")]
    public SupportStructuresOutputDto? SupportStructures { get; init; }

    [JsonPropertyName("quality_settings")]
    public QualitySettingsOutputDto? QualitySettings { get; init; }

    [JsonPropertyName("post_processing_actions")]
    public IEnumerable<PostProcessingActionOutputDto>? PostProcessingActions { get; init; }

    [JsonPropertyName("overall_assessment")]
    public OverallAssessmentOutputDto? OverallAssessment { get; init; }

    [JsonPropertyName("reviewer_id")]
    public Guid? ReviewerId { get; init; }

    [JsonPropertyName("admin_actions")]
    public IEnumerable<AdminActionsOutputDto>? AdminActions { get; init; }
}

public record ProjectDimensionOutputDto
{
    [JsonPropertyName("width")]
    public required double Width { get; init; }

    [JsonPropertyName("height")]
    public required double Height { get; init; }

    [JsonPropertyName("depth")]
    public required double Depth { get; init; }

    [JsonPropertyName("unit_of_measurement")]
    public required string UnitOfMeasurement { get; init; }
}

public record MaterialUsageOutputDto
{
    [JsonPropertyName("material")]
    public required string Material { get; init; }

    [JsonPropertyName("grams")]
    public required int Grams { get; init; }

    [JsonPropertyName("time")]
    public required TimeSpan Time { get; init; }

    [JsonPropertyName("custom_cost")]
    public double? CustomCost { get; init; }

    [JsonPropertyName("manufacturing_cost")]
    public required double ManufacturingCost { get; init; }

    [JsonPropertyName("final_price")]
    public required double FinalPrice { get; init; }
}

public record SupportStructuresOutputDto
{
    [JsonPropertyName("has_support_structures")]
    public required bool HasSupportStructures { get; init; }

    [JsonPropertyName("special_instructions")]
    public string? SpecialInstructions { get; init; }

    [JsonPropertyName("support_structure_type")]
    public required string SupportStructureType { get; init; }
}

public record QualitySettingsOutputDto
{
    [JsonPropertyName("layer_height_unit_of_measurement")]
    public required string LayerHeightUnitOfMeasurement { get; init; }

    [JsonPropertyName("layer_height")]
    public required double LayerHeight { get; init; }

    [JsonPropertyName("infill_percentage")]
    public required double InfillPercentage { get; init; }

    [JsonPropertyName("infill_type")]
    public required string InfillType { get; init; }
}

public record PostProcessingActionOutputDto
{
    [JsonPropertyName("action")]
    public required string Action { get; init; }

    [JsonPropertyName("duration")]
    public required TimeSpan Duration { get; init; }

    [JsonPropertyName("cost")]
    public required double Cost { get; init; }
}

public record OverallAssessmentOutputDto
{
    [JsonPropertyName("equipment_quality")]
    public required string EquipmentQuality { get; init; }

    [JsonPropertyName("potential_changes")]
    public string? PotentialChanges { get; init; }

    [JsonPropertyName("additional_notes")]
    public string? AdditionalNotes { get; init; }

    [JsonPropertyName("dimensions")]
    public required ProjectDimensionOutputDto Dimensions { get; init; }

    [JsonPropertyName("weight")]
    public required int Weight { get; init; }
}

public record AdminActionsOutputDto
{
    [JsonPropertyName("action")]
    public required string Action { get; init; }

    [JsonPropertyName("status")]
    public required string Status { get; init; }
}

*/