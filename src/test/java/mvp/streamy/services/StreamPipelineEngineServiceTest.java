package mvp.streamy.services;


import mvp.streamy.Repository.RiddleRepository;
import mvp.streamy.models.ResultValue;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.SafariAnimal;
import mvp.streamy.services.ResultValueFactory;
import mvp.streamy.services.StreamPipelineEngineServiceV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamPipelineEngineServiceTest {

    private StreamPipelineEngineServiceV2 engine;
    private RiddleRepository riddleRepository;
    private mvp.streamy.services.ResultValueFactory resultValueFactory;

    @BeforeEach
    void setup() {
        engine = new StreamPipelineEngineServiceV2();
        riddleRepository = new RiddleRepository();
    }

    @Test
    void riddle1_orderAnimalsByAge() {
        Riddle riddle = riddleRepository.findById("1");

        String pipeline = """
                .sorted(Comparator.comparing(SafariAnimal::age))
                .toList()
                """;

        Object rawResult =
                engine.execute(riddle.input(), pipeline, SafariAnimal.class);

        ResultValue actual =
                ResultValueFactory.from(rawResult);

        assertEquals(riddle.expectedOutput(), actual.value());
    }

    @Test
    void riddle2_removeDuplicates() {
        Riddle riddle = riddleRepository.findById("2");

        String pipeline = """
                .distinct()
                .toList()
                """;

        Object rawResult =
                engine.execute(riddle.input(), pipeline, SafariAnimal.class);

        ResultValue actual =
                ResultValueFactory.from(rawResult);

        assertEquals(riddle.expectedOutput(), actual.value());
    }

    @Test
    void riddle4_findHeaviestAnimal() {
        Riddle riddle = riddleRepository.findById("4");

        String pipeline = """
                .max(Comparator.comparing(SafariAnimal::weight))
                .orElseThrow()
                """;

        Object rawResult =
                engine.execute(riddle.input(), pipeline, SafariAnimal.class);

        ResultValue actual =
                ResultValueFactory.from(rawResult);

        assertEquals(riddle.expectedOutput(), actual.value());
    }

    @Test
    void riddle5_uppercaseAnimalNames() {
        Riddle riddle = riddleRepository.findById("5");

        String pipeline = """
                .map(a -> a.name().toUpperCase())
                .toList()
                """;

        Object rawResult =
                engine.execute(riddle.input(), pipeline, SafariAnimal.class);

        ResultValue actual =
                ResultValueFactory.from(rawResult);

        assertEquals(riddle.expectedOutput(), actual.value());
    }

    @Test
    void riddle6_findPredators() {
        Riddle riddle = riddleRepository.findById("6");

        String pipeline = """
                .filter(SafariAnimal::predator)
                .toList()
                """;

        Object rawResult =
                engine.execute(riddle.input(), pipeline, SafariAnimal.class);

        ResultValue actual =
                ResultValueFactory.from(rawResult);

        assertEquals(riddle.expectedOutput(), actual.value());
    }

    @Test
    void riddle7_heaviestAnimalPerSpecies() {
        Riddle riddle = riddleRepository.findById("7");

        String pipeline = """
                .collect(
                    Collectors.groupingBy(
                        SafariAnimal::species,
                        Collectors.collectingAndThen(
                            Collectors.maxBy(
                                Comparator.comparing(SafariAnimal::weight)
                            ),
                            Optional::get
                        )
                    )
                )
                """;

        Object rawResult =
                engine.execute(riddle.input(), pipeline, SafariAnimal.class);

        ResultValue actual =
                ResultValueFactory.from(rawResult);

        assertEquals(riddle.expectedOutput(), actual.value());
    }
}
