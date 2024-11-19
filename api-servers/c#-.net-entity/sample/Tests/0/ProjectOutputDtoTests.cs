using Xunit;
using Cadly.Slicer.API.Dtos.Projects;
using Cadly.Slicer.API.Tests.Dtos.Common;

namespace Cadly.Slicer.API.Tests.Dtos.Projects
{
    public class ProjectOutputDtoTests
    {
        [Fact]
        public void Should_CreateDto_WithDefaultValues()
        {
            var id = new Guid("12345678-1234-1234-1234-123456789abc");
            var title = "Test Project";
            var category = "Category1";
            var description = "This is a test project.";
            var hashtags = new List<string> { "#test", "#project" };
            var files = new List<ProjectFileOutputDto>
            {
                new ProjectFileOutputDto
                {
                    Id = new Guid("12345678-1234-1234-1234-123456789abc"),
                    Name = "File1",
                    Extension = ".txt",
                    Size = 1024,
                    CreatedAt = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                    UpdatedAt = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                    Quantifier = 1
                }
            };
            var images = new List<ProjectImageOutputDto>
            {
                new ProjectImageOutputDto
                {
                    Id = new Guid("12345678-1234-1234-1234-123456789abc"),
                    Name = "Image1",
                    Extension = ".jpg",
                    Size = 2048,
                    IsProfileImage = true,
                    Url = "http://example.com/image.jpg",
                    CreatedAt = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc)
                }
            };
            var status = "Active";
            var ownerId = new Guid("12345678-1234-1234-1234-123456789abc");
            var createdAt = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            var updatedAt = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc);
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
            var reviewerId = new Guid("12345678-1234-1234-1234-123456789abc");
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

			// Assert Snapshot
			Utils.Snapshot<ProjectOutputDto>(dto, nameof(ProjectOutputDto));
        }
    }
}
