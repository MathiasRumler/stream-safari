package mvp.streamy.Repository;

import mvp.streamy.models.Riddle;
import mvp.streamy.models.SafariAnimal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RiddleRepository {

    private final Map<String, Riddle> riddles =
            Map.of(
                    "1", new Riddle(
                            "1",
                            "Order animals by age (youngest first)",
                            SafariAnimal.class,
                            SafariScenarios.BASE,
                            List.of(
                                    new SafariAnimal("Hyena", "MAMMAL", 8, 60, true),
                                    new SafariAnimal("Zebra", "MAMMAL", 10, 300, false),
                                    new SafariAnimal("Lion", "MAMMAL", 12, 190, true),
                                    new SafariAnimal("Giraffe", "MAMMAL", 15, 800, false),
                                    new SafariAnimal("Elephant", "MAMMAL", 25, 6000, false)
                            )
                    ),
                    "2", new Riddle(
                            "2",
                            "Remove duplicate animals",
                            SafariAnimal.class,
                            SafariScenarios.WITH_DUPLICATES,
                            List.of(
                                    SafariScenarios.BASE.get(0),
                                    SafariScenarios.BASE.get(1),
                                    SafariScenarios.BASE.get(3)
                            )
                    ),

                    "4", new Riddle(
                            "4",
                            "Find the heaviest animal",
                            SafariAnimal.class,
                            SafariScenarios.BASE,
                            new SafariAnimal("Elephant", "MAMMAL", 25, 6000, false)
                    ),
                    "5", new Riddle(
                            "5",
                            "Get all animal names in uppercase",
                            SafariAnimal.class,
                            SafariScenarios.BASE,
                            List.of("LION", "ELEPHANT", "GIRAFFE", "HYENA", "ZEBRA")
                    ),
                    "6", new Riddle(
                            "6",
                            "Find all predators",
                            SafariAnimal.class,
                            SafariScenarios.BASE,
                            List.of(
                                    SafariScenarios.BASE.get(0),
                                    SafariScenarios.BASE.get(3)
                            )
                    ),
                    "7", new Riddle(
                            "7",
                            "Find the heaviest animal per species",
                            SafariAnimal.class,
                            SafariScenarios.MIXED_SPECIES,
                            Map.of(
                                    "MAMMAL",
                                    new SafariAnimal("Elephant", "MAMMAL", 25, 6000, false),
                                    "REPTILE",
                                    new SafariAnimal("Crocodile", "REPTILE", 40, 500, true)
                            )
                    )
            );

    public List<Riddle> findAll() {
        return List.copyOf(riddles.values());
    }

    public Riddle findById(String id) {
        return riddles.get(id);
    }
}
