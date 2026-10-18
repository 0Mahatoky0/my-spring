package util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import model.UploadFile;

public class MethodExecutor {

    private ServletContext servletContext;

    public MethodExecutor(ServletContext context) {
        this.servletContext = context;
    }

    public Object execute(Method method) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // cree une instance de la classe qui va executer
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        return method.invoke(instance);
    }

    public  Object execute(Method method, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException {
        // cree une instance de la classe qui va executer
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        System.out.println("FIX: Q -> " + Arrays.toString(getArgsValues(method, req)));
        return method.invoke(instance, getArgsValues(method, req));
    }

    private  Object[] getArgsValues(Method method, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException {
        ArrayList<Object> paramsValues = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            System.out.println("NEED: ->" + parameter.getName());
            paramsValues.add(getParameterValue(parameter, req));
        }
        return paramsValues.toArray();
    }

    private Object getParameterValue(Parameter parameter, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException {
        Type targetType = parameter.getParameterizedType();
        return createInstance(targetType, null, parameter.getName(), req);
    }

    private Object getObjectValue(Type type, String base, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException {
        if (type instanceof Class<?>) {
            Object instance = ((Class<?>) type).getDeclaredConstructor().newInstance();

            for (Field field : instance.getClass().getDeclaredFields()) {
                ReflectionUtils.getSetter(field).invoke(instance,
                        createInstance(field.getType(), base, field.getName(), req));
            }
            return instance;
        }
        throw new RuntimeException(
                "Erreur type doit etre une instance de class or il est une instance de : " + type.getTypeName());
    }

    private Object createInstance(Type type, String base, String instanceName, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException {
        String parameterValueString = getParametterValue(base, instanceName, req);
        if (type == String.class) {
            return parameterValueString;
        } else if (type == Double.class || type == double.class) {
            return Double.valueOf(parameterValueString);
        } else if (type == Integer.class || type == int.class) {
            return Integer.valueOf(parameterValueString);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(parameterValueString);
        } else if (type == Long.class || type == long.class) {
            return Long.valueOf(parameterValueString);
        } else if (type == Date.class) {
            return Date.valueOf(parameterValueString);
        } else if (type == UploadFile.class) {
            return this.createInstanceUploadFile(instanceName,req);
        }
         else if (type.getClass() instanceof Object) {
            String nextObjectBase = "";
            if (base != null) {
                nextObjectBase = nextObjectBase.concat(base).concat(".");
            }
            nextObjectBase = nextObjectBase.concat(instanceName);
            return getObjectValue(type, nextObjectBase, req);
        }
        throw new IllegalArgumentException("Type non supporté : " + type.getTypeName());
    }

    private UploadFile createInstanceUploadFile(String filename,HttpServletRequest req) throws IOException, ServletException {
        return new UploadFile(req.getPart(filename),this.servletContext);
    }

    private  String getParametterValue(String base, String instanceName, HttpServletRequest req) {
        System.out.println("FIX: Creer instance : " + base + "." + instanceName);

        if (base == null) {
            return req.getParameter(instanceName);
        } else {
            return req.getParameter(base.concat(".").concat(instanceName));
        }
    }
}
