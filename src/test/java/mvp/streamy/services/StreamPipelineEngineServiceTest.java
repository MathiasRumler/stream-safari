package mvp.streamy.services;


import mvp.streamy.models.SafariAnimal;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class StreamPipelineEngineTest {

    private final StreamPipelineEngineService engine =
            new StreamPipelineEngineService();
    private final StreamPipelineEngineServiceV2 engine2 =
            new StreamPipelineEngineServiceV2();

    @Test
    void shouldSortAndDistinctListUsingStreamPipeline() throws ClassNotFoundException {

        List<Integer> input =
                List.of(5, 2, 9, 1, 2, 5);

        String pipeline =
                """
                .distinct()
                .sorted()
                """;

        List<Integer> result =
                engine.execute(input, pipeline);
        List<Integer> result3 = engine2.execute(
                Arrays.asList(1, 2, 3, 4, 5),
                ".filter(n -> n > 2).map(n -> n * 2)",
                Integer.class
        );
        // For strings
        List<String> words = engine2.execute(
                Arrays.asList("hello", "world", "java"),
                ".filter(s -> s.length() > 4).map(String::toUpperCase)",
                String.class
        );
//        List<GameResult> lost = new ArrayList<>();
//        lost.add(new GameResult(true, null, null));
//        List<GameResult> gameList = engine2.execute(lost,".sorted()", GameResult.class);

        assertEquals(List.of("HELLO", "WORLD"),words);

        assertEquals(
                List.of(1, 2, 5, 9),
                result
        );

        List<SafariAnimal> resultAnimals = engine2.execute(
                Arrays.asList(
                        new SafariAnimal("Lion",120,400),
                        new SafariAnimal("Knuu",120,400),
                        new SafariAnimal("Giraffe",120,400)
                ), ".map(e->e)",SafariAnimal.class);

    }

    @Test
    void hack() {

        List<Integer> input =
                List.of(5, 2, 9, 1, 2, 5);

        String overflowPiepline =
                """
                .map(a->a*313213132131231312312312)
                .sorted()
                """;

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> engine.execute(input, overflowPiepline)
                );

        assertTrue(
                exception.getMessage().contains("integer number too large"),
                "Expected validation error for forbidden pipeline"
        );
    }

    @Test
    void shouldRejectForbiddenOperationsInPipeline() {

        List<Integer> input =
                List.of(1, 2, 3);

        String invalidPipeline =
                """
                .peek(System.out::println)
                """;

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> engine.execute(input, invalidPipeline)
                );

        assertTrue(
                exception.getMessage().contains("Forbidden construct"),
                "Expected validation error for forbidden pipeline"
        );
    }

}
