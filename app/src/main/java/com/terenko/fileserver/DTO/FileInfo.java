package com.terenko.fileserver.DTO;


public class FileInfo implements DTO{
    String uuid;
    String name;
    String exetension;
    String catalogUuid;
    private final String className= getClass().getName();
    public FileInfo(){

    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getExetension() {
        return exetension;
    }

    public String getCatalogUuid() {
        return catalogUuid;
    }

    @Override
    public String getClassName() {
        return className;
    }
}
