package devops25.releaserangers.coursemgmt_service.util;

import java.lang.reflect.Field;

public class PatchUtils {
    public static <T> void applyPatch(T source, T target) throws IllegalAccessException {
        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final Object value = field.get(source);
            if (value != null) {
                field.set(target, value);
            }
        }
    }
}
