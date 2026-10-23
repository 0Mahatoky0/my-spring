import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelAndView;
import model.UrlMethod;
import security.SecurityService;
import util.FinderAnotation;
import util.MethodExecutor;
import util.SpringJsonParser;
import view.ViewService;

@MultipartConfig(fileSizeThreshold = 2 * 1024 * 1024, maxFileSize = 10 * 1024 * 1024, maxRequestSize = 100 * 1024
        * 1024)
public class DispacherServlet extends HttpServlet {

    private HashMap<UrlMethod, Method> urlMap;
    private SpringJsonParser jsonParser;
    private MethodExecutor methodExecutor;
    private SecurityService securityService;
    private ViewService viewService;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.urlMap = (HashMap<UrlMethod, Method>) context.getAttribute("urlMap");
        this.viewService = new ViewService(
                context.getAttribute("pageResolvePrefix").toString(),
                context.getAttribute("pageResolveSufix").toString());
                Object objIsConnectedSessionName = context.getInitParameter("isConnectedSessionName");
        this.securityService = new SecurityService(objIsConnectedSessionName != null ? objIsConnectedSessionName.toString() : "isConnected");
        // TODO : a initialiser dans App initialiser
        this.jsonParser = new SpringJsonParser(new ObjectMapper());
        this.methodExecutor = new MethodExecutor(context);
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
                
                if (FinderAnotation.haveNeedLoginAnotation(method)) {
                    if (!this.securityService.isConnected(req)) {
                        ModelAndView modelAndView = new ModelAndView("login");
                        modelAndView.addAttributes("error","Vous devez vous connecter");
                        this.viewService.processModelAndView(req, res,modelAndView);
                        return;
                    }
                }

                if (method.getParameterCount() != 0) {
                    resultExecution = methodExecutor.execute(method, req);
                } else {
                    resultExecution = methodExecutor.execute(method);
                }

                // verifier si la methode est annote api
                if (FinderAnotation.haveApiAnotation(method)) {
                    processApi(req, res, resultExecution);
                    return;
                }

                // si il retourne uniquement une string
                if (resultExecution instanceof String) {
                    // envoyer vers la vues corespondente
                    this.viewService.forwardToView(req, res, resultExecution.toString());
                }
                if (resultExecution instanceof ModelAndView) {
                    ModelAndView modelAndView = (ModelAndView) resultExecution;
                    this.viewService.processModelAndView(req, res, modelAndView);
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

    private void processApi(HttpServletRequest req, HttpServletResponse res, Object jsonResponse)
            throws JsonProcessingException, IOException {
        res.setContentType("application/json");
        res.getWriter().write(this.jsonParser.toJson(jsonResponse));
        res.getWriter().flush();
    }

    private void showAllUrlMapping(HttpServletResponse res) throws IOException {
        res.getWriter().println("[INF] : LISTE DES URL PRESENT");
        for (Map.Entry<UrlMethod, Method> mapping : this.urlMap.entrySet()) {
            res.getWriter().println(mapping.getKey() + " -> " + toString(mapping.getValue()));
        }
        res.getWriter().println("[INF] : FIN LISTE ");
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