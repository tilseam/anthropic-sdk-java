# Migration Guide: From Deepseek Tool Style to Official Anthropic SDK

This document shows how to migrate from custom Deepseek-style tool definitions to the official Anthropic SDK format.

## Key Changes

### 1. Import Statements

**Before (Deepseek Style):**
```java
import org.ruoyi.common.chat.entity.chat.Parameters;
import org.ruoyi.common.chat.entity.chat.tool.DeepseekToolsFunction;
import org.ruoyi.common.chat.entity.chat.tool.DeepseekTool;
```

**After (Official Anthropic SDK):**
```java
import com.anthropic.core.JsonValue;
import com.anthropic.models.messages.Tool;
import com.anthropic.models.messages.Tool.InputSchema;
```

### 2. Tool Creation

**Before (Deepseek Style):**
```java
DeepseekTool tools = new DeepseekTool();
tools.setType("function");

DeepseekToolsFunction function = new DeepseekToolsFunction();
function.setName("generateShotBreakdown");
function.setDescription("...");
function.setParameters(createParameters());

tools.setFunction(function);
```

**After (Official Anthropic SDK):**
```java
Tool tool = Tool.builder()
    .name("generateShotBreakdown")
    .description("...")
    .inputSchema(schema)
    .build();
```

### 3. Schema Definition

**Before (Deepseek Style):**
```java
private static Parameters createParameters() {
    return Parameters.builder()
        .type("object")
        .properties(createProperties())
        .required(List.of("shots"))
        .build();
}
```

**After (Official Anthropic SDK):**
```java
InputSchema schema = InputSchema.builder()
    .properties(createSchemaProperties())
    .addRequired("shots")
    .build();
```

### 4. Properties Conversion

**Before (Deepseek Style):**
```java
Map<String, Object> properties = new LinkedHashMap<>();
properties.put("shots", createShotsProperty());
// Returns Map<String, Object> directly
```

**After (Official Anthropic SDK):**
```java
Map<String, Object> properties = new LinkedHashMap<>();
properties.put("shots", createShotsProperty());
return JsonValue.from(properties);  // Wrap in JsonValue
```

### 5. Complete Example Comparison

#### Deepseek Style (Custom Implementation)

```java
public static final DeepseekTool TOOL = createGenerateShotBreakdownTool();

private static DeepseekTool createGenerateShotBreakdownTool() {
    DeepseekTool tools = new DeepseekTool();
    tools.setType("function");

    DeepseekToolsFunction function = new DeepseekToolsFunction();
    function.setName("generateShotBreakdown");
    function.setDescription("根据输入的脚本文本，生成电影或视频的镜头分解列表...");
    function.setParameters(createParameters());

    tools.setFunction(function);
    return tools;
}

private static Parameters createParameters() {
    return Parameters.builder()
        .type("object")
        .properties(createProperties())
        .required(List.of("shots"))
        .build();
}
```

#### Official Anthropic SDK Style

```java
private static Tool createGenerateShotBreakdownTool() {
    InputSchema schema = InputSchema.builder()
        .properties(createSchemaProperties())
        .addRequired("shots")
        .build();

    return Tool.builder()
        .name("generateShotBreakdown")
        .description("根据输入的脚本文本，生成电影或视频的镜头分解列表...")
        .inputSchema(schema)
        .build();
}

private static JsonValue createSchemaProperties() {
    Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("shots", createShotsProperty());
    return JsonValue.from(properties);
}
```

## Benefits of Official SDK Approach

1. **Type Safety**: Uses official SDK types with proper validation
2. **Builder Pattern**: Fluent and intuitive API
3. **Compatibility**: Guaranteed to work with Anthropic API updates
4. **Documentation**: Well-documented with Javadoc
5. **Maintenance**: Actively maintained by Anthropic

## Usage with Anthropic Client

```java
AnthropicClient client = AnthropicOkHttpClient.fromEnv();

Tool shotBreakdownTool = createGenerateShotBreakdownTool();

MessageCreateParams createParams = MessageCreateParams.builder()
    .model(Model.CLAUDE_SONNET_4_20250514)
    .maxTokens(4096)
    .addTool(shotBreakdownTool)
    .toolChoice(ToolChoiceTool.builder()
        .name("generateShotBreakdown")
        .build())
    .addUserMessage("Generate a shot breakdown for...")
    .build();

client.messages().create(createParams);
```

## Complete Working Example

See `ShotBreakdownToolExample.java` for a complete, working implementation using the official Anthropic SDK.
