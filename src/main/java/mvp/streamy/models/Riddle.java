package mvp.streamy.models;

import java.util.List;

public record Riddle(

        String id,
        String description,
        Class<?> dataType,
        List<SafariAnimal>input,
        Object expectedOutput

//        Set<String> allowedOperations,
//        Set<String>forbiddenOperations
) {
}
