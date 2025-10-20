package com.anthropic.example;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.JsonValue;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.Tool;
import com.anthropic.models.messages.Tool.InputSchema;
import com.anthropic.models.messages.ToolChoiceTool;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating how to create a shot breakdown tool using the official Anthropic SDK.
 *
 * This tool generates a shot breakdown list for movie or video scripts, including shot descriptions,
 * angles, dialogue, notes, and Midjourney prompts for reference images.
 */
public final class ShotBreakdownToolExample {
    private ShotBreakdownToolExample() {}

    // Shot angle constants
    private static final List<String> SHOT_ANGLES = Arrays.asList(
            "Extreme Wide",
            "Wide",
            "Medium Wide",
            "Medium",
            "Medium Close Up",
            "Close Up",
            "Extreme Close Up",
            "Point of View",
            "Drone",
            "Low Angle",
            "High Angle",
            "Over-the-Shoulder",
            "Dutch Angle",
            "Pan",
            "Pan Left",
            "Pan Right",
            "Tilt",
            "Tilt Up",
            "Tilt Down",
            "Dolly",
            "Push In",
            "Pull Out",
            "Truck",
            "Truck Left",
            "Truck Right",
            "Zoom In",
            "Zoom Out",
            "Rack Focus",
            "Roll",
            "Waypoint Flight");

    public static void main(String[] args) {
        // Configures using the `ANTHROPIC_API_KEY` environment variable
        AnthropicClient client = AnthropicOkHttpClient.fromEnv();

        // Create the tool definition
        Tool shotBreakdownTool = createGenerateShotBreakdownTool();

        // Create the message request
        MessageCreateParams createParams = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_4_20250514)
                .maxTokens(4096)
                .addTool(shotBreakdownTool)
                .toolChoice(ToolChoiceTool.builder()
                        .name("generateShotBreakdown")
                        .build())
                .addUserMessage(
                        "Generate a shot breakdown for a short scene: A detective enters a dimly lit office and discovers a mysterious letter on the desk.")
                .build();

        // Execute the request and print the tool use results
        client.messages().create(createParams).content().stream()
                .flatMap(contentBlock -> contentBlock.toolUse().stream())
                .forEach(toolUseBlock -> {
                    System.out.println("Tool: " + toolUseBlock.name());
                    System.out.println("Input: " + toolUseBlock._input());
                });
    }

    /**
     * Creates the shot breakdown tool definition using the official Anthropic SDK format.
     */
    private static Tool createGenerateShotBreakdownTool() {
        InputSchema schema = InputSchema.builder()
                .properties(createSchemaProperties())
                .addRequired("shots")
                .build();

        return Tool.builder()
                .name("generateShotBreakdown")
                .description(
                        "根据输入的脚本文本，生成电影或视频的镜头分解列表，包含每个镜头的描述、角度、台词、注释、镜头参考图像的Midjourney Prompt等信息。")
                .inputSchema(schema)
                .build();
    }

    /**
     * Creates the properties for the tool's input schema.
     */
    private static JsonValue createSchemaProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("shots", createShotsProperty());
        return JsonValue.from(properties);
    }

    /**
     * Creates the "shots" array property definition.
     */
    private static Map<String, Object> createShotsProperty() {
        Map<String, Object> shotsProperty = new LinkedHashMap<>();
        shotsProperty.put("type", "array");
        shotsProperty.put("description", "镜头列表");
        shotsProperty.put("items", createShotItemSchema());
        return shotsProperty;
    }

    /**
     * Creates the schema for individual shot items.
     */
    private static Map<String, Object> createShotItemSchema() {
        Map<String, Object> items = new LinkedHashMap<>();
        items.put("type", "object");
        items.put("properties", createShotProperties());
        items.put("required", Arrays.asList("description", "angle", "referencePrompt"));
        return items;
    }

    /**
     * Creates the properties for individual shot objects.
     */
    private static Map<String, Object> createShotProperties() {
        Map<String, Object> shotProperties = new LinkedHashMap<>();
        shotProperties.put("description", createStringProperty("镜头的描述"));
        shotProperties.put("angle", createAngleProperty());
        shotProperties.put("script", createStringProperty("镜头对应的台词内容，可为空"));
        shotProperties.put("note", createStringProperty("关于镜头的额外注释，可为空"));
        shotProperties.put(
                "referencePrompt",
                createStringProperty("镜头参考图像的Midjourney Prompt，描述镜头的人物、场景、元素和景别"));
        return shotProperties;
    }

    /**
     * Creates a string property with description.
     */
    private static Map<String, Object> createStringProperty(String description) {
        Map<String, Object> prop = new LinkedHashMap<>();
        prop.put("type", "string");
        prop.put("description", description);
        return prop;
    }

    /**
     * Creates the angle property (array of strings with enum values).
     */
    private static Map<String, Object> createAngleProperty() {
        Map<String, Object> angleProp = new LinkedHashMap<>();
        angleProp.put("type", "array");
        angleProp.put("description", "镜头角度或者运动列表");

        Map<String, Object> angleItems = new LinkedHashMap<>();
        angleItems.put("type", "string");
        angleItems.put("enum", SHOT_ANGLES);
        angleProp.put("items", angleItems);

        return angleProp;
    }
}
