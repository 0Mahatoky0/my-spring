import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.UrlMethod;
import util.FinderAnotation;

@WebListener
public class AppInitialiser implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // initialiser les routes de l aplications
        ServletContext context = sce.getServletContext();
        String pakageControleur = context.getInitParameter("controleurPackage");

        if (pakageControleur == null || pakageControleur.isBlank()) {
            throw new IllegalStateException("Parametre d'initialisation 'controleurPackage' manquant dans web.xml");
        }
        try {
            HashMap<UrlMethod, Method> urlMap = FinderAnotation.getControleurMaping(pakageControleur);
            context.setAttribute("urlMap", urlMap);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
