package mvp.streamy.services;


import static org.junit.jupiter.api.Assertions.assertEquals;

import mvp.streamy.Repository.RiddleRepository;
import mvp.streamy.models.ResultValue;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.SafariAnimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
   String pipeline2 = """
        .sorted()
        .toList()
        """;
    String pipeline3 = """
        .sorted(SafariAnimal.BY_WEIGHT)
        .toList()
        """;
    Object rawResult =
        engine.execute(riddle.input(), pipeline, SafariAnimal.class);

    Object rawResult2 =
        engine.execute(riddle.input(), pipeline2, SafariAnimal.class);

    Object rawResult3 =
        engine.execute(riddle.input(), pipeline3, SafariAnimal.class);

    ResultValue actual =
        ResultValueFactory.from(rawResult);

    ResultValue actual2 =
        ResultValueFactory.from(rawResult2);

    ResultValue actual3 =
        ResultValueFactory.from(rawResult3);

    assertEquals(riddle.expectedOutput(), actual.value());
    assertEquals(riddle.expectedOutput(), actual2.value());
    assertEquals(riddle.expectedOutput(), actual3.value());


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
  void riddle5_sumAnimalWeights() {

    Riddle riddle =
        riddleRepository.findById("5");

    String pipeline = """
        .mapToInt(SafariAnimal::weight)
        .sum()
        """;

    Object rawResult =
        engine.execute(
            riddle.input(),
            pipeline,
            SafariAnimal.class
        );

    ResultValue actual =
        ResultValueFactory.from(rawResult);

    assertEquals(
        riddle.expectedOutput(),
        actual.value()
    );
  }


  @Test
  void riddle6_findPredators() {
    Riddle riddle = riddleRepository.findById("6");

    String pipeline = """
        .filter(SafariAnimal::predator)
        .toList()
        """;
  riddle.input().stream().filter(SafariAnimal::predator);
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

  @Test
  void riddle8_groupAnimalsByWeightRange() {

    Riddle riddle =
        riddleRepository.findById("8");

    String pipeline = """
        .collect(Collectors.groupingBy(a -> {
            int w = a.weight();
            if (w <= 200) return "0-200";
            if (w <= 500) return "200-500";
            if (w <= 1000) return "500-1000";
            return "1000+";
        }))
        """;

    Object rawResult =
        engine.execute(
            riddle.input(),
            pipeline,
            SafariAnimal.class
        );

    ResultValue actual =
        ResultValueFactory.from(rawResult);

    assertEquals(
        riddle.expectedOutput(),
        actual.value()
    );
  }

}
