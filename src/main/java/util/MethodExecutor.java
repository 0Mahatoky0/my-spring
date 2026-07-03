package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodExecutor {
    
    public static Object execute(Method method) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        //cree une instance de la classe qui va executer
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        return method.invoke(instance);
    }
}
