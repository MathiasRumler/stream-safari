package mvp.streamy.Repository;

import mvp.streamy.models.Riddle;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

@Repository
public class RiddleRepository {

    private final Map<String, Riddle> riddles =
            Map.of(
                    "1", new Riddle(
                            "1",
                            "Sort the list in ascending order",
                            Arrays.asList(5, 3, 1, 4, 2),
                            Arrays.asList(1, 2, 3, 4, 5)
                    ),
                    "2", new Riddle(
                            "2",
                            "Remove duplicates and sort",
                            Arrays.asList(3, 1, 2, 3, 1),
                            Arrays.asList(1, 2, 3)
                    ),
                    "3", new Riddle(
                            "3",
                            "Double Values",
                            Arrays.asList(3, 1, 2, 3, 1),
                            Arrays.asList(6, 2, 4 , 6, 2)
                    )
            );

    public List<Riddle> findAll() {
        return List.copyOf(riddles.values());
    }

    public Riddle findById(String id) {
        return riddles.get(id);
    }
}
