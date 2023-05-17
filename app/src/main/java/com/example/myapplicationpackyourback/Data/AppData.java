package com.example.myapplicationpackyourback.Data;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.room.Database;

import com.example.myapplicationpackyourback.Constants.MyConstants;
import com.example.myapplicationpackyourback.Database.RoomDB;
import com.example.myapplicationpackyourback.Models.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData extends Application {
    RoomDB database;
    String category;
    Context context;
    public static final String LAST_VERSION="LAST_VERSION";
    public static final int NEW_VERSION = 1;

    public AppData(RoomDB database) {
        this.database = database;
    }

    public AppData(RoomDB database, Context context) {
        this.database = database;
        this.context = context;
    }
    public List<Items> getBasicData(){
        category="BASIC NEEDS";
        List<Items>  basicItems = new ArrayList<>();
        basicItems.clear();
        basicItems.add(new Items("Visa", category,false));
        basicItems.add(new Items("Passport", category,false));
        return  basicItems;
    }
    public List<Items> getPersonalCareData(){
        String []data ={"Tooth-paste","Tooth-Brush","Flouse","Mouthwash"};
        return prepareItemList(MyConstants.PERSONAL_CARE_CAMEL_CASE,data);
    }
    public List<Items> getClothingData(){
        String []data ={"Tooth-paste","Tooth-Brush","Flouse","Mouthwash"};
        return prepareItemList(MyConstants.CLOTHING_CAMEL_CASE,data);
    }
    public List<Items> getBabyNeedsData(){
        String []data ={"Tooth-paste","Tooth-Brush","Flouse","Mouthwash"};
        return prepareItemList(MyConstants.BABY_NEEDS_CAMEL_CASE,data);
    }
    public List<Items> getHealthData(){
        String []data ={"Tooth-paste","Tooth-Brush","F","Mouthwash"};
        return prepareItemList(MyConstants.HEALTH_CAMEL_CASE,data);
    }
    public List<Items> getTechnologyData(){
        String []data ={"Tech"};
        return prepareItemList(MyConstants.TECHNOLOGY_CAMEL_CASE,data);
    }
    public List<Items> getFoodData(){
        String []data ={"SandWich"};
        return prepareItemList(MyConstants.FOOD_CAMEL_CASE,data);
    }
    public List<Items> getBeachSuppliesData(){
        String []data ={"Soap"};
        return prepareItemList(MyConstants.BEACH_SUPPLIES_CAMEL_CASE,data);
    }
    public List<Items> getCarSuppliesData(){
        String []data ={"Tool"};
        return prepareItemList(MyConstants.CAR_SUPPLIES_CAMEL_CASE,data);
    }
    public List<Items> getNeedsData(){
        String []data ={"Towel"};
        return prepareItemList(MyConstants.NEEDS_CAMEL_CASE,data);
    }
    public List<Items> prepareItemList(String category,String []data){
        List<String> list = Arrays.asList(data);
        List<Items> dataList =new ArrayList<>();
        dataList.clear();

        for (int i=0;i<list.size();i++){
            dataList.add(new Items(list.get(i),category,false ));
        }
        return dataList;
    }

    public List<List<Items>> getAllData(){
        List<List<Items>> listOfAllItems = new ArrayList<>();
        listOfAllItems.add(getBasicData());
        listOfAllItems.add(getClothingData());
        listOfAllItems.add(getPersonalCareData());
        listOfAllItems.add(getBabyNeedsData());
        listOfAllItems.add(getHealthData());
        listOfAllItems.add(getTechnologyData());
        listOfAllItems.add(getFoodData());
        listOfAllItems.add(getBeachSuppliesData());
        listOfAllItems.add(getCarSuppliesData());
        listOfAllItems.add(getNeedsData());
        return listOfAllItems;
    }
    public void persistAllData(){
        List<List<Items>> listOfAllItem =getAllData();
        for (List<Items> list:listOfAllItem){
            for (Items items:list){
                database.mainDao().saveItem(items);
            }
        }
        System.out.println("Data Added");
    }

    public void persistDataByCatagory(String category,Boolean onlyDelete){
        try{
            List<Items> list =deleteAndGetListByCategory(category, onlyDelete);
            if(!onlyDelete){
                for (Items items :list){
                    database.mainDao().saveItem(items);
                }
                Toast.makeText(context, "Reset Successfully", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Reset Successfully", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }



    private List<Items> deleteAndGetListByCategory(String category,Boolean onlyDelete){
        if (onlyDelete){
            database.mainDao().deleteAllByCategoryAndAddedBy(category,MyConstants.SYSTEM_SMALL);
        }else {
            database.mainDao().deleteAllByCategory(category);
        }
        switch (category){
            case MyConstants.BASIC_NEEDS_CAMEL_CASE:
                return getBasicData();
            case MyConstants.CLOTHING_CAMEL_CASE:
                return getClothingData();
            case MyConstants.PERSONAL_CARE_CAMEL_CASE:
                return getPersonalCareData();
            case MyConstants.BABY_NEEDS_CAMEL_CASE:
                return getBabyNeedsData();
            case MyConstants.HEALTH_CAMEL_CASE:
                return getHealthData();
            case MyConstants.TECHNOLOGY_CAMEL_CASE:
                return getTechnologyData();
            case MyConstants.FOOD_CAMEL_CASE:
                return getFoodData();
            case MyConstants.BEACH_SUPPLIES_CAMEL_CASE:
                return getBeachSuppliesData();
            case MyConstants.CAR_SUPPLIES_CAMEL_CASE:
                return getCarSuppliesData();
            case MyConstants.NEEDS_CAMEL_CASE:
                return getNeedsData();
            default:
                return new ArrayList<>();
        }
    }

}
