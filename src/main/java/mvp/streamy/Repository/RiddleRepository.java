package mvp.streamy.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.SafariAnimal;
import org.springframework.stereotype.Repository;

@Repository
public class RiddleRepository {

  private final Map<String, Riddle> riddles = Map.of(
      "1", new Riddle(
          "1",
          "Order animals by age (youngest first)",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.orderedByAge()
      ),

      "2", new Riddle(
          "2",
          "Remove duplicate animals",
          SafariAnimal.class,
          SafariScenarios.WITH_DUPLICATES,
          SafariScenarios.withoutDuplicates()
      ),

      "4", new Riddle(
          "4",
          "Find the heaviest animal",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.heaviestAnimal()
      ),

      "5", new Riddle(
          "5",
          "Get the weight of all animals combined",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.sumAnimalWeight()
      ),

      "6", new Riddle(
          "6",
          "Find all predators",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.predators()
      ),

      "7", new Riddle(
          "7",
          "Find the heaviest animal per species",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.heaviestPerSpecies()
      ),
      "8", new Riddle(
          "8",
          "Group animals into weight ranges: 0-200, 200-500, 500-1000, 1000+",
          SafariAnimal.class,
          SafariScenarios.BASE_ANIMALS,
          SafariScenarios.groupAnimalsByWeightRange()
      )
  );


  public List<Riddle> findAll() {
    return new ArrayList<>(riddles.values());
  }

  public Riddle findById(String id) {
    return riddles.get(id);
  }
}