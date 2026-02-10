package mvp.streamy.models;

import java.util.Comparator;

public record SafariAnimal(
        String name,
        AnimalSpecies species,
        AnimalClass animalClass,
        int age,
        int weight,
        boolean predator
) implements Comparable<SafariAnimal> {

  @Override
  public int compareTo(SafariAnimal other) {
    return Integer.compare(this.age, other.age);
  }

  public static final Comparator<SafariAnimal> BY_AGE =
      Comparator.comparingInt(SafariAnimal::age);

  public static final Comparator<SafariAnimal> BY_WEIGHT =
      Comparator.comparingInt(SafariAnimal::weight);

  public static final Comparator<SafariAnimal> BY_NAME =
      Comparator.comparing(SafariAnimal::name);

  public static final Comparator<SafariAnimal> PREDATORS_FIRST =
      Comparator.comparing(SafariAnimal::predator).reversed();
}