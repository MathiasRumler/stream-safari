package mvp.streamy.Repository;

import mvp.streamy.models.SafariAnimal;
import mvp.streamy.models.AnimalSpecies;
import mvp.streamy.models.AnimalClass;

import java.util.List;

public final class SafariScenarios {

    private SafariScenarios() {}

    /** Base dataset used by most riddles */
    public static final List<SafariAnimal> BASE_ANIMALS = List.of(
            new SafariAnimal("Hyena", AnimalSpecies.HYENA, AnimalClass.MAMMAL, 8, 60, true),
            new SafariAnimal("Zebra", AnimalSpecies.ZEBRA, AnimalClass.MAMMAL, 10, 300, false),
            new SafariAnimal("Lion", AnimalSpecies.LION, AnimalClass.MAMMAL, 12, 190, true),
            new SafariAnimal("Giraffe", AnimalSpecies.GIRAFFE, AnimalClass.MAMMAL, 15, 800, false),
            new SafariAnimal("Elephant", AnimalSpecies.ELEPHANT, AnimalClass.MAMMAL, 25, 6000, false)
    );

    public static  List<String> uppercase() {
        return BASE_ANIMALS.stream().map(a -> a.name().toUpperCase())
                .toList();
    }


    /** Dataset with duplicates */
    public static final List<SafariAnimal> WITH_DUPLICATES = List.of(
            BASE_ANIMALS.get(0),
            BASE_ANIMALS.get(1),
            BASE_ANIMALS.get(0),
            BASE_ANIMALS.get(3),
            BASE_ANIMALS.get(3)
    );

    /** Mixed species for grouping riddles */
    public static final List<SafariAnimal> MIXED_SPECIES = List.of(
            new SafariAnimal(
                    "Lion",
                    AnimalSpecies.LION,
                    AnimalClass.MAMMAL,
                    12,
                    190,
                    true
            ),
            new SafariAnimal(
                    "Elephant",
                    AnimalSpecies.ELEPHANT,
                    AnimalClass.MAMMAL,
                    25,
                    6000,
                    false
            ),
            new SafariAnimal(
                    "Crocodile",
                    AnimalSpecies.CROCODILE,
                    AnimalClass.REPTILE,
                    40,
                    500,
                    true
            ),
            new SafariAnimal(
                    "Lizard",
                    AnimalSpecies.LIZARD,
                    AnimalClass.REPTILE,
                    2,
                    3,
                    false
            )
    );
}
