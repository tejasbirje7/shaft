package org.shaft.administration.apigateway.entity;

public class Routes {
    private String path;
    private String method;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Routes{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
