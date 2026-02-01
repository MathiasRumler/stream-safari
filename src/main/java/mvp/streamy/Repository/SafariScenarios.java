package mvp.streamy.Repository;

import mvp.streamy.models.SafariAnimal;

import java.util.List;

public final class SafariScenarios {

    private SafariScenarios() {}

    /** Base dataset used by most riddles */
    public static final List<SafariAnimal> BASE = List.of(
            new SafariAnimal("Lion", "MAMMAL", 12, 190, true),
            new SafariAnimal("Elephant", "MAMMAL", 25, 6000, false),
            new SafariAnimal("Giraffe", "MAMMAL", 15, 800, false),
            new SafariAnimal("Hyena", "MAMMAL", 8, 60, true),
            new SafariAnimal("Zebra", "MAMMAL", 10, 300, false)
    );

    /** Dataset with duplicates */
    public static final List<SafariAnimal> WITH_DUPLICATES = List.of(
            BASE.get(0),
            BASE.get(1),
            BASE.get(0),
            BASE.get(3),
            BASE.get(3)
    );

    /** Mixed species for grouping riddles */
    public static final List<SafariAnimal> MIXED_SPECIES = List.of(
            new SafariAnimal("Lion", "MAMMAL", 12, 190, true),
            new SafariAnimal("Elephant", "MAMMAL", 25, 6000, false),
            new SafariAnimal("Crocodile", "REPTILE", 40, 500, true),
            new SafariAnimal("Lizard", "REPTILE", 2, 3, false)
    );
}