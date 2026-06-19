package util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import anotation.Controleur;
import anotation.UrlMapping;

public class FinderAnotation {

    public static HashMap<String, String> getControleurMaping(String packageName) throws Exception {
        // recuperer les classe dans le package
        List<Class<?>> listClasses = findAllControleur(packageName);
        HashMap<String, String> allMaping = new HashMap<>();

        // recuperer la lsite des fonction du controleur
        for (Class<?> controleur : listClasses) {
            for (Method m : controleur.getDeclaredMethods()) {
                if (m.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping urlMap = m.getAnnotation(UrlMapping.class);
                    allMaping.put(urlMap.value(),controleur.getSimpleName() + "::" + m.getName());            
                }
            }
        }
        return allMaping;
    }

    public static List<Class<?>> findAllControleur(String packageName) throws Exception {
        // recuperer les classe dans le package
        List<Class<?>> listClasses = getClassesInPackage(packageName);
        List<Class<?>> lsitControleurs = new ArrayList<>();
        // verifier si la classe possede l anotation
        for (Class<?> class1 : listClasses) {
            if (class1.isAnnotationPresent(Controleur.class)) {
                lsitControleurs.add(class1);
            }
        }
        return lsitControleurs;
    }

    public static List<String> findControleurName(String packageName, Class<?> clazz) throws Exception {
        // recuperer les classe dans le package
        List<Class<?>> listClasses = getClassesInPackage(packageName);
        List<String> listeClasseAnnoter = new ArrayList<>();
        // verifier si la classe possede l anotation
        for (Class<?> class1 : listClasses) {
            if (class1.isAnnotationPresent(Controleur.class)) {
                listeClasseAnnoter.add(class1.getSimpleName());
            }
        }
        return listeClasseAnnoter;
    }

    // C'est cette fonction-là qu'il faut copier-coller dans ta classe
    public static List<Class<?>> getClassesInPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Package introuvable : " + packageName);
        }

        File directory = new File(resource.getFile());
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        classes.add(Class.forName(className));
                    }
                }
            }
        }
        return classes;
    }
}
