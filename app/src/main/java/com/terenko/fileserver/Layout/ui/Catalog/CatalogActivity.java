package com.terenko.fileserver.Layout.ui.Catalog;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
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

import br.com.forusers.heinsinputdialogs.HeinsInputDialog;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputStringListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CatalogActivity extends AppCompatActivity {
    App app;
    private CatalogViewModel catalogViewModel;
    RecyclerView recyclerView;
    Adapter adapter;
    SpeedDialView speedDialView;

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
        catalogViewModel.getData().observe(this, catalogDTO -> adapter.setData(catalogDTO));
        app = (App) getApplication();
        app.getAccount().observe(this, account -> {
                loadData();
                getSupportActionBar().setTitle(account.getLogin());
        });
        initFab();

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

    public void deleteCatalog(){
        if (catalogViewModel.getSelectedCatalogs().getValue().size()==0){
            Toast.makeText(CatalogActivity.this,getString(R.string.no_item_string),Toast.LENGTH_SHORT).show();
            return;
        }
        catalogViewModel.getSelectedCatalogs().getValue().forEach(x->{
                getApp().getApi().getApiCatalog().delCatalog(x.getUuid()).enqueue(new Callback<Responce>() {
                    @Override
                    public void onResponse(Call<Responce> call, Response<Responce> response) {
                        if(response.errorBody()!=null){
                            Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(response.body().getStatusCode()!=200){
                            Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                        }else {


                            Toast.makeText(CatalogActivity.this,
                                  CatalogActivity.this.getString(R.string.catalog_delete_toast),
                                    Toast.LENGTH_SHORT).show();
                            loadData();
                        }
                    }

                    @Override
                    public void onFailure(Call<Responce> call, Throwable t) {
                        Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    }
                });


                }
                );
    }

    public  void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) ->deleteCatalog() )
                .setNegativeButton("No", (dialog, id) -> dialog.cancel()).show();
    }
    public  void addDialog(){
        HeinsInputDialog dialog = new HeinsInputDialog(this);
        dialog.setPositiveButton(new OnInputStringListener() {
            @Override
            public boolean onInputString(AlertDialog alertDialog, String s) {
                addCatalog(s,false);
                return false;
            }
        });
        dialog.setTitle(R.string.add_catalog_string);
        dialog.setHint(R.string.add_catalog_hint_dialog);
        dialog.show();
    }
    public void addCatalog(String catalogName,boolean access){
        ApiService apiService=app.getApi();
        if(apiService==null){
            Toast.makeText(CatalogActivity.this, R.string.toast_login, Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getApiCatalog().addCatalog(catalogName,access).enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call,@NotNull Response<Responce> response) {
                if(response.errorBody()!=null){
                    Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.body().getStatusCode()!=200){
                    Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                }else {


                    Toast.makeText(CatalogActivity.this,
                            R.string.catalog_added,
                            Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void loadData(){
        ApiService apiService=app.getApi();
        if(apiService==null){
            Toast.makeText(CatalogActivity.this, R.string.toast_login, Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getApiCatalog().getUserCatalogs().enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call,@NotNull Response<Responce> response) {
                if(response.errorBody()!=null){
                    Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.body().getStatusCode()!=200){
                    Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
                }else {
                catalogViewModel.getData().setValue( response.body().getDtoList().stream().map(x->(CatalogDTO)x).collect(Collectors.toList()));

                }
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                Toast.makeText(CatalogActivity.this, R.string.Data_loading_error, Toast.LENGTH_SHORT).show();
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
        boolean isSelected=false;
        boolean recentlySelected=false;
        TextView text;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            cardView=itemView.findViewById(R.id.card);

            CatalogActivity catalogActivity =(CatalogActivity)itemView.getContext();
            itemView.setOnLongClickListener(v -> {
                if (!isSelected) {
                    cardView.setCardBackgroundColor(Color.GREEN);
                    ArrayList<CatalogDTO> catalogDTOS = new ArrayList<>( catalogActivity.catalogViewModel.getSelectedCatalogs().getValue());
                    catalogDTOS.add(dataDTO);
                    catalogActivity.catalogViewModel.getSelectedCatalogs().setValue(catalogDTOS);
                    isSelected = true;
                    recentlySelected=true;
                }else {
                    ArrayList<CatalogDTO> catalogDTOS = new ArrayList<>( catalogActivity.catalogViewModel.getSelectedCatalogs().getValue());
                    catalogDTOS.remove(dataDTO);
                    cardView.setCardBackgroundColor(Color.WHITE);
                    catalogActivity.catalogViewModel.getSelectedCatalogs().setValue(catalogDTOS);
                    isSelected=false;
                }

                return false;
            });
            itemView.setOnClickListener(v -> {
                if(!isSelected) {
                    FileActivity.start(v.getContext());
                    catalogActivity.getApp().getCurrentCatalog().setValue(dataDTO);
                }
                else {
                    if (recentlySelected)recentlySelected=false;
                    else {
                        ArrayList<CatalogDTO> catalogDTOS = new ArrayList<>(catalogActivity.catalogViewModel.getSelectedCatalogs().getValue());
                        catalogDTOS.remove(dataDTO);
                        cardView.setCardBackgroundColor(Color.WHITE);
                        catalogActivity.catalogViewModel.getSelectedCatalogs().setValue(catalogDTOS);
                        isSelected = false;
                    }
                }

            });
        }

        public void bind(CatalogDTO data) {
            dataDTO = data;
            text.setText(data.getName());
        }
    }
}