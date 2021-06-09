package com.zebra.basicintent1;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.util.ArrayList;


public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "inventory.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Location (Id INTEGER PRIMARY KEY AUTOINCREMENT, locationName text)");
        db.execSQL("CREATE TABLE Inventory (Id INTEGER PRIMARY KEY AUTOINCREMENT, NrInv text, Name text , Location text, ScanDate text)");
        db.execSQL("CREATE TABLE Ingredient (ING_Id INTEGER PRIMARY KEY AUTOINCREMENT, ING_INV text, ING_Name text, ING_st_koszt text, ING_data_likwidacji)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Location");
        db.execSQL("DROP TABLE IF EXISTS Inventory");
        db.execSQL("DROP TABLE IF EXISTS Ingredient");
        onCreate(db);
    }

    public void truncateLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Location");
    }

    public void truncateIngredient() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Ingredient");
    }

    public boolean insertData(String nr_inv, String name, String location, String dataScan)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NrInv", nr_inv);
        contentValues.put("Name", name);
        contentValues.put("Location", location);
        contentValues.put("ScanDate", dataScan);

        long result = db.insert("inventory", null, contentValues);
        if (result == -1) return false;
        else
                return true;
    }
    public int isSaved(String nr_inv) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteStatement s = db.compileStatement( "select count(*) from inventory where NrInv = " + "'" + nr_inv + "'" );
        int count = (int) s.simpleQueryForLong();
        return count;
    }

    //tworzenie lokalnej bazy składników
    public boolean addToIngredient(String nr_inv, String name, String st_koszt, String data_likwidacji)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ING_INV", nr_inv);
        contentValues.put("ING_Name", name);
        contentValues.put("ING_st_koszt", st_koszt);
        contentValues.put("ING_data_likwidacji", data_likwidacji);

        long result = db.insert("Ingredient", null, contentValues);
        if (result == -1) return false;

        else
            return true;

    }

    //tworzenie lokalnej bazy pól spisowych
    public boolean addToLocation(String locationName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("locationName", locationName);

        long result = db.insert("Location", null, contentValues);
        if (result == -1) return false;
        else
            return true;
    }


    public Ingredient getIngredient(String nr_inv){
        SQLiteDatabase db = this.getReadableDatabase();
        Ingredient obj=null;
        //Log.d("DBhelper.getIngredient:", nr_inv);
        Cursor c = db.rawQuery("Select * from Ingredient where ING_INV = " + "'"+ nr_inv+ "'",null);

        c.moveToFirst();
        if (c.getCount()>0) {
            obj = new Ingredient(
                    c.getString(c.getColumnIndex("ING_INV")),
                    c.getString(c.getColumnIndex("ING_Name")),
                    c.getString(c.getColumnIndex("ING_st_koszt")),
                    c.getString(c.getColumnIndex("ING_data_likwidacji"))
            );
        }

        return obj;
    }


    //Get Locations list from database to Array
    public ArrayList<String> getLocations(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> ls = new ArrayList<String>();

        Cursor c = db.rawQuery("Select * from Location",null );
        if (c.moveToFirst()) {
            do {
                ls.add(c.getString(1));
            } while (c.moveToNext());
        }
        return ls;
    }

    public long locationCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteStatement s = db.compileStatement( "select count(*) from Location" );
        long count = s.simpleQueryForLong();
        return count;
    }

}
