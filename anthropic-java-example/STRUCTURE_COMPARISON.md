# Tool Definition Structure Comparison

## Original Deepseek Structure vs Official Anthropic SDK

### Structure Overview

```
Original Deepseek:
└── DeepseekTool
    ├── type: "function"
    └── function: DeepseekToolsFunction
        ├── name: "generateShotBreakdown"
        ├── description: "..."
        └── parameters: Parameters
            ├── type: "object"
            ├── properties: Map<String, Object>
            └── required: List<String>

Official Anthropic SDK:
└── Tool
    ├── name: "generateShotBreakdown"
    ├── description: "..."
    └── inputSchema: InputSchema
        ├── type: "object" (implicit)
        ├── properties: JsonValue
        └── required: List<String>
```

### Complete Field Mapping

| Original (Deepseek)               | Official (Anthropic SDK)          | Notes                           |
|-----------------------------------|-----------------------------------|---------------------------------|
| `DeepseekTool`                    | `Tool`                            | Main tool class                 |
| `DeepseekTool.type`               | _(not needed)_                    | Implicit in Tool                |
| `DeepseekToolsFunction`           | _(merged into Tool)_              | Function merged into Tool       |
| `DeepseekToolsFunction.name`      | `Tool.name`                       | Direct mapping                  |
| `DeepseekToolsFunction.description`| `Tool.description`               | Direct mapping                  |
| `Parameters`                      | `InputSchema`                     | Schema definition               |
| `Parameters.type`                 | `InputSchema.type`                | Always "object"                 |
| `Parameters.properties`           | `InputSchema.properties`          | Wrapped in JsonValue            |
| `Parameters.required`             | `InputSchema.required`            | Use addRequired() method        |

### Code Transformation Examples

#### 1. Basic Tool Setup

**Original:**
```java
DeepseekTool tools = new DeepseekTool();
tools.setType("function");
DeepseekToolsFunction function = new DeepseekToolsFunction();
function.setName("generateShotBreakdown");
tools.setFunction(function);
```

**Official SDK:**
```java
Tool tool = Tool.builder()
    .name("generateShotBreakdown")
    // type is implicit
    .build();
```

#### 2. Adding Description

**Original:**
```java
function.setDescription("根据输入的脚本文本，生成电影或视频的镜头分解列表...");
```

**Official SDK:**
```java
Tool tool = Tool.builder()
    .name("generateShotBreakdown")
    .description("根据输入的脚本文本，生成电影或视频的镜头分解列表...")
    .build();
```

#### 3. Parameters/Schema Definition

**Original:**
```java
Parameters parameters = Parameters.builder()
    .type("object")
    .properties(createProperties())
    .required(List.of("shots"))
    .build();
function.setParameters(parameters);
```

**Official SDK:**
```java
InputSchema schema = InputSchema.builder()
    // .type("object") is implicit
    .properties(createSchemaProperties())
    .addRequired("shots")
    .build();
    
Tool tool = Tool.builder()
    .name("generateShotBreakdown")
    .inputSchema(schema)
    .build();
```

#### 4. Properties with JsonValue Wrapper

**Original:**
```java
private static Map<String, Object> createProperties() {
    Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("shots", createShotsProperty());
    return properties; // Returns Map directly
}
```

**Official SDK:**
```java
private static JsonValue createSchemaProperties() {
    Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("shots", createShotsProperty());
    return JsonValue.from(properties); // Wrapped in JsonValue
}
```

#### 5. Nested Properties (Arrays and Objects)

Both approaches use the same structure for nested properties:

```java
private static Map<String, Object> createShotsProperty() {
    Map<String, Object> shotsProperty = new LinkedHashMap<>();
    shotsProperty.put("type", "array");
    shotsProperty.put("description", "镜头列表");
    shotsProperty.put("items", createShotItemSchema());
    return shotsProperty;
}

private static Map<String, Object> createShotItemSchema() {
    Map<String, Object> items = new LinkedHashMap<>();
    items.put("type", "object");
    items.put("properties", createShotProperties());
    items.put("required", Arrays.asList("description", "angle", "referencePrompt"));
    return items;
}
```

### Key Differences Summary

1. **Fewer Classes**: No need for separate `DeepseekToolsFunction` class
2. **Builder Pattern**: Consistent use of builders throughout
3. **JsonValue Wrapper**: Properties must be wrapped in `JsonValue.from()`
4. **Type Field**: No explicit type field needed (implicit as "custom")
5. **Required Fields**: Use `addRequired()` method instead of setting list directly

### Migration Checklist

- [ ] Replace `DeepseekTool` with `Tool`
- [ ] Remove `DeepseekToolsFunction` and merge into `Tool.builder()`
- [ ] Replace `Parameters` with `InputSchema`
- [ ] Wrap top-level properties in `JsonValue.from()`
- [ ] Use `addRequired()` instead of `required()` with list
- [ ] Remove explicit `type("function")` calls
- [ ] Update import statements to use `com.anthropic.*`
- [ ] Test with official Anthropic client

### See Also

- `ShotBreakdownToolExample.java` - Complete working example
- `MessagesToolsExample.java` - Simple tool example from official SDK
