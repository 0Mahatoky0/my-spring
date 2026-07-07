import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelAndView;
import model.UrlMethod;
import util.MethodExecutor;

public class DispacherServlet extends HttpServlet {

    private HashMap<UrlMethod, Method> urlMap;
    private String pageResolvePrefix;
    private String pageResolveSufix;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.urlMap = (HashMap<UrlMethod, Method>) context.getAttribute("urlMap");
        pageResolvePrefix = context.getAttribute("pageResolvePrefix").toString();
        pageResolveSufix = context.getAttribute("pageResolveSufix").toString();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = res.getWriter();

        out.println("Path info : " + req.getServletPath());
        startProcessMapping(req, res, "GET", out);

        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = res.getWriter();

        out.println("Path info : " + req.getServletPath());
        startProcessMapping(req, res, "POST", out);

        out.flush();
    }

    private void startProcessMapping(HttpServletRequest req, HttpServletResponse res, String methodName,
            PrintWriter out) {
        // verifier si ca cores
        // verifier si l url taper corespond a une route
        if (urlMap.containsKey(new UrlMethod(req.getServletPath(), methodName))) {
            out.println("--URL VALIDE (200)--");
            Method method = urlMap.get(new UrlMethod(req.getServletPath(), methodName));
            out.println(req.getServletPath().concat("->").concat(method.getDeclaringClass().getName()).concat("::")
                    .concat(method.getName()));
            try {
                out.println("INF : Execution de la methode ...");

                Object resultExecution = MethodExecutor.execute(method);

                // si il retourne uniquement une string
                if (resultExecution instanceof String) {
                    // envoyer vers la vues corespondente
                    RequestDispatcher dispacher = req
                            .getRequestDispatcher("/WEB-INF/views/" + resultExecution.toString());
                    dispacher.forward(req, res);
                }

                // si il retourn une model view
                if (resultExecution instanceof ModelAndView) {
                    // envoyer vers la vues corespondente
                    ModelAndView modelAndView = (ModelAndView) resultExecution;
                    RequestDispatcher dispacher = req
                            .getRequestDispatcher(pageResolvePrefix + modelAndView.getView() + pageResolveSufix);

                    // envoyer les models vers la page
                    for (Map.Entry<String, Object> model : modelAndView.getAttributes().entrySet()) {
                        req.setAttribute(model.getKey(),model.getValue());
                        System.out.println("OULALA : " + model.getKey());
                    }

                    dispacher.forward(req, res);
                }

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