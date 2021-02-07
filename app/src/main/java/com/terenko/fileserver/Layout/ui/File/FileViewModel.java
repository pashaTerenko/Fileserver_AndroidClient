package com.terenko.fileserver.Layout.ui.File;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.terenko.fileserver.App;
import com.terenko.fileserver.DTO.CatalogDTO;
import com.terenko.fileserver.DTO.FileInfo;

import java.util.List;


public class FileViewModel extends ViewModel {
    App app;
    MutableLiveData<List<FileInfo>> data;
    MutableLiveData<CatalogDTO> catalog;

    FileViewModel() {

        data = new MutableLiveData<>();

    }

    public MutableLiveData<List<FileInfo>> getData() {
        return data;
    }

    public MutableLiveData<CatalogDTO> getCatalog() {
        return catalog;
    }

    public void setCatalog(MutableLiveData<CatalogDTO> catalog) {
        this.catalog = catalog;
    }
}