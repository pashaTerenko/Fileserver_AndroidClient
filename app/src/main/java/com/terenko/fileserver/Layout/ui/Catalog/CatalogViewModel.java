package com.terenko.fileserver.Layout.ui.Catalog;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.terenko.fileserver.API.ApiService;
import com.terenko.fileserver.DTO.CatalogDTO;

import java.util.List;


public class CatalogViewModel extends ViewModel {

    MutableLiveData<List<CatalogDTO>> data;


    CatalogViewModel() {
    data=new MutableLiveData<>();
    }

    public MutableLiveData<List<CatalogDTO>> getData() {
        return data;
    }
}