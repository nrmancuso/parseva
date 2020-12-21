package parseva.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class TokenUtils {

    /**
     * Creates a map of 'field name' to 'field value' from all {@code public} {@code int} fields
     * of a class.
     *
     * @param cls source class
     * @return unmodifiable name to value map
     */
    public static Map<String, Integer> nameToValueMapFromPublicIntFields(Class<?> cls) {
        final Map<String, Integer> map = Arrays.stream(cls.getDeclaredFields())
            .filter(fld -> Modifier.isPublic(fld.getModifiers()) && fld.getType() == Integer.TYPE)
            .collect(Collectors.toMap(Field::getName, fld -> getIntFromField(fld, fld.getName())));
        return Collections.unmodifiableMap(map);
    }
}
