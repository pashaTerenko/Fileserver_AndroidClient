package com.terenko.fileserver.Layout.ui.File;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.terenko.fileserver.API.ApiService;
import com.terenko.fileserver.App;
import com.terenko.fileserver.DTO.CatalogDTO;
import com.terenko.fileserver.DTO.FileInfo;
import com.terenko.fileserver.DTO.Responce;
import com.terenko.fileserver.Layout.ui.Catalog.CatalogActivity;
import com.terenko.fileserver.Layout.ui.login.LoginActivity;
import com.terenko.fileserver.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FileActivity extends AppCompatActivity {


    private static final int PICKFILE_RESULT_CODE = 111;

     static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    App app;
    private FileViewModel fileViewModel;
    RecyclerView recyclerView;
    Adapter adapter;
    ProgressBar progress;
    SpeedDialView speedDialView;
    public static void start(Context caller) {
        Intent intent = new Intent(caller, FileActivity.class);
        caller.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        recyclerView = findViewById(R.id.list_file);
      initSpinner();
      initFab();
        adapter = new Adapter();

        fileViewModel = new ViewModelProvider(this, new FileViewModelFactory())
                .get(FileViewModel.class);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fileViewModel.getData().observe(this, fileInfos -> adapter.setData(fileInfos));
        app = (App) getApplication();
        fileViewModel.setApp(app);
        fileViewModel.setActivity(this);
        fileViewModel.setCatalog(app.getCurrentCatalog());
        fileViewModel.getCatalog().observe(this, catalogDTO -> {
            loadData();
            getSupportActionBar().setTitle(catalogDTO.getName());
        });

        //findViewById(R.id.fab).setOnClickListener(v -> addDialog());
    }

    public void initFab(){
        speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_delete_catalog, R.drawable.delete_button)
                        .create());
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_create_catalog, R.drawable.create_catalog)
                        .create());




        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {

            }
        });
        speedDialView.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.fab_create_catalog:
                    addDialog();
                    break;
                case R.id.fab_delete_catalog:
                    deleteDialog();
                    break;


            }
            return true;
        });
    }


    private void initSpinner(){
        ConstraintLayout layout=findViewById(R.id.container);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(300, 300);
        Sprite doubleBounce = new DoubleBounce();
        doubleBounce.setColor(Color.YELLOW);
        progress.setIndeterminateDrawable(doubleBounce);
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.startToStart = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        layout.addView(progress, params);
        progress.setVisibility(View.GONE);
    }
    private boolean writeResponseBodyToDisk(ResponseBody body,FileInfo fileInfo) {
        try {
            // todo change the file location/name according to your needs
            File tempFile = new File( getExternalFilesDir(null),
                    fileInfo.getUuid()+ "." + fileExt(fileInfo.getName()) /* prefix */         /* suffix */
                    /* directory */
            );

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(tempFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;


                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
    private void addDialog() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                PICKFILE_RESULT_CODE
        );
    }
    public void deleteFile(){
        if (fileViewModel.getSelectedFiles().getValue().size()==0){
            Toast.makeText(FileActivity.this,getString(R.string.no_item_string),Toast.LENGTH_SHORT).show();
            return;
        }
        fileViewModel.getSelectedFiles().getValue().forEach(x->{
                    getApp().getApi().getApiFile().delFile(fileViewModel.getCatalog().getValue().getUuid(),x.getUuid()).enqueue(new Callback<Responce>() {
                        @Override
                        public void onResponse(Call<Responce> call, Response<Responce> response) {
                            if(response.errorBody()!=null){
                                Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(response.body().getStatusCode()!=200){
                                Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                            }else {


                                Toast.makeText(FileActivity.this,
                                        FileActivity.this.getString(R.string.catalog_delete_toast),
                                        Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        }

                        @Override
                        public void onFailure(Call<Responce> call, Throwable t) {
                            Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                        }
                    });


                }
        );
    }

    public  void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_file_string);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) ->deleteFile() )
                .setNegativeButton("No", (dialog, id) -> dialog.cancel()).show();
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

    private void loadData() {

        ApiService apiService = app.getApi();
        if (apiService == null) {
            Toast.makeText(FileActivity.this, R.string.toast_login, Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getApiFile().getFilesFromCatalog(fileViewModel.getCatalog().getValue().getUuid()).enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call, @NotNull Response<Responce> response) {
                if (response.errorBody() != null) {
                    Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().getStatusCode() != 200) {
                    Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                } else {
                    fileViewModel.getData().setValue(response.body().getDtoList().stream().map(x -> (FileInfo) x).collect(Collectors.toList()));

                }
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void openFile(File file) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(file.getAbsolutePath()).substring(1));
        newIntent.setDataAndType(FileProvider.getUriForFile(FileActivity.this, this.getApplicationContext().getPackageName() + ".provider", file), mimeType);
        newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }


    }




    private void downloadFile(FileInfo fileInfo) {
        loadSpinnerTurnOn();
        app.getApi().getApiFile().download(fileViewModel.getCatalog().getValue().getUuid(), fileInfo.getUuid()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                runOnUiThread(() -> loadSpinnerTurnOff());
                if (response.errorBody() != null) {
                    Toast.makeText(FileActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        boolean writtenToDisk = writeResponseBodyToDisk( response.body(), fileInfo);


                        return null;
                    }
                }.execute();
            }



            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> loadSpinnerTurnOff());
                Toast.makeText(FileActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    public void onItemSelected(FileInfo fileInfo)  {


        if (new File(getExternalFilesDir(null) , fileInfo.getUuid() + "." + fileExt(fileInfo.getName())).exists())
            openFile(new File(  getExternalFilesDir(null) , fileInfo.getUuid() + "." + fileExt(fileInfo.getName())));
        else
            downloadFile(fileInfo);
    }

    public App getApp() {
        return app;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void loadSpinnerTurnOn() {

        progress.setVisibility(View.VISIBLE);

    }

    public void loadSpinnerTurnOff() {
        progress.setVisibility(View.GONE);
    }

    private void uploadFile(Uri uri) {
        loadSpinnerTurnOn();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);

             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {


            if (uri != null) {

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, nRead);
                }

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), outputStream.toByteArray());

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileViewModel.getFileName(uri), requestFile);
                app.getApi().getApiFile()
                        .addFile(body, fileViewModel.getCatalog().getValue().getUuid()).enqueue(new Callback<Responce>() {
                    @Override
                    public void onResponse(Call<Responce> call, Response<Responce> response) {
                        runOnUiThread(() -> loadSpinnerTurnOff());
                        if (response.body().getStatusCode() != 200) {


                            Toast.makeText(FileActivity.this, R.string.file_uploud_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(FileActivity.this, R.string.file_upload_success, Toast.LENGTH_SHORT).show();
                        loadData();

                    }

                    @Override
                    public void onFailure(Call<Responce> call, Throwable t) {
                        runOnUiThread(() -> loadSpinnerTurnOff());

                        Toast.makeText(FileActivity.this, R.string.file_uploud_failed, Toast.LENGTH_SHORT).show();

                    }
                });
            }

        } catch (FileNotFoundException e) {
            loadSpinnerTurnOff();
            e.printStackTrace();
        } catch (IOException e) {
            loadSpinnerTurnOff();
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
                    .inflate(R.layout.item_file, viewGroup, false));
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
        boolean isSelected=false;
        boolean recentlySelected=false;
        TextView text;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            cardView=itemView.findViewById(R.id.card);
            FileActivity context = (FileActivity) itemView.getContext();
            itemView.setOnLongClickListener(v -> {
                        if (!isSelected) {
                            cardView.setCardBackgroundColor(Color.GREEN);
                            ArrayList<FileInfo> fileInfos = new ArrayList<>(context.fileViewModel.getSelectedFiles().getValue());
                            fileInfos.add(dataDTO);
                            context.fileViewModel.getSelectedFiles().setValue(fileInfos);
                            isSelected = true;
                            recentlySelected = true;
                        } else {
                            ArrayList<FileInfo> fileInfos = new ArrayList<>(context.fileViewModel.getSelectedFiles().getValue());
                            fileInfos.remove(dataDTO);
                            cardView.setCardBackgroundColor(Color.WHITE);
                            context.fileViewModel.getSelectedFiles().setValue(fileInfos);
                            isSelected = false;
                        }
                    return false;
            });
            itemView.setOnClickListener(v -> {
                if(!isSelected) {
                    context.onItemSelected(dataDTO);
                }
                else {
                    if (recentlySelected)recentlySelected=false;
                    else {
                        ArrayList<FileInfo> fileInfos = new ArrayList<>(context.fileViewModel.getSelectedFiles().getValue());
                        fileInfos.remove(dataDTO);
                        cardView.setCardBackgroundColor(Color.WHITE);
                        context.fileViewModel.getSelectedFiles().setValue(fileInfos);
                        isSelected = false;
                    }
                }

            });
        }

        public void bind(FileInfo data) {
            dataDTO = data;
            text.setText(data.getName());
        }
    }
}