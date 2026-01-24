package mvp.streamy.models;

import java.util.List;

public record Riddle(

        String id,
        String description,

        List<Integer>input,
        List<Integer> expectedOutput

//        Set<String> allowedOperations,
//        Set<String>forbiddenOperations
) {
}
