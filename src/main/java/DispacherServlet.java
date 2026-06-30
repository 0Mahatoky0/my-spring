import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UrlMethod;
import util.FinderAnotation;
import util.MethodExecutor;

public class DispacherServlet extends HttpServlet {

    private List<String> classeControleurs = new ArrayList<>();
    private HashMap<UrlMethod, Method> urlMap;

    @Override
    public void init() throws ServletException {
        this.urlMap = new HashMap<>();

        ServletContext context = getServletContext();
        String pakageControleur = context.getInitParameter("controleurPackage");

        try {
            if (pakageControleur == null || pakageControleur.isBlank()) {
                throw new ServletException("Parametre d'initialisation 'controleurPackage' manquant dans web.xml");
            }
            this.classeControleurs = FinderAnotation.findControleurName(pakageControleur);
            this.urlMap = FinderAnotation.getControleurMaping(pakageControleur);
        } catch (Exception e) {
            throw new ServletException("Impossible d'initialiser la liste des controleurs", e);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = res.getWriter();

        if (classeControleurs == null || classeControleurs.isEmpty()) {
            out.println("Aucun controleur configure ou trouve.");
            out.flush();
            return;
        }
        out.println("Path info : " + req.getPathInfo());
        startProcessMapping(req.getPathInfo(), "GET", out);

        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = res.getWriter();

        if (classeControleurs == null || classeControleurs.isEmpty()) {
            out.println("Aucun controleur configure ou trouve.");
            out.flush();
            return;
        }
        out.println("Path info : " + req.getPathInfo());
        startProcessMapping(req.getPathInfo(), "POST", out);

        out.flush();
    }

    private void startProcessMapping(String sourceUrl, String methodName, PrintWriter out) {
        // verifier si l url taper corespond a une route
        if (urlMap.containsKey(new UrlMethod(sourceUrl, methodName))) {
            out.println("--URL VALIDE (200)--");
            Method method = urlMap.get(new UrlMethod(sourceUrl, methodName));
            out.println(sourceUrl.concat("->").concat(method.getDeclaringClass().getName()).concat("::")
                    .concat(method.getName()));
            try {
                out.println("INF : Execution de la methode ...");
                MethodExecutor.execute(method);
                out.println("INF : La methode a ete executer avec succes !");
            } catch (Exception e) {
                out.println("ERREUR : Une erreur s est produit lors de l execution de la methode : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            out.println("-URL INTROUVABLE (404)-");
            this.urlMap.forEach((cle, valeur) -> {
                out.println(cle + " -> " + valeur.getDeclaringClass().getName() + "::" +
                        valeur.getName());
            });
        }
    }
}