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
        startProcessMapping(req, res, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        startProcessMapping(req, res, "POST");
    }

    private void startProcessMapping(HttpServletRequest req, HttpServletResponse res, String methodName) throws IOException {
        // resoudre le cors
        resolveCors(res);
        res.getWriter().println("[INF] : Path info : " + req.getServletPath());
        // verifier si l url taper corespond a une route
        if (urlMap.containsKey(new UrlMethod(req.getServletPath(), methodName))) {
            res.getWriter().println("[INF] : URL VALIDE (code 200) ");

            Method method = urlMap.get(new UrlMethod(req.getServletPath(), methodName));
            res.getWriter().println(req.getServletPath().concat("->").concat(toString(method)));
            try {
                res.getWriter().println("[INF] : Execution de la methode ...");
                Object resultExecution = MethodExecutor.execute(method);
                
                // si il retourne uniquement une string
                if (resultExecution instanceof String) {
                    // envoyer vers la vues corespondente
                    RequestDispatcher dispacher = req
                            .getRequestDispatcher(resolveNameView(resultExecution.toString()));
                    dispacher.forward(req, res);
                }

                if (resultExecution instanceof ModelAndView) {
                    // envoyer vers la vue corespondente
                    ModelAndView modelAndView = (ModelAndView) resultExecution;
                    RequestDispatcher dispacher = req
                            .getRequestDispatcher(resolveNameView(modelAndView.getView()));
                    // envoyer les models vers la page
                    for (Map.Entry<String, Object> model : modelAndView.getAttributes().entrySet()) {
                        req.setAttribute(model.getKey(), model.getValue());
                    }
                    dispacher.forward(req, res);
                }
                res.getWriter().println("[INF] : La methode a ete executer avec succes !");
            } catch (Exception e) {
                res.getWriter().println("[ERROR] : Une erreur s est produit lors de l execution de la methode : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            res.getWriter().println("[ERROR] : URL INTROUVABLE (code 404)");
            showAllUrlMapping(res);
        }
        res.getWriter().flush();
    }

    private void showAllUrlMapping(HttpServletResponse res) throws IOException {
        res.getWriter().println("[INF] : LISTE DES URL PRESENT");
        for (Map.Entry<UrlMethod, Method> mapping : this.urlMap.entrySet()) {
            res.getWriter().println(mapping.getKey() + " -> " + toString(mapping.getValue()));
        }
        res.getWriter().println("[INF] : FIN LISTE ");
    }

    private String resolveNameView(String viewName) {
        return pageResolvePrefix + viewName + pageResolveSufix;
    }

    private void resolveCors(HttpServletResponse res) {
        res.setContentType("text/plain");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private String toString(Method method) {
        return method.getDeclaringClass().getName().concat("::")
                    .concat(method.getName());
    }
}