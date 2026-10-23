package security;
import jakarta.servlet.http.HttpServletRequest;
public class SecurityService {

    private final String isConnectedSessionName;
    

    public SecurityService(String isConnectedSessionName) {
        this.isConnectedSessionName = isConnectedSessionName;
    }

    public boolean isConnected(HttpServletRequest req) {
        return req.getSession().getAttribute(this.isConnectedSessionName) != null;
    }
}