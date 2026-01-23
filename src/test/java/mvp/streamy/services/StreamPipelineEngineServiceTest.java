package mvp.streamy.services;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class StreamPipelineEngineTest {

    private final StreamPipelineEngineService engine =
            new StreamPipelineEngineService();

    @Test
    void shouldSortAndDistinctListUsingStreamPipeline() {

        List<Integer> input =
                List.of(5, 2, 9, 1, 2, 5);

        String pipeline =
                """
                .stream()
                .distinct()
                .sorted()
                .toList()
                """;

        List<Integer> result =
                engine.execute(input, pipeline);

        assertEquals(
                List.of(1, 2, 5, 9),
                result
        );
    }

    @Test
    void shouldRejectForbiddenOperationsInPipeline() {

        List<Integer> input =
                List.of(1, 2, 3);

        String invalidPipeline =
                """
                .stream()
                .peek(System.out::println)
                .toList()
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

    @Test
    void shouldRejectPipelineWithoutStreamStart() {

        List<Integer> input =
                List.of(1, 2, 3);

        String invalidPipeline =
                """
                .sorted()
                .toList()
                """;

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> engine.execute(input, invalidPipeline)
                );

        assertTrue(
                exception.getMessage().contains("start with .stream()")
        );
    }
}
