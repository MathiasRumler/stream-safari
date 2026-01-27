package mvp.streamy.models;

import java.util.List;

public record GameResult<T> (
        boolean success,
        List<T>output,
        String message
) {
}
