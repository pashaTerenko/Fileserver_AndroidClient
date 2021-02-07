package com.terenko.fileserver.DTO;


public class FileDTO implements DTO {

    String name;
    byte[] data;
    public FileDTO(){}
    private final String className= getClass().getName();

    @Override
    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
