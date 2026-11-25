package model;

import jakarta.servlet.http.HttpSession;

public class MySession {

    private HttpSession httpSession;

    public MySession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void put(String key,Object value) {
        this.httpSession.setAttribute(key, value);
    }

    public void remove(String key) {
        this.httpSession.removeAttribute(key);
    }

    public Object get(String key) {
        return this.httpSession.getAttribute(key);
    }
}
