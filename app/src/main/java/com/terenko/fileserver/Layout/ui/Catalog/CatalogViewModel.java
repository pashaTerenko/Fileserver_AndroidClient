package com.terenko.fileserver.Layout.ui.Catalog;


import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.terenko.fileserver.API.ApiService;
import com.terenko.fileserver.DTO.CatalogDTO;

import java.util.ArrayList;
import java.util.List;


public class CatalogViewModel extends ViewModel {

    MutableLiveData<List<CatalogDTO>> data=new MutableLiveData<>();
    MutableLiveData<List<CatalogDTO>> selectedCatalogs=new MutableLiveData<>();

    CatalogViewModel() {
selectedCatalogs.postValue(new ArrayList<>());
    }

    public MutableLiveData<List<CatalogDTO>> getData() {
        return data;
    }

    public MutableLiveData<List<CatalogDTO>> getSelectedCatalogs() {
        return selectedCatalogs;
    }

}