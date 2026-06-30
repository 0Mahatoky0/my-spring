package util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import anotation.Controleur;
import anotation.GetMapping;
import anotation.PostMapping;
import anotation.UrlMapping;
import model.UrlMethod;

public class FinderAnotation {

    public static HashMap<UrlMethod,Method> getControleurMaping(String packageName) throws Exception{
        // recuperer les classe dans le package
        List<Class<?>> listClasses = findAllControleur(packageName);
        HashMap<UrlMethod,Method> allMaping = new HashMap<>();

        // recuperer la lsite des fonction de chaque controleur
        for (Class<?> controleur : listClasses) {
            System.out.println("Verification de la classe " + controleur.getName());
            for (Method m : controleur.getDeclaredMethods()) {
                
                UrlMethod urlMethod = null;

                System.out.println("Verification du method : " + m.getName());
                //recuperer les anotations urlMapping
                if (m.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping urlMap = m.getAnnotation(UrlMapping.class);
                    urlMethod = new UrlMethod(urlMap);
                }

                //recuperer les anotations getMapping
                if(m.isAnnotationPresent(GetMapping.class)) {
                    System.out.println("Anotation get present ");
                    GetMapping urlMap = m.getAnnotation(GetMapping.class);
                    urlMethod = new UrlMethod(urlMap);
                }

                //recuperer les anotations postMapping
                if(m.isAnnotationPresent(PostMapping.class)) {
                    PostMapping urlMap = m.getAnnotation(PostMapping.class);
                    urlMethod = new UrlMethod(urlMap);
                }

                if(urlMethod != null) {
                    if(allMaping.containsKey(urlMethod)) {
                        Method firstDefin = allMaping.get(urlMethod);
                        throw new IllegalArgumentException("L' url : " + urlMethod.getUrl() + " est redefini .\nDefinition 1 : " + firstDefin.getDeclaringClass().getName() + "::" + firstDefin.getName() + "\nDefinition 2 : " + m.getDeclaringClass().getName() + "::" + m.getName());
                    }
                    allMaping.put(urlMethod,m);
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

    public static List<String> findControleurName(String packageName) throws Exception {
        // recuperer les classe dans le package
        List<Class<?>> listClasses = findAllControleur(packageName);
        List<String> listeClasseAnnoter = new ArrayList<>();
        // verifier si la classe possede l anotation
        for (Class<?> class1 : listClasses) {
            listeClasseAnnoter.add(class1.getSimpleName());
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

                    // verifier si il sagit d une directory
                    if (file.isDirectory()) {
                        List<Class<?>> sousClass = FinderAnotation
                                .getClassesInPackage(packageName.concat(".").concat(file.getName()));
                        classes.addAll(sousClass);
                    }
                }
            }
        }
        return classes;
    }

    public static void main(String[] args) throws Exception {
        HashMap<UrlMethod,Method> urlMap = FinderAnotation.getControleurMaping("controleur");
        urlMap.forEach((url,methode) -> {
            System.out.println(url  + "->" + methode.getName());
        });

        System.out.println("bonjour");
    }
}
