import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.FinderAnotation;

public class DispacherServlet extends HttpServlet {

    private List<String> classeControleurs = new ArrayList<>();
    private HashMap<String, String> urlMap;

    @Override
    public void init() throws ServletException {
        this.urlMap = new HashMap<>();

        ServletContext context = getServletContext();
        String pakageControleur = context.getInitParameter("controleurPackage");

        try {
            if (pakageControleur == null || pakageControleur.isBlank()) {
                throw new ServletException("Parametre d'initialisation 'controleurPackage' manquant dans web.xml");
            }
            this.classeControleurs = FinderAnotation.findControleurName(pakageControleur, getClass());
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

        this.urlMap.forEach((cle, valeur) -> {
            out.println(cle + " -> " + valeur);
        });
        out.flush();
    }
}