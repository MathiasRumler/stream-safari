package mvp.streamy.models;

public record SafariAnimal(
        String name,
        AnimalSpecies species,
        AnimalClass animalClass,
        int age,
        int weight,
        boolean predator
) {
}