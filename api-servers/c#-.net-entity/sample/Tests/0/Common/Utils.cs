using System.Text.Json;
using Company.Slicer.API.Tests.Common;

namespace Company.Slicer.API.Tests.Dtos.Common;

public class Utils
{
    public static void Snapshot<T>(T dto, string fileName)
    {
        try
        {
            // Act - Serialize Dto to Pretty-Json
            string json = JsonSerializer.Serialize<T>(
				dto,
				new JsonSerializerOptions { WriteIndented = true }
			);

            // Snapshot file path
			string baseDirectory = Path.Combine(AppContext.BaseDirectory, @"../../../Dtos");
			Directory.SetCurrentDirectory(baseDirectory);
			string currentWorkingDirectory = Directory.GetCurrentDirectory();
            string filePath = $"{currentWorkingDirectory}/Snapshots/{fileName}_Snapshot.md";

            if (!File.Exists(filePath))
            {
                File.WriteAllText(filePath, json);
                Console.WriteLine($"Snapshot file created: {filePath}");
            }
            else
            {
                string snapshot = File.ReadAllText(filePath);
                var expectedDto = JsonSerializer.Deserialize<T>(snapshot);
                
                Assert.Equal(json, snapshot);
                Assert.Equal(
					dto,
					expectedDto,
					Company.Slicer.API.Tests.Common
					.Utils.JsonSerializerComparer<T>.Instance
				);
            }
        }
        catch (Exception exception)
        {
            Console.WriteLine(exception.Message);
        }
    }
}
