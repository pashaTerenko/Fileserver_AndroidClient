package com.terenko.fileserver.Layout.ui.Catalog;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.terenko.fileserver.Layout.ui.login.RegisterViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class CatalogViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CatalogViewModel.class)) {
            return (T) new CatalogViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}