import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelAndView;
import model.UrlMethod;
import util.FinderAnotation;
import util.MethodExecutor;
import util.SpringJsonParser;

public class DispacherServlet extends HttpServlet {

    private HashMap<UrlMethod, Method> urlMap;
    private String pageResolvePrefix;
    private String pageResolveSufix;
    private SpringJsonParser jsonParser;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.urlMap = (HashMap<UrlMethod, Method>) context.getAttribute("urlMap");
        pageResolvePrefix = context.getAttribute("pageResolvePrefix").toString();
        pageResolveSufix = context.getAttribute("pageResolveSufix").toString();
        
        // TODO : a initialiser dans App initialiser
        this.jsonParser = new SpringJsonParser(new ObjectMapper());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        startProcessMapping(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        startProcessMapping(req, res);
    }

    private void startProcessMapping(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // resoudre le cors
        resolveCors(res);
        // verifier si l url taper corespond a une route
        if (urlMap.containsKey(new UrlMethod(req.getServletPath(), req.getMethod()))) {
        Method method = urlMap.get(new UrlMethod(req.getServletPath(), req.getMethod()));
            try {
                Object resultExecution = null;
                if(method.getParameterCount() != 0) {
                    resultExecution = MethodExecutor.execute(method,req);
                } else {
                    resultExecution = MethodExecutor.execute(method);
                }
                 // verifier si la methode est annote api
                if (FinderAnotation.haveApiAnotation(method)) {
                    processApi(req, res,resultExecution);
                    return;
                }
                // si il retourne uniquement une string
                if (resultExecution instanceof String) {
                    // envoyer vers la vues corespondente
                    req.getRequestDispatcher(resolveNameView(resultExecution.toString())).forward(req, res);
                }
                if (resultExecution instanceof ModelAndView) {
                    ModelAndView modelAndView = (ModelAndView) resultExecution;
                    processModelAndView(req, res, modelAndView);
                }
            } catch (Exception e) {
                res.getWriter().println(
                        "[ERROR] : Une erreur s est produit lors de l execution de la methode : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            res.getWriter().println("[ERROR] : URL INTROUVABLE (code 404)");
            showAllUrlMapping(res);
        }
        res.getWriter().flush();
    }

    private void processApi(HttpServletRequest req, HttpServletResponse res, Object jsonResponse) throws JsonProcessingException, IOException {
        res.setContentType("application/json");
        res.getWriter().write(this.jsonParser.toJson(jsonResponse));
        res.getWriter().flush();
    }

    private void processModelAndView(HttpServletRequest req, HttpServletResponse res, ModelAndView modelAndView)
            throws ServletException, IOException {
        // envoyer les models vers la page
        for (Map.Entry<String, Object> model : modelAndView.getAttributes().entrySet()) {
            req.setAttribute(model.getKey(), model.getValue());
        }
        // envoyer vers la vue corespondente
        req.getRequestDispatcher(resolveNameView(modelAndView.getView())).forward(req, res);
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
        // res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private String toString(Method method) {
        return method.getDeclaringClass().getName().concat("::")
                .concat(method.getName());
    }
}