# Shot Breakdown Tool Example

This example demonstrates how to create a complex tool using the official Anthropic SDK for Java. The tool generates a shot breakdown list for movie or video scripts.

## Overview

The `ShotBreakdownToolExample` class shows how to:

1. Define a complex JSON schema for tool inputs using the official SDK
2. Create nested object structures with arrays and enums
3. Use the Tool and InputSchema builders properly
4. Execute tool calls with the Anthropic API

## Tool Definition

The tool `generateShotBreakdown` accepts a script text and generates a structured breakdown containing:

- **shots**: An array of shot objects, where each shot contains:
  - **description**: Description of the shot (required)
  - **angle**: Array of camera angles/movements from a predefined list (required)
  - **script**: Dialogue content for the shot (optional)
  - **note**: Additional notes about the shot (optional)
  - **referencePrompt**: Midjourney prompt for reference images (required)

## Key Differences from Custom Implementations

This example shows the proper way to define tools using the official Anthropic SDK, replacing custom classes like:

- `DeepseekTool` → `com.anthropic.models.messages.Tool`
- `DeepseekToolsFunction` → Built into Tool builder
- `Parameters` → `com.anthropic.models.messages.Tool.InputSchema`

## Usage

To run this example:

```bash
# Set your API key
export ANTHROPIC_API_KEY=your_api_key_here

# Run the example
./gradlew :anthropic-java-example:run -Pexample=ShotBreakdownTool
```

## Schema Structure

The tool uses the official SDK's `InputSchema.builder()` and `JsonValue.from()` methods to create the schema:

```java
InputSchema schema = InputSchema.builder()
    .properties(createSchemaProperties())
    .addRequired("shots")
    .build();
```

Properties are defined using standard Java Maps and converted to JsonValue:

```java
Map<String, Object> properties = new LinkedHashMap<>();
properties.put("shots", createShotsProperty());
return JsonValue.from(properties);
```

## Camera Angles

The example includes a comprehensive list of camera angles and movements commonly used in filmmaking:

- Wide shots: Extreme Wide, Wide, Medium Wide
- Close-ups: Medium Close Up, Close Up, Extreme Close Up
- Camera movements: Pan, Tilt, Dolly, Truck, Zoom
- Special angles: POV, Drone, Low Angle, High Angle, Dutch Angle, etc.
