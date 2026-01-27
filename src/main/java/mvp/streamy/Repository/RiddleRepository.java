package mvp.streamy.Repository;

import mvp.streamy.models.Riddle;
import mvp.streamy.models.SafariAnimal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

@Repository
public class RiddleRepository {

    /**
     * Todo Replace
     Class<?> surly = Class.forName("java.lang.Integer");

     */
    private final Map<String, Riddle<?>> riddles =
            Map.of(
                    "1", new Riddle<>(
                            "1",
                            "Sort the list in ascending order",
                            Integer.class,
                            Arrays.asList(5, 3, 1, 4, 2),
                            Arrays.asList(1, 2, 3, 4, 5)
                    ),
                    "2", new Riddle<>(
                            "2",
                            "Remove duplicates and sort",
                            Integer.class,
                            Arrays.asList(3, 1, 2, 3, 1),
                            Arrays.asList(1, 2, 3)
                    ),
                    "3", new Riddle<>(
                            "3",
                            "Double Values",
                            Integer.class,
                            Arrays.asList(3, 1, 2, 3, 1),
                            Arrays.asList(6, 2, 4 , 6, 2)
                    ),
                    "4", new Riddle<>(
                            "4",
                            "Find Max",
                            Integer.class,
                            Arrays.asList(1, 17, 54, 14, 14, 33, 45, -11),
                            Arrays.asList(54)
                    ),
                    "5", new Riddle<>(
                            "5",
                            "Uppercase",
                            String.class,
                            Arrays.asList("hello", "world", "java"),
                            Arrays.asList("HELLO", "WORLD")

                    ),
                    "6", new Riddle<>(
                            "6",
                            "Find the b's",
                            String.class,
                            Arrays.asList("hello", "bello", "jello","bob"),
                            Arrays.asList("bello", "bob")

                    )
                    ,
                    "7", new Riddle<>(
                            "7",
                            "Find the same animals",
                            SafariAnimal.class,
                            Arrays.asList(
                                    new SafariAnimal("Lion",120,400),
                                    new SafariAnimal("Knuu",120,400),
                                    new SafariAnimal("Giraffe",120,400)
                            ),
                            Arrays.asList(
                                    new SafariAnimal("Lion",120,400),
                                    new SafariAnimal("Knuu",120,400),
                                    new SafariAnimal("Giraffe",120,400)
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
