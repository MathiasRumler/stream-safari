package mvp.streamy.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

public class StreamPipelineEngineServiceAbuseTest {

    private final StreamPipelineEngineServiceV2 engine2 =
            new StreamPipelineEngineServiceV2();

    @Test
    void testRiddleInput() {

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
                        () -> engine2.execute(input, overflowPiepline,Object.class)
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

        UnsupportedOperationException exception =
                assertThrows(
                        UnsupportedOperationException.class,
                        () -> engine2.execute(input, invalidPipeline, Integer.class)
                );

        assertTrue(
                exception.getMessage().contains("Forbidden construct"),
                "Expected validation error for forbidden pipeline"
        );
    }

    @Test
    void shouldTimeoutOnInfiniteLoop() {
        List<Integer> input = List.of(1);

        // This pipeline creates an infinite stream and tries to count it.
        // count() will run forever without allocating memory, allowing the timeout to trigger.
        String infinitePipeline =
                """
                .flatMap(x -> java.util.stream.Stream.generate(() -> 1))
                .count()
                """;

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> engine2.execute(input, infinitePipeline, Integer.class)
                );

        assertTrue(
                exception.getMessage().contains("Execution timed out"),
                "Expected timeout error for infinite loop"
        );
    }
}