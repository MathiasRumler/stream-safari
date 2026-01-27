package mvp.streamy.models;

import java.util.List;

public record Riddle<T>(

        String id,
        String description,
        Class<?> dataType,
        List<T>input,
        List<T> expectedOutput

//        Set<String> allowedOperations,
//        Set<String>forbiddenOperations
) {
}
