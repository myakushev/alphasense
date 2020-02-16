package com.alphasense.backend.tests.cucumber.utils;

public class ContextParam {

    private String name;
    private String path;
    private boolean isIndexed;
    private boolean isUnique = false;

    public ContextParam(String name, String path, boolean isIndexed) {
        this.name = name;
        this.path = path;
        this.isIndexed = isIndexed;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isIndexed() {
        return isIndexed;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public ContextParam setUnique(boolean unique) {
        isUnique = unique;
        return this;
    }
}
