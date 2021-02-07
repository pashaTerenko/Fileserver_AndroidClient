package com.terenko.fileserver.Layout.ui.File;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.terenko.fileserver.App;
import com.terenko.fileserver.DTO.CatalogDTO;
import com.terenko.fileserver.DTO.FileInfo;
import com.terenko.fileserver.DTO.Responce;
import com.terenko.fileserver.Layout.ui.Catalog.MainActivity;
import com.terenko.fileserver.Layout.ui.login.LoginActivity;
import com.terenko.fileserver.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FileActivity extends AppCompatActivity {
    private String[] mFileList;
    private File mPath = new File(Environment.getExternalStorageDirectory() + "//yourdir//");


    private static final int PICKFILE_RESULT_CODE = 111;


    App app;
    private FileViewModel fileViewModel;
    RecyclerView recyclerView;
    Adapter adapter;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, FileActivity.class);
        caller.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        recyclerView = findViewById(R.id.list_file);

        adapter = new Adapter();

        fileViewModel = new ViewModelProvider(this, new FileViewModelFactory())
                .get(FileViewModel.class);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fileViewModel.getData().observe(this, fileInfos -> adapter.setData(fileInfos));
        app = (App) getApplication();
        fileViewModel.setCatalog(app.getCurrentCatalog());
        fileViewModel.getCatalog().observe(this, catalogDTO -> {
            loadData();
            getSupportActionBar().setTitle(catalogDTO.getName());
        });

        findViewById(R.id.fab).setOnClickListener(v -> addDialog());
    }

    public void addDialog() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                PICKFILE_RESULT_CODE
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICKFILE_RESULT_CODE) {
                Uri dataUri = data.getData();
                uploadFile(dataUri);

            }
        }
    }

    /*    public void addFile(String catalogName,boolean access){
            app.getApi().getApiCatalog().addCatalog(catalogName,access).enqueue(new Callback<Responce>() {
                @Override
                public void onResponse(Call<Responce> call,@NotNull Response<Responce> response) {
                    if(response.errorBody()!=null){
                        Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(response.body().getStatusCode()!=200){
                        Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    }else {


                        Toast.makeText(FileActivity.this,
                                R.string.catalog_added,
                                Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                }

                @Override
                public void onFailure(Call<Responce> call, Throwable t) {
                    Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                }
            });
        }*/
    public void loadData() {


        fileViewModel.getData().setValue(fileViewModel.getCatalog().getValue().getFileList());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
    private void uploadFile(Uri uri) {

        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);

             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ) {


            if (uri != null) {

                int nRead;
                byte[   ] data = new byte[16384];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, nRead);
                }

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), outputStream.toByteArray());

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", getFileName(uri), requestFile);
                app.getApi().getApiFile()
                        .addFile(body, fileViewModel.getCatalog().getValue().getUuid()).enqueue(new Callback<Responce>() {
                    @Override
                    public void onResponse(Call<Responce> call, Response<Responce> response) {
                        if (response.body().getStatusCode() != 200) {
                            Toast.makeText(FileActivity.this, R.string.file_uploud_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(FileActivity.this, R.string.file_upload_success, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<Responce> call, Throwable t) {
                        Toast.makeText(FileActivity.this, R.string.file_uploud_failed, Toast.LENGTH_SHORT).show();

                    }
                });
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_button: {
                LoginActivity.start(this);
                break;
            }
            case R.id.refresh: {
                loadData();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<FileInfo> data = new ArrayList<>();

        public void setData(List<FileInfo> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_catalog, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind(data.get(i));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        FileInfo dataDTO;

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            //TODO
            // itemView.setOnClickListener());
        }

        public void bind(FileInfo data) {
            dataDTO = data;
            text.setText(data.getName());
        }
    }
}