package com.example.kyle.dotapicker;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.kyle.dotapicker.MainActivity.hero_pool_size;

/**
 * Created by Kyle on 2/19/2017.
 */

public class DataBaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "adv-syn2.db";
    private static final String ENEMY_COL = "enemy_id";
    private static final String ID_COL = "hero_id";
    private static final String ADV_COL = "advantage";
    private static final String[] ROLE_COL = {"_id", "name", "is_carry", "is_support", "is_off_lane", "is_jungler", "is_mid", "is_roaming", "is_initiator", "is_pusher", "is_disabler"};
    private static final int DATABASE_VERSION = 11;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // setForcedUpgrade is needed to enable updating database version updates without an upgrade
        // script (explained here: https://github.com/jgilfelt/android-sqlite-asset-helper )
        setForcedUpgrade();

        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }

    public long getNumOfRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        long numRows = DatabaseUtils.queryNumEntries(db, "Advantages");
        return numRows;
    }

    public ArrayList<String> getRowAndDate(int col, String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + ENEMY_COL + " = '" + col + "'", null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        String array[] = new String[cursor.getCount()+1];
        while(!cursor.isAfterLast()) {
            names.add(cursor.getString(cursor.getColumnIndex(ADV_COL)));
            cursor.moveToNext();
        }
        cursor.close();
        if(col < hero_pool_size-2) {
            for(int j = 0; j< col-1; j++){
                array[j] = names.get(j);
            }
            array[col-1] = "0";
            for(int j = col; j < array.length; j++){
                array[j] = names.get(j-1);
            }
        } else {
            for(int j = 0; j< col-1; j++){
                array[j] = names.get(j);
            }
            array[hero_pool_size-3] = "0";
        }
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(array));
        return arrayList;
    }

    public double[][] advantageTableBuilder(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        double[][] names = new double[hero_pool_size][hero_pool_size];
        for(int i = 1; i < hero_pool_size-1; i++) {
            ArrayList<String> enemy_col = getRowAndDate(i, TABLE_NAME);

            for(int j = 1; j < 114; j++) {
                names[j][i] = Double.valueOf(enemy_col.get(j-1));
            }
        }
        return names;
    }

    public int[][] roleTableBuilder(String TABLE_NAME) {
        int[][] hero_roles = new int[hero_pool_size][11];
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToFirst();
        int k = 1;
        while(!cursor.isAfterLast()) {
            for(int i = 0; i < 11; i++){
                //we don't need the hero name
                int j = i-1;
                if(i != 1) {
                    String str = (cursor.getString(cursor.getColumnIndex(String.valueOf(ROLE_COL[i]))));
                    if(j < 0) {
                        j = 0;
                    }
                    hero_roles[k][j] = Integer.parseInt(str);
                }
            }
            k++;
            cursor.moveToNext();
        }
        return hero_roles;
    }
}