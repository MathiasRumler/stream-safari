package mvp.streamy.Repository;

import java.util.List;
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
      new SafariAnimal("Ello the Elephant", AnimalSpecies.ELEPHANT, AnimalClass.MAMMAL, 45, 6000, false),

      new SafariAnimal("Hakka the Hyena", AnimalSpecies.HYENA, AnimalClass.MAMMAL, 6, 55, true),
      new SafariAnimal("Zuri the Zebra", AnimalSpecies.ZEBRA, AnimalClass.MAMMAL, 7, 280, false),
      new SafariAnimal("Luma the Lion", AnimalSpecies.LION, AnimalClass.MAMMAL, 9, 170, true),
      new SafariAnimal("Garo the Giraffe", AnimalSpecies.GIRAFFE, AnimalClass.MAMMAL, 5, 600, false),
      new SafariAnimal("Motu the Elephant", AnimalSpecies.ELEPHANT, AnimalClass.MAMMAL, 30, 6500, false)
  );

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

}
