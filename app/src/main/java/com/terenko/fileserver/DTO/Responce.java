package com.terenko.fileserver.DTO;


import java.util.List;

public class Responce  {
    private int statusCode;
    private String message;
    DTO dto;
    List<DTO> dtoList;
public Responce(){

}
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public DTO getDto() {
        return dto;
    }

    public List<DTO> getDtoList() {
        return dtoList;
    }
}
