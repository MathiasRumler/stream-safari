package mvp.streamy.models;

public record RiddleDefinition(
    String id,
    String description,
    String dataset,
    String solution
) {}