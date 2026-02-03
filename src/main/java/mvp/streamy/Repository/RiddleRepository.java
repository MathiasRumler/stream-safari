package mvp.streamy.Repository;

import mvp.streamy.models.AnimalClass;
import mvp.streamy.models.AnimalSpecies;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.SafariAnimal;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RiddleRepository {

  private final Map<String, Riddle> riddles = Map.of(
      "1", new Riddle(
          "1",
          "Order animals by age (youngest first)",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          List.of(
              SafariScenarios.BASE_ANIMALS.get(0), // Hyena (8)
              SafariScenarios.BASE_ANIMALS.get(1), // Zebra (10)
              SafariScenarios.BASE_ANIMALS.get(2), // Lion (12)
              SafariScenarios.BASE_ANIMALS.get(3), // Giraffe (15)
              SafariScenarios.BASE_ANIMALS.get(4)  // Elephant (25)
          )
      ),

      "2", new Riddle(
          "2",
          "Remove duplicate animals",
          SafariAnimal.class,
          // Creating a list with duplicates for the source data
          SafariScenarios.WITH_DUPLICATES,
          List.of(SafariScenarios.BASE_ANIMALS.get(0),
              SafariScenarios.BASE_ANIMALS.get(1),
              SafariScenarios.BASE_ANIMALS.get(3))
      ),

      "4", new Riddle(
          "4",
          "Find the heaviest animal",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          new SafariAnimal("Elephant", AnimalSpecies.ELEPHANT, AnimalClass.MAMMAL, 25, 6000,
              false) // Elephant (6000)
      ),

      "5", new Riddle(
          "5",
          "Get all animal names in uppercase",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.uppercase()),

      "6", new Riddle(
          "6",
          "Find all predators",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          List.of(
              new SafariAnimal("Hyena", AnimalSpecies.HYENA, AnimalClass.MAMMAL, 8, 60, true),
              new SafariAnimal("Lion", AnimalSpecies.LION, AnimalClass.MAMMAL, 12, 190, true)

          )
      ),

      "7", new Riddle(
          "7",
          "Find the heaviest animal per species",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          Map.of(
              AnimalSpecies.ELEPHANT, SafariScenarios.BASE_ANIMALS.get(4),
              AnimalSpecies.GIRAFFE, SafariScenarios.BASE_ANIMALS.get(3),
              AnimalSpecies.LION, SafariScenarios.BASE_ANIMALS.get(2),
              AnimalSpecies.ZEBRA, SafariScenarios.BASE_ANIMALS.get(1),
              AnimalSpecies.HYENA, SafariScenarios.BASE_ANIMALS.get(0)
          )
      )
  );

  public List<Riddle> findAll() {
    return new ArrayList<>(riddles.values());
  }

  public Riddle findById(String id) {
    return riddles.get(id);
  }
}