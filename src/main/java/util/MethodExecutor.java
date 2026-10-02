package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

public class MethodExecutor {

    public static Object execute(Method method) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // cree une instance de la classe qui va executer
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        return method.invoke(instance);
    }

    public static Object execute(Method method,HttpServletRequest req) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        //cree une instance de la classe qui va executer
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        return method.invoke(instance, getArgsValues(method, req));
    }

    private static Object[] getArgsValues(Method method, HttpServletRequest req) {
        ArrayList<Object> paramsValues = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            String valueString = req.getParameter(parameter.getName());
            System.out.println("NEED: " + parameter.getName());
            if (valueString == null) {
                paramsValues.add(null);
            }
            paramsValues.add(parseString(valueString, parameter.getParameterizedType()));
        }
        return paramsValues.toArray();
    }

    public static Object parseString(String input, Type targetType) {
        if (targetType == String.class) {
            return input;
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(input);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(input);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(input);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(input);
        }
        throw new IllegalArgumentException("Type non supporté : " + targetType.getTypeName());
    }
}
