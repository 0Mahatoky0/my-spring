import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

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
        String pageResolvePrefix = context.getInitParameter("pageResolvePrefix");
        String pageResolveSufix = context.getInitParameter("pageResolveSufix");
        
        if (pakageControleur == null || pakageControleur.isBlank()) {
            throw new IllegalStateException("Parametre d'initialisation 'controleurPackage' manquant dans web.xml");
        }
        try {
            HashMap<UrlMethod, Method> urlMap = new HashMap<>();
            FinderAnotation.getControleurMaping(pakageControleur, urlMap);
            context.setAttribute("urlMap", urlMap);
            context.setAttribute("pageResolvePrefix", pageResolvePrefix);
            context.setAttribute("pageResolveSufix", pageResolveSufix);

            setUploadFileDestination(context);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void setUploadFileDestination(ServletContext context) throws Exception {
        try (InputStream input = AppInitialiser.class.getClassLoader().getResourceAsStream("app.properties")) {
            if(input == null) {
                System.err.println("[WARN] : Fichier de configuration 'app.properties' introuvable.");
            } else {
                Properties properties = new Properties();
                properties.load(input);
                context.setAttribute("fileUploadDir",properties.getProperty("fileUploadDir"));
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : Imposible de lire 'app.properties'.");
            System.err.println("[ERROR] : " + e.getMessage());
            throw e;
        }
    }
}
