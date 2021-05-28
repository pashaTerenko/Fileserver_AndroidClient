package com.terenko.fileserver.Layout.ui.File;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.terenko.fileserver.App;
import com.terenko.fileserver.DTO.CatalogDTO;
import com.terenko.fileserver.DTO.FileInfo;
import com.terenko.fileserver.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileViewModel extends ViewModel {
    App app;
    Activity activity;
    MutableLiveData<List<FileInfo>> data;
    MutableLiveData<CatalogDTO> catalog;
    MutableLiveData<List<FileInfo>> selectedFiles=new MutableLiveData<>();

    FileViewModel() {
        selectedFiles.postValue(new ArrayList<>());
        data = new MutableLiveData<>();
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = app.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public MutableLiveData<List<FileInfo>> getData() {
        return data;
    }

    public MutableLiveData<CatalogDTO> getCatalog() {
        return catalog;
    }

    public MutableLiveData<List<FileInfo>> getSelectedFiles() {
        return selectedFiles;
    }

    public void setSelectedFiles(MutableLiveData<List<FileInfo>> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public void setCatalog(MutableLiveData<CatalogDTO> catalog) {
        this.catalog = catalog;
    }
}