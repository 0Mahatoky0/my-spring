package security;
import jakarta.servlet.http.HttpServletRequest;
public class SecurityService {

    public boolean isConnected(HttpServletRequest req) {
        return req.getSession().getAttribute("isConnected") != null;
    }
}