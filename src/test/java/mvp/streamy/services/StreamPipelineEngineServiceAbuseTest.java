package mvp.streamy.services;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> engine2.execute(input, invalidPipeline, Integer.class)
                );

        assertTrue(
                exception.getMessage().contains("Forbidden construct"),
                "Expected validation error for forbidden pipeline"
        );
    }
}
