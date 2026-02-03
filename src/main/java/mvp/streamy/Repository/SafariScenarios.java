package mvp.streamy.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mvp.streamy.models.AnimalClass;
import mvp.streamy.models.AnimalSpecies;
import mvp.streamy.models.SafariAnimal;

public final class SafariScenarios {

  private SafariScenarios() {
  }

  /**
   * Base dataset used by most riddles
   */
  public static final List<SafariAnimal> BASE_ANIMALS = List.of(
      new SafariAnimal("Henno the Hyena", AnimalSpecies.HYENA, AnimalClass.MAMMAL, 8, 60, true),
      new SafariAnimal("Zembet the Zebra", AnimalSpecies.ZEBRA, AnimalClass.MAMMAL, 10, 300, false),
      new SafariAnimal("Leo the Lion", AnimalSpecies.LION, AnimalClass.MAMMAL, 12, 190, true),
      new SafariAnimal("Gigi the Giraffe", AnimalSpecies.GIRAFFE, AnimalClass.MAMMAL, 15, 800, false),
      new SafariAnimal("Ello the Elephant", AnimalSpecies.ELEPHANT, AnimalClass.MAMMAL, 25, 6000, false),

      new SafariAnimal("Hakka the Hyena", AnimalSpecies.HYENA, AnimalClass.MAMMAL, 6, 55, true),
      new SafariAnimal("Zuri the Zebra", AnimalSpecies.ZEBRA, AnimalClass.MAMMAL, 7, 280, false),
      new SafariAnimal("Luma the Lion", AnimalSpecies.LION, AnimalClass.MAMMAL, 9, 170, true),
      new SafariAnimal("Garo the Giraffe", AnimalSpecies.GIRAFFE, AnimalClass.MAMMAL, 5, 600, false),
      new SafariAnimal("Motu the Elephant", AnimalSpecies.ELEPHANT, AnimalClass.MAMMAL, 30, 6500, false)
  );


  public static List<String> uppercase() {
    return BASE_ANIMALS.stream().map(a -> a.name().toUpperCase())
        .toList();
  }

  public static int sumAnimalWeight() {
    return BASE_ANIMALS.stream().mapToInt(SafariAnimal::weight).sum();
  }

  public static List<SafariAnimal> orderedByAge() {
    return BASE_ANIMALS.stream()
        .sorted(Comparator.comparing(SafariAnimal::age))
        .toList();
  }

  public static List<SafariAnimal> withoutDuplicates() {
    return WITH_DUPLICATES.stream()
        .distinct()
        .toList();
  }

  public static SafariAnimal heaviestAnimal() {
    return BASE_ANIMALS.stream()
        .max(Comparator.comparing(SafariAnimal::weight))
        .orElseThrow();
  }

  public static List<SafariAnimal> predators() {
    return BASE_ANIMALS.stream()
        .filter(SafariAnimal::predator)
        .toList();
  }

  public static Map<AnimalSpecies, SafariAnimal> heaviestPerSpecies() {
    return BASE_ANIMALS.stream()
        .collect(
            Collectors.groupingBy(
                SafariAnimal::species,
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparing(SafariAnimal::weight)),
                    Optional::get
                )
            )
        );
  }


  /**
   * Dataset with duplicates
   */
  public static final List<SafariAnimal> WITH_DUPLICATES = List.of(
      BASE_ANIMALS.get(0),
      BASE_ANIMALS.get(1),
      BASE_ANIMALS.get(0),
      BASE_ANIMALS.get(3),
      BASE_ANIMALS.get(3)
  );

  /**
   * Mixed species for grouping riddles
   */
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
