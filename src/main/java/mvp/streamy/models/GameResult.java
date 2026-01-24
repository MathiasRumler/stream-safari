package mvp.streamy.models;

import java.util.List;

public record GameResult (
        boolean success,
        List<Integer>output,
        String message
) {
}
