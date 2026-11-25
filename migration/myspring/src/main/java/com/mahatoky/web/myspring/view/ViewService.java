package view;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelAndView;

public class ViewService {

    private String prefix;
    private String sufix;


    public ViewService(String prefix, String sufix) {
        this.prefix = prefix;
        this.sufix = sufix;
    }

    public void processModelAndView(HttpServletRequest req, HttpServletResponse res, ModelAndView modelAndView)
            throws ServletException, IOException {
        // envoyer les models vers la page
        for (Map.Entry<String, Object> model : modelAndView.getAttributes().entrySet()) {
            req.setAttribute(model.getKey(), model.getValue());
        }
        // envoyer vers la vue corespondente
        req.getRequestDispatcher(resolveNameView(modelAndView.getView())).forward(req, res);
    }

    public void forwardToView(HttpServletRequest req, HttpServletResponse res,String viewName) throws ServletException, IOException {
        req.getRequestDispatcher(resolveNameView(viewName)).forward(req, res);
    }

    private String resolveNameView(String viewName) {
        return prefix + viewName + sufix;
    }
}
