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

    public static Object execute(Method method, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        // cree une instance de la classe qui va executer
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
            paramsValues.add(getParameterValue(parameter,req));
        }
        return paramsValues.toArray();
    }

    public static Object getParameterValue(Parameter parameter,HttpServletRequest req) {
        Type targetType = parameter.getParameterizedType();
        String inputValue = req.getParameter(parameter.getName());

        if (targetType == String.class) {
            return inputValue;
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(inputValue);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(inputValue);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(inputValue);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(inputValue);
        }
        throw new IllegalArgumentException("Type non supporté : " + targetType.getTypeName());
    }
}
