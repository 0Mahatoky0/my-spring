package model;

import java.util.Objects;

import anotation.GetMapping;
import anotation.PostMapping;
import anotation.UrlMapping;

public class UrlMethod {

    private String url;
    private String method;

    public UrlMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public UrlMethod(UrlMapping anotationMapping) {
        this.url = anotationMapping.value();
        this.method = anotationMapping.method();
    }

    public UrlMethod(GetMapping getAnotationMapping) {
        this.url = getAnotationMapping.value();
        this.method = "GET";
    }

    public UrlMethod(PostMapping getAnotationMapping) {
        this.url = getAnotationMapping.value();
        this.method = "POST";
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UrlMethod)) {
            return false;
        }
        UrlMethod otherObject = (UrlMethod) obj;
        return this.url.equals(otherObject.getUrl()) && this.method.equals(otherObject.getMethod());
    }

    @Override
    public String toString() {
        return "[" + this.url + "," + this.method + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.url,this.method);
    }
}
