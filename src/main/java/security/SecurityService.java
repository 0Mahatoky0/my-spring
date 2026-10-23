package security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import security.anotation.NeedLogin;
import security.contrat.MySpringUser;
import util.FinderAnotation;

public class SecurityService {

    private final String isConnectedSessionName;

    public SecurityService(String isConnectedSessionName) {
        this.isConnectedSessionName = isConnectedSessionName;
    }

    public boolean isConnected(HttpServletRequest req) {
        return req.getSession().getAttribute(this.isConnectedSessionName) != null;
    }

    public Optional<MySpringUser> getUserConnected(HttpServletRequest req) {
        Object obj = req.getSession().getAttribute(this.isConnectedSessionName);
        if (obj != null && obj instanceof MySpringUser) {
            return Optional.of(((MySpringUser) obj));
        }
        return Optional.ofNullable(null);
    }

    public boolean accesAuthorised(HttpServletRequest req, Method method) {
        NeedLogin needLogin = method.getAnnotation(NeedLogin.class);
        Optional<MySpringUser> optionalUser = getUserConnected(req);
        if (needLogin != null
                 && (optionalUser.isEmpty() || !Arrays.asList(needLogin.value()).contains(optionalUser.get().getRole())))
            return false;
        return true;
    }
}