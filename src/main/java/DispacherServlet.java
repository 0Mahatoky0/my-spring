import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UrlMethod;
import util.MethodExecutor;

public class DispacherServlet extends HttpServlet {

    private HashMap<UrlMethod, Method> urlMap;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.urlMap = (HashMap<UrlMethod, Method>)context.getAttribute("urlMap");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = res.getWriter();

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