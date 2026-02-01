package mvp.streamy.models;


public record RiddleResult(
        boolean success,
        ResultValue result,
        String message
) {}
