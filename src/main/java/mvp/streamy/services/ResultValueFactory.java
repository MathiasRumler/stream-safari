package mvp.streamy.services;

import mvp.streamy.models.ResultType;
import mvp.streamy.models.ResultValue;
import mvp.streamy.models.SafariAnimal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ResultValueFactory {

    public static ResultValue from(Object value) {
        if (value == null) {
            return new ResultValue(ResultType.NUMBER, null); // or special case
        }

        if (value instanceof Number) {
            return new ResultValue(ResultType.NUMBER, value);
        }

        if (value instanceof Map<?, ?> map) {
            return new ResultValue(ResultType.MAP, map);
        }

        if (value instanceof Collection<?> collection) {
            return new ResultValue(ResultType.LIST, List.copyOf(collection));
        }

        if (value instanceof SafariAnimal safariAnimal) {
            return new ResultValue(ResultType.SAFARIANIMAL, safariAnimal);
        }

        throw new IllegalArgumentException(
                "Unsupported result type: " + value.getClass()
        );
    }

    private ResultValueFactory() {}
}
