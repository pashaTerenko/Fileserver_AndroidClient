package com.terenko.fileserver.Layout.ui.Catalog;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.terenko.fileserver.API.ApiService;
import com.terenko.fileserver.App;
import com.terenko.fileserver.DTO.CatalogDTO;
import com.terenko.fileserver.DTO.Responce;
import com.terenko.fileserver.Layout.ui.File.FileActivity;
import com.terenko.fileserver.Layout.ui.login.LoginActivity;
import com.terenko.fileserver.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    App app;
    private CatalogViewModel catalogViewModel;
    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list_catalog);

        adapter = new Adapter();

        catalogViewModel= new ViewModelProvider(this, new CatalogViewModelFactory())
                .get(CatalogViewModel.class);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        catalogViewModel.getData().observe(this, new Observer<List<CatalogDTO>>() {
            @Override
            public void onChanged(List<CatalogDTO> catalogDTO) {
            adapter.setData(catalogDTO);
            }});
        app = (App) getApplication();
        app.getAccount().observe(this, account -> {
                loadData();
                getSupportActionBar().setTitle(account.getLogin());
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
addDialog();
            }
        });
    }
    public  void addDialog(){
        View addView = View.inflate(this, R.layout.dialog_add_catalog, null);
        CheckBox checkBox =  addView.findViewById(R.id.catalog_checkboxAccess);
        EditText editText=addView.findViewById(R.id.catalog_addInput);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.addDialogTitle);
        builder.setMessage("")
                .setView(addView)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    addCatalog(editText.getText().toString(),checkBox.isChecked());}
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }
    public void addCatalog(String catalogName,boolean access){
        ApiService apiService=app.getApi();
        if(apiService==null){
            Toast.makeText(MainActivity.this, R.string.toast_login, Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getApiCatalog().addCatalog(catalogName,access).enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call,@NotNull Response<Responce> response) {
                if(response.errorBody()!=null){
                    Toast.makeText(MainActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.body().getStatusCode()!=200){
                    Toast.makeText(MainActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                }else {


                    Toast.makeText(MainActivity.this,
                            R.string.catalog_added,
                            Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void loadData(){
        ApiService apiService=app.getApi();
        if(apiService==null){
            Toast.makeText(MainActivity.this, R.string.toast_login, Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getApiCatalog().getUserCatalogs().enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call,@NotNull Response<Responce> response) {
                if(response.errorBody()!=null){
                    Toast.makeText(MainActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.body().getStatusCode()!=200){
                    Toast.makeText(MainActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                }else {
                catalogViewModel.getData().setValue( response.body().getDtoList().stream().map(x->(CatalogDTO)x).collect(Collectors.toList()));

                }
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void onItemSelected(CatalogDTO catalogDTO){

    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
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

        ArrayList<CatalogDTO> data = new ArrayList<>();

        public void setData(List<CatalogDTO> data) {
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

        CatalogDTO dataDTO;

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            //TODO
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileActivity.start(v.getContext());
                MainActivity mainActivity=(MainActivity)v.getContext();
                mainActivity.getApp().getCurrentCatalog().setValue(dataDTO);
                }
            });
        }

        public void bind(CatalogDTO data) {
            dataDTO = data;
            text.setText(data.getName());
        }
    }
}