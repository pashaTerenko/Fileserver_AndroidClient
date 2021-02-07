package com.terenko.fileserver.DTO;



import java.nio.file.AccessMode;
import java.util.List;
import java.util.Set;


public class CatalogDTO implements DTO {
    private String uuid;
    private String name;
    private AccessMode accessMode;
    private AccountDTO creator;
    private List<FileInfo> fileList;
    Set<String> haveAccess;
    public CatalogDTO(String uuid, String name, AccessMode accessMode, AccountDTO creator, List<FileInfo> fileList,Set<String> haveAccess) {
        this.uuid = uuid;
        this.name = name;
        this.accessMode = accessMode;
        this.creator = creator;
        this.fileList = fileList;
        this.haveAccess=haveAccess;
    }

    @Override
    public String getClassName() {
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public AccountDTO getCreator() {
        return creator;
    }

    public List<FileInfo> getFileList() {
        return fileList;
    }

    public Set<String> getHaveAccess() {
        return haveAccess;
    }
}
