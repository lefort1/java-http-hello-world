package mike706574;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Prop {
    public static String required(Map<String, String> config, String key) {
        if (!config.containsKey(key)) {
            String message = String.format("Missing required configuration property: %s",
                    key);
            throw new NoSuchElementException(message);
        }

        String value = config.get(key);

        if (value.trim().equals("")) {
            String message = String.format("Value for configuration property %s was blank.",
                    key);
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public static Optional<String> optional(Map<String, String> config, String key) {
        if (!config.containsKey(key)) {
            return Optional.empty();
        }

        String value = config.get(key);

        if (value.trim().equals("")) {
            String message = String.format("Value for configuration property %s was blank.",
                    key);
            throw new IllegalArgumentException(message);
        }

        return Optional.of(value);
    }
}
