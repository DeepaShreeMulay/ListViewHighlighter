package com.vritti.yard;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin-3 on 6/21/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "YardDB";

    public DatabaseHelper(Context parent) {
        super(parent, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        utility ut = new utility();
        db.execSQL(ut.getBusTimeTable());

        db.execSQL("CREATE TABLE RouteStationTable(RouteStationId TEXT, TimeTableId TEXT, StationMasterId TEXT" +
                ",InstallationId TEXT, ETATime TEXT, stationName TEXT)");
        db.execSQL("CREATE TABLE SelectInstallationId(InstallationId TEXT, InstallationDesc TEXT, NetworkCode TEXT, YardId TEXT)");
        db.execSQL("CREATE TABLE InstallationMaster(InstalationId TEXT, InstalationName TEXT, InstallationDesc TEXT" +
                ", Address TEXT, NetworkCode TEXT, LastbusReporting TEXT, LastAdvDate TEXT" +
                ", ServerTime TEXT, SubNetworkCode TEXT)");
       /* db.execSQL("CREATE TABLE McLocTable(LocationMasterId TEXT, LocationCode TEXT, LocationDesc TEXT)");
        db.execSQL("CREATE TABLE McLocTable(LocationMasterId TEXT, LocationCode TEXT, LocationDesc TEXT)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS BusTimeTable");
        db.execSQL("DROP TABLE IF EXISTS RouteStationTable");
        db.execSQL("DROP TABLE IF EXISTS SelectInstallationId");
        db.execSQL("DROP TABLE IF EXISTS InstallationMaster");/*
        db.execSQL("DROP TABLE IF EXISTS PlatformMaster");
        db.execSQL("DROP TABLE IF EXISTS BusTypeMaster");*/
        onCreate(db);
    }

    public void AddSelectInstallationId(String InstallationId, String InstallationDesc, String NetworkCode,String YardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("SelectInstallationId", null, null);
        ContentValues cv = new ContentValues();
        cv.put("InstallationId", InstallationId);
        cv.put("InstallationDesc", InstallationDesc);
        cv.put("NetworkCode", NetworkCode);
        cv.put("YardId", YardId);
        db.insert("SelectInstallationId", null, cv);
    }

    public void AddRouteStation(String RouteStationId ,String TimeTableId ,String StationMasterId,
                                String InstallationId ,String ETATime ,String stationName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("RouteStationTable", null, null);
        ContentValues cv = new ContentValues();
        cv.put("RouteStationId", RouteStationId);
        cv.put("TimeTableId", TimeTableId);
        cv.put("StationMasterId", StationMasterId);
        cv.put("InstallationId", InstallationId);
        cv.put("ETATime", ETATime);
        cv.put("stationName", stationName);
        db.insert("RouteStationTable", null, cv);
    }

    public void AddInstallationMaster(String InstalationId, String InstalationName, String InstallationDesc, String Address
                                      , String NetworkCode , String LastbusReporting , String LastAdvDate , String
                                      ServerTime , String SubNetworkCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("InstallationMaster", null, null);
        ContentValues cv = new ContentValues();
        cv.put("InstalationId", InstalationId);
        cv.put("InstalationName", InstalationName);
        cv.put("InstallationDesc", InstallationDesc);
        cv.put("Address", Address);
        cv.put("LastbusReporting", LastbusReporting);
        cv.put("NetworkCode", NetworkCode);
        cv.put("LastAdvDate", LastAdvDate);
        cv.put("ServerTime", ServerTime);
        cv.put("SubNetworkCode", SubNetworkCode);
        db.insert("InstallationMaster", null, cv);
    }

    public void AddBusTimeTable(String TimeTableId ,String ST ,String TV ,String StartingStationMasterId ,String
             DestinationStationMasterId ,String ViaStationMasterId ,String DivisionMasterId ,String
         PlatformMasterId ,String BusTypeMasterId ,String StartingStation ,String DestinationStation ,String
        BusTypeName ,String ViaStation ,String DivisionName ,String DutyNo, String DeleteFlag, String DeleteDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("TimeTableId", TimeTableId);
        cv.put("ST", ST);
        cv.put("TV", TV);
        cv.put("StartingStationMasterId", StartingStationMasterId);
        cv.put("DestinationStationMasterId", DestinationStationMasterId);
        cv.put("ViaStationMasterId", ViaStationMasterId);
        cv.put("DivisionMasterId", DivisionMasterId);
        cv.put("PlatformMasterId", PlatformMasterId);
        cv.put("BusTypeMasterId", BusTypeMasterId);
        cv.put("StartingStation", StartingStation);
        cv.put("DestinationStation", DestinationStation);
        cv.put("BusTypeName", BusTypeName);
        cv.put("ViaStation", ViaStation);
        cv.put("DivisionName", DivisionName);
        cv.put("DutyNo", DutyNo);
        cv.put("DeleteFlag", DeleteFlag);
        cv.put("DeleteDate", DeleteDate);
        db.insert("BusTimeTable", null, cv);
    }

    public void updateBusTimeTable(String TimeTableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DeleteFlag", "Y");
        cv.put("DeleteDate", getCurrDate());
        db.update("BusTimeTable", cv, "TimeTableId" + " = '" + TimeTableId + "'", null);
    }

    public void updateBusTimeTableAll(String DeleteDate,String DeleteFlag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DeleteFlag", "N");
        cv.put("DeleteDate", "");
        db.update("BusTimeTable", cv, "DeleteDate" + " = '" + DeleteDate + "' and DeleteFlag" + " = '" + DeleteFlag + "'", null);
    }

    private String getCurrDate() {
        String intime = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        intime = dateFormat.format(c.getTime());
        Log.e("dateTime", intime);
        return intime;
    }

    public void UpdatePumpList(String pump_no ,String  pump_name ,String  item_code ,String  item_desc ,String pump_seq,String  opening ,String  closing ,String  testing ,String  SaleLtrs ,String  Rate ,String  Amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pump_no", pump_no);
        cv.put("pump_name", pump_name);
        cv.put("item_code", item_code);
        cv.put("item_desc", item_desc);
        cv.put("pump_seq", pump_seq);
        cv.put("opening", opening);
        cv.put("closing", closing);
        cv.put("testing", testing);
        cv.put("SaleLtrs", SaleLtrs);
        cv.put("Rate", Rate);
        cv.put("Amount", Amount);
        db.update("PumpList", cv, "pump_no" + "='" + pump_no+"'", null);
    }
}
