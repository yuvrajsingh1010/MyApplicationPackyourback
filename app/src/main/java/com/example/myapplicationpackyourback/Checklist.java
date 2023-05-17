package com.example.myapplicationpackyourback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplicationpackyourback.Adapter.CheckListAdapter;
import com.example.myapplicationpackyourback.Constants.MyConstants;
import com.example.myapplicationpackyourback.Data.AppData;
import com.example.myapplicationpackyourback.Database.RoomDB;
import com.example.myapplicationpackyourback.Models.Items;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Checklist extends AppCompatActivity {

    RecyclerView recyclerView;
    CheckListAdapter checkListAdapter;
    RoomDB database;
    List<Items> itemsList =new ArrayList<>();
    String header ,show;


    EditText txtAdd;
    Button btnAdd;
    LinearLayout linearLayout;

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one,menu);
        if(MyConstants.MY_SELECTIONS.equals(header)){
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);

        }else if (MyConstants.MY_LIST_CAMEL_CASE.equals(header))
            menu.getItem(1).setVisible(false);
        MenuItem menuItem =menu.findItem(R.id.btnSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Items> mFinalList = new ArrayList<>();
                for (Items items:itemsList){
                    if (items.getItemname().toLowerCase().startsWith(newText.toLowerCase())){
                        mFinalList.add(items);
                    }
                }
                updateRecycler(mFinalList);
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent =new Intent(this,Checklist.class);
        AppData appData = new AppData(database,this);
        switch (item.getItemId()){
            case R.id.btnMySelection:
                intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_SELECTIONS);
                intent.putExtra(MyConstants.SHOW_SMALL,MyConstants.FALSE_STRING);
                startIntentSenderForResult(intent,101);
                return true;
            case R.id.btnCustomList:
                intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_LIST_CAMEL_CASE);
                intent.putExtra(MyConstants.SHOW_SMALL,MyConstants.TRUE_STRING);
                startIntentSenderForResult(intent);
                return true;
            case R.id.btnDeleteDefault:
                new AlertDialog.Builder(this)
                        .setTitle("Delete default Data")
                        .setMessage("Aren you sure ?\n\nAs this will delete the default data provided by(PAck)")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                appData.persistDataByCatagory(header,true);
                                updateRecycler(itemsList);
                            }
                        }).setIcon(R.drawable.ic_warning)
                        .show();
                return true;
            case R.id.btnReset:
                new AlertDialog.Builder(this)
                        .setTitle("Reset the Data")
                        .setMessage("Are you Sure you want two reset data?("+header+")")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appData.persistDataByCatagory(header,false);
                                itemsList = database.mainDao().getAll(header);
                                updateRecycler(itemsList);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setIcon(R.drawable.ic_warning)
                        .show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent =getIntent();
        header = intent.getStringExtra(MyConstants.HEADER_SMALL);
        show = intent.getStringExtra(MyConstants.SHOW_SMALL);
        getSupportActionBar().setTitle(header);
        txtAdd = findViewById(R.id.txtAdd);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayout = findViewById(R.id.linearLayout);
        database = RoomDB.getInstance(this);
        if(MyConstants.FALSE_STRING.equals(show)){
            linearLayout.setVisibility(View.GONE);
            itemsList =database.mainDao().getAllSelected(true);
        }else {
            itemsList =database.mainDao().getAll(header);
        }
        updateRecycler(itemsList);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = txtAdd.getText().toString();
                    if (itemName!= null && !itemName.isEmpty()){
                        addNewItem(itemName);
                        Toast.makeText(Checklist.this, "Item Added", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Checklist.this, "Empty can't be added", Toast.LENGTH_SHORT).show();
                    }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();;
        return true;
    }

    private void addNewItem(String itemName){
        Items items = new Items();
        items.setChecked(false);
        items.setCategory(header);
        items.setItemname(itemName);
        items.setAddedby(MyConstants.USER_SMALL);
        database.mainDao().saveItem(items);
        itemsList =database.mainDao().getAll(header);
        updateRecycler(itemsList);
        recyclerView.scrollToPosition(checkListAdapter.getItemCount() - 1);
        txtAdd.setText("");
    }
    private void updateRecycler(List<Items> itemsList){

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,LinearLayout.VERTICAL));
        checkListAdapter =new CheckListAdapter(Checklist.this,itemsList,database,show);
        recyclerView.setAdapter(checkListAdapter);
    }
}