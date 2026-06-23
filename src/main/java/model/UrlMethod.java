package model;

public class UrlMethod {

    private String url;
    private String method;

    public UrlMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UrlMethod)) {
            return false;
        }
        UrlMethod otherObject = (UrlMethod) obj;

        return this.url.equals(otherObject.getUrl()) && this.method.equals(otherObject.getMethod());
    }
}
