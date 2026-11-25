package util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    /**
     * Récupère la méthode Getter d'un champ ou lève une exception si non trouvée.
     */
    public static Method getGetter(Field field) throws NoSuchMethodException {
        if (field == null) {
            throw new IllegalArgumentException("Le champ fourni ne peut pas être null.");
        }

        Class<?> clazz = field.getDeclaringClass();
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        String capitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String getterName = fieldType.equals(boolean.class)
                ? (fieldName.startsWith("is") ? fieldName : "is" + capitalized)
                : "get" + capitalized;

        Method method = clazz.getMethod(getterName);
        if (!method.getReturnType().equals(fieldType)) {
            throw new NoSuchMethodException("Le type de retour du getter (" 
                    + method.getReturnType().getName() 
                    + ") ne correspond pas au type du champ (" 
                    + fieldType.getName() + ").");
        }
        return method;
    }

    /**
     * Récupère la méthode Setter d'un champ ou lève une exception si non trouvée.
     */
    public static Method getSetter(Field field) throws NoSuchMethodException {
        if (field == null) {
            throw new IllegalArgumentException("Le champ fourni ne peut pas être null.");
        }

        Class<?> clazz = field.getDeclaringClass();
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        String capitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String setterName = "set" + capitalized;

        return clazz.getMethod(setterName, fieldType);
    }
}