package util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

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
        System.out.println("FIX: Q -> " + Arrays.toString(getArgsValues(method, req)));
        return method.invoke(instance, getArgsValues(method, req));
    }

    private static Object[] getArgsValues(Method method, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        ArrayList<Object> paramsValues = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            System.out.println("NEED: ->" + parameter.getName());
            paramsValues.add(getParameterValue(parameter, req));
        }
        return paramsValues.toArray();
    }

    private static Object getParameterValue(Parameter parameter, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Type targetType = parameter.getParameterizedType();
        System.out.println("FIX: " + targetType.getTypeName());
        return createInstance(targetType, null, parameter.getName(), req);
    }

    private static Object getObjectValue(Type type, String base, String instanceName, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        System.out.println("FIX: ->" + type.getTypeName());
        if (type instanceof Class<?>) {
            Object instance = ((Class<?>)type).getDeclaredConstructor().newInstance();

            for (Field field : instance.getClass().getDeclaredFields()) {
                System.out.println("FIX: " + field.getName());
                String baseInstanceName = "";
                if(base != null) {
                    baseInstanceName = baseInstanceName.concat(".").concat(instanceName);
                } else {
                    baseInstanceName = instanceName;
                }
                ReflectionUtils.getSetter(field).invoke(instance,
                        createInstance(field.getType(), baseInstanceName, field.getName(), req));
            }
            return instance;
        }
        throw new RuntimeException("Erreur type doit etre une instance de class or il est une instance de : " + type.getTypeName());
    }

    private static Object createInstance(Type type, String base, String instanceName, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        if (type == String.class) {
            return getParametterValue(base, instanceName, req);
        } else if (type == Double.class || type == double.class) {
            return Double.valueOf(getParametterValue(base, instanceName, req));
        } else if (type == Integer.class || type == int.class) {
            return Integer.valueOf(getParametterValue(base, instanceName, req));
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(getParametterValue(base, instanceName, req));
        } else if (type == Long.class || type == long.class) {
            return Long.valueOf(getParametterValue(base, instanceName, req));
        } else if (type.getClass() instanceof Object) {

            return getObjectValue(type, base, instanceName, req);
        }
        throw new IllegalArgumentException("Type non supporté : " + type.getTypeName());
    }

    private static String getParametterValue(String base, String instanceName, HttpServletRequest req) {
        System.out.println("FIX: " + instanceName);
        if (base == null) {
            return req.getParameter(instanceName);
        } else {
            return req.getParameter(base.concat(".").concat(instanceName));
        }
    }
}
