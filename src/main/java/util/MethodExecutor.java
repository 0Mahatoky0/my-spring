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
import java.util.HashMap;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import model.MySession;
import model.UploadFile;

public class MethodExecutor {

    private ServletContext servletContext;

    public MethodExecutor(ServletContext context) {
        this.servletContext = context;
    }

    public Object execute(Method method) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // cree une instance de la classe qui va executer
        Object instance = getInstanceController(method).orElseThrow( () -> new RuntimeException(
                    "Impossible de trouver une instance pour la classe : " + method.getDeclaringClass().getName()));
        System.out.println("FIX : nom d instance prise : " + instance.getClass().getName() + " pour la methode : "
                + method.getName());
        return method.invoke(instance);
    }

    public Object execute(Method method, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException, NameNotFoundException {
        // cree une instance de la classe qui va executer
        Object instance = getInstanceController(method).orElseThrow(() -> new RuntimeException(
                "Impossible de trouver une instance pour la classe : " + method.getDeclaringClass().getName()));
        System.out.println("FIX: Q -> " + Arrays.toString(getArgsValues(method, req)));
        return method.invoke(instance, getArgsValues(method, req));
    }

    private Optional<Object> getInstanceController(Method method) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Object obj = this.servletContext.getAttribute("controllers");
        if (obj instanceof HashMap<?, ?>) {
            return Optional.of(((HashMap<Class<?>, Object>) obj).get(method.getDeclaringClass()));
        }
        return Optional.empty();
    }

    private Object[] getArgsValues(Method method, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException, NameNotFoundException {
        ArrayList<Object> paramsValues = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            System.out.println("NEED: ->" + parameter.getName());
            paramsValues.add(getParameterValue(parameter, req));
        }
        return paramsValues.toArray();
    }

    private Object getParameterValue(Parameter parameter, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException, NameNotFoundException {
        Type targetType = parameter.getParameterizedType();
        return createInstance(targetType, null, parameter.getName(), req);
    }

    private Object getObjectValue(Type type, String base, HttpServletRequest req)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, IOException, ServletException, NameNotFoundException {
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
            NoSuchMethodException, SecurityException, IOException, ServletException, NameNotFoundException {
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
            return this.createInstanceUploadFile(instanceName, req);
        } else if (type == MySession.class) {
            return this.getSession(req);
        } else if (type.getClass() instanceof Object) {
            String nextObjectBase = "";
            if (base != null) {
                nextObjectBase = nextObjectBase.concat(base).concat(".");
            }
            nextObjectBase = nextObjectBase.concat(instanceName);
            return getObjectValue(type, nextObjectBase, req);
        }
        throw new IllegalArgumentException("Type non supporté : " + type.getTypeName());
    }

    private Object getSession(HttpServletRequest req) {
        MySession mySession = (MySession) req.getSession().getAttribute("session");
        if (mySession == null) {
            req.getSession().setAttribute("session", new MySession(req.getSession()));
        }
        return mySession;
    }

    private UploadFile createInstanceUploadFile(String filename, HttpServletRequest req)
            throws IOException, ServletException, NameNotFoundException {
        return new UploadFile(req.getPart(filename), this.servletContext);
    }

    private String getParametterValue(String base, String instanceName, HttpServletRequest req) {
        System.out.println("FIX: Creer instance : " + base + "." + instanceName);

        if (base == null) {
            return req.getParameter(instanceName);
        } else {
            return req.getParameter(base.concat(".").concat(instanceName));
        }
    }
}
