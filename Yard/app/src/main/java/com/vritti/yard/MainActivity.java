package com.vritti.yard;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    Context parent;
    ListView BusTimeTableList;
    ListView listview_via_list;

    String TimeTableId;
    String ST;
    String TV;
    String StartingStationMasterId;
    String DestinationStationMasterId;
    String ViaStationMasterId;
    String DivisionMasterId;
    String PlatformMasterId;
    String BusTypeMasterId;
    String StartingStation;
    String DestinationStation;
    String BusTypeName;
    String ViaStation;
    String DivisionName;
    String DutyNo;

    String CurrTimeRecord;

    String pdutyno;
    String pbusno;
    String ppltfno;
    String psettime;
    String ppltby;
    String preason;
    String pdriveno ="0";
    String pconductorno = "0";
    
    String edutyno;
    String ebusno;
    String epltfno;
    String esettime;
    String efrom;
    String eto;
    String evia;
    String edriveno ="0";
    String econductorno = "0";

    utility ut;
    String responsemsg = "m", sop;
    private ArrayList<String> TimeTableIdList, TimeRecord;
    private ArrayList<String> eFromList, eToList, eViaList, eBusTypeList;
    private static GetBusTimeTableAsync async;
    private static GetRouteStationTableAsync asynk;
    private static DataSyncBusReportingAsync asyncTask;
    private static DataSyncBusReportingAsyncExtra asyncTaskextra;

    private Timer autoUpdate;

    ArrayList<BusTimeTableItemBean> busTimeTableItemBeanlist;
    BusTimeTableItemBean busTimeTableItemBean;
    BusTimeTableItemListAdapter busTimeTableItemListAdapter;

    ArrayList<ViaRouteStnItemBean> viaItemBeanlist;
    ViaRouteStnItemBean viaItemBean;
    ViaRouteStationItemListAdapter viaItemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();
        if (dbvalue()){
            checkBusTimeTable();
            updatelist();
            getCurrentDateTime();
        }else {//
            if (NetworkUtils.isNetworkAvailable(parent)) {
            async = null;
            async = new GetBusTimeTableAsync();
            async.execute();
            }
        }
        setListner();
    }

    private void checkBusTimeTable() {
        DatabaseHelper db = new DatabaseHelper(parent);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery("Select distinct DeleteDate from BusTimeTable " + "where DeleteDate ='"+
                getCurrDate2()+"'", null);
        Log.e("update","dailyrecord");
        if (c.getCount()== 0){
            Log.e("update","no record");
            c.close();
            sql.close();
            db.close();
        }else{
            c.moveToFirst();
            db.updateBusTimeTableAll(getCurrDate2(),"Y");
            Log.e("update","deleted record");
            c.close();
            sql.close();
            db.close();
        }


    }

    private boolean dbvalue(){
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from BusTimeTable", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                cursor.close();
                sql.close();
                db1.close();
                return true;
            }else{
                cursor.close();
                sql.close();
                db1.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void initView() {
        parent = MainActivity.this;
        ut = new utility();
        BusTimeTableList = (ListView) findViewById(R.id.listview_bus_list);
        TimeTableIdList = new ArrayList<String>();
        TimeRecord = new ArrayList<String>();
        eFromList = new ArrayList<String>();eViaList = new ArrayList<String>();
        eToList = new ArrayList<String>();eBusTypeList = new ArrayList<String>();
        Common.INSTALLATIONID = utility.getInstallationId(parent);
        if (Common.INSTALLATIONID == null){
            Intent i = new Intent(parent,SelectStationActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void setListner() {
        BusTimeTableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getfirstData(position);
                setPrompt();
            }
        });
    }

    protected void getfirstData(int position) {
        // TODO Auto-generated method stub
        BusTypeMasterId = busTimeTableItemBeanlist.get(position).getBusTypeMasterId();
        BusTypeName = busTimeTableItemBeanlist.get(position).getBusTypeName();
        DestinationStation = busTimeTableItemBeanlist.get(position).getDestinationStation();
        DivisionMasterId = busTimeTableItemBeanlist.get(position).getDivisionMasterId();
        DestinationStationMasterId = busTimeTableItemBeanlist.get(position).getDestinationStationMasterId();
        DivisionName = busTimeTableItemBeanlist.get(position).getDivisionName();
        DutyNo = busTimeTableItemBeanlist.get(position).getDutyNo();
        PlatformMasterId = busTimeTableItemBeanlist.get(position).getPlatformMasterId();
        ST = busTimeTableItemBeanlist.get(position).getST();
        StartingStation = busTimeTableItemBeanlist.get(position).getStartingStation();
        StartingStationMasterId = busTimeTableItemBeanlist.get(position).getStartingStationMasterId();
        TimeTableId = busTimeTableItemBeanlist.get(position).getTimeTableId();
        TV = busTimeTableItemBeanlist.get(position).getTV();
        ViaStation = busTimeTableItemBeanlist.get(position).getViaStation();
        ViaStationMasterId = busTimeTableItemBeanlist.get(position).getViaStationMasterId();
    }

    protected void setPrompt() {


        LayoutInflater li = LayoutInflater.from(parent);
        View promptsView = li.inflate(R.layout.prompt_departurebus, null);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView textViewdepartfrom = (TextView) promptsView.findViewById(R.id.textViewdepartfrom);
        final TextView textViewdepartto = (TextView) promptsView.findViewById(R.id.textViewdepartto);
        final TextView textViewdepartVia = (TextView) promptsView.findViewById(R.id.textViewdepartVia);
        textViewdepartfrom.setText(StartingStation);
        textViewdepartto.setText(DestinationStation);
        textViewdepartVia.setText(ViaStation);

        final EditText editTextdepartDutyno = (EditText) promptsView.findViewById(R.id.editTextdepartDutyno);
        final EditText editTextdepartBusno = (EditText) promptsView.findViewById(R.id.editTextdepartBusno);
        final EditText editTextdepartPlatform = (EditText) promptsView.findViewById(R.id.editTextdepartPlatform);
        final EditText editTextdepartTime = (EditText) promptsView.findViewById(R.id.editTextdepartTime);
        final EditText editTextDepartLateBy = (EditText) promptsView.findViewById(R.id.editTextDepartLateBy);
        final EditText editTextdepartReason = (EditText) promptsView.findViewById(R.id.editTextdepartReason);
        final EditText editTextdepartDriver = (EditText) promptsView.findViewById(R.id.editTextdepartDriver);
        final EditText editTextdepartConductor = (EditText) promptsView.findViewById(R.id.editTextdepartConductor);
        editTextdepartDutyno.setText(DutyNo);
        editTextdepartPlatform.setText(PlatformMasterId);

        editTextdepartTime.setText(TV);
        editTextdepartTime.setKeyListener(null);
        editTextdepartTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(parent, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTextdepartTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextdepartDutyno.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editTextdepartBusno.getWindowToken(), 0);

        listview_via_list = (ListView) promptsView.findViewById(R.id.listview_via_list);
        updatelist2();
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Toast.makeText(parent, "Work In Progress", Toast.LENGTH_LONG).show();

                                // get user input and set it to result
                                pdutyno = editTextdepartDutyno.getText().toString();
                                pbusno = editTextdepartBusno.getText().toString();
                                ppltfno = editTextdepartPlatform.getText().toString();
                                psettime = editTextdepartTime.getText().toString();
                                ppltby = editTextDepartLateBy.getText().toString();
                                preason = editTextdepartReason.getText().toString();
                                pdriveno = editTextdepartDriver.getText().toString();
                                pconductorno = editTextdepartConductor.getText().toString();
                                if (ppltfno == null || editTextdepartPlatform.getText().toString().equalsIgnoreCase("")) {
                                    editTextdepartPlatform.setError("Please Enter Platform");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                } else if (pbusno == null || editTextdepartBusno.getText().toString().equalsIgnoreCase("")) {
                                    editTextdepartBusno.setError("Please Enter Bus");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                } else if (psettime == null || editTextdepartTime.getText().toString().equalsIgnoreCase("")) {
                                    editTextdepartTime.setError("Please Enter Time");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if (NetworkUtils.isNetworkAvailable(parent)) {
                                        asyncTask = null;
                                        asyncTask = new DataSyncBusReportingAsync();
                                        asyncTask.execute();
                                    }
                                    //Toast.makeText(parent, "Data Updated Successfully", Toast.LENGTH_LONG).show();
                                }
                               // updatelist();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                updatelist();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

    }

    private void getTimetableIds() {
        TimeTableIdList.clear();
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "Select Distinct TimeTableId from BusTimeTable", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                TimeTableIdList.add(cursor.getString(0));
            } while (cursor.moveToNext());
            if (NetworkUtils.isNetworkAvailable(parent)) {
                asynk = null;
                asynk = new GetRouteStationTableAsync();
                asynk.execute();
            }

            cursor.close();
            db.close();
            db1.close();
        }

    }

    public class GetBusTimeTableAsync extends AsyncTask<String, Void, String>{
        SpotsDialog SPdialog;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            utility ut = new utility();
            DatabaseHelper db = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTimeTabelWithSationName?InstallationId="
                    +Common.INSTALLATIONID
                    +"&Networkcode="
                    +Common.NETWORKCODE;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                sql.execSQL("DROP TABLE IF EXISTS BusTimeTable");
                sql.execSQL(ut.getBusTimeTable());

                Log.e("TimeTable", "resmsg : " + responsemsg);

                if (responsemsg.contains("<TimeTableId>")) {
                    sop = "valid";
                    String columnName, columnValue;

                    Cursor cur = sql.rawQuery("SELECT * FROM BusTimeTable", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table");
                    Log.e(" data..."," fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);
                            columnValue = ut.getValue(e, columnName);
                            values1.put(columnName, columnValue);
                        }
                        sql.insert("BusTimeTable",
                                null, values1);
                    }

                    cur.close();

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop == "valid") {
                    getTimetableIds();
                    updatelist();
                } else {
                    //ut.showD(getApplicationContext(),"nodata");
                }
                SPdialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SPdialog = new SpotsDialog(parent);
            SPdialog.setCancelable(false);
            SPdialog.show();
        }
    }

    private String getCurrDate2() {
        String intime = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        intime = dateFormat.format(c.getTime());
        Log.e("dateTime", intime);
        return intime;
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

    public void updatelist() {
        // TODO Auto-generated method stub
        DatabaseHelper db = new DatabaseHelper(parent);
        SQLiteDatabase sql = db.getWritableDatabase();
        busTimeTableItemBeanlist = new ArrayList<BusTimeTableItemBean>();
        busTimeTableItemBeanlist.clear();

        Cursor c = sql.rawQuery("Select * from BusTimeTable " +
                "where DeleteFlag <>'Y' and DeleteDate <> '"+getCurrDate() +"'"+
                " order by "
                + " TV "                //+ "(CommunicationId AS INT) "
                 , null);
        if (c.getCount()== 0){
            c.close();
            sql.close();
            db.close();
        }else{
            c.moveToFirst();
            int column = 0;
            do{
                busTimeTableItemBean = new BusTimeTableItemBean();
                busTimeTableItemBean.setBusTypeMasterId(c.getString( c.getColumnIndex("BusTypeMasterId") ));;
                busTimeTableItemBean.setBusTypeName(c.getString( c.getColumnIndex("BusTypeName") ));
                busTimeTableItemBean.setDestinationStation(c.getString( c.getColumnIndex("DestinationStation") ));
                busTimeTableItemBean.setDestinationStationMasterId(c.getString( c.getColumnIndex("DestinationStationMasterId") ));
                busTimeTableItemBean.setDivisionMasterId(c.getString( c.getColumnIndex("DivisionMasterId") ));
                busTimeTableItemBean.setDivisionName(c.getString( c.getColumnIndex("DivisionName") ));
                busTimeTableItemBean.setDutyNo(c.getString( c.getColumnIndex("DutyNo") ));
                busTimeTableItemBean.setPlatformMasterId(c.getString( c.getColumnIndex("PlatformMasterId") ));
                busTimeTableItemBean.setST(c.getString( c.getColumnIndex("ST") ));
                busTimeTableItemBean.setStartingStation(c.getString( c.getColumnIndex("StartingStation") ));
                busTimeTableItemBean.setStartingStationMasterId(c.getString( c.getColumnIndex("StartingStationMasterId") ));
                busTimeTableItemBean.setTimeTableId(c.getString( c.getColumnIndex("TimeTableId") ));
                busTimeTableItemBean.setTV(c.getString( c.getColumnIndex("TV") ));
                busTimeTableItemBean.setViaStation(c.getString( c.getColumnIndex("ViaStation") ));
                busTimeTableItemBean.setViaStationMasterId(c.getString( c.getColumnIndex("ViaStationMasterId") ));

                busTimeTableItemBeanlist.add(busTimeTableItemBean);
            }while(c.moveToNext());
            c.close();
            sql.close();
            db.close();
        }

        busTimeTableItemListAdapter = new BusTimeTableItemListAdapter(getApplicationContext(), busTimeTableItemBeanlist);
        BusTimeTableList.setAdapter(busTimeTableItemListAdapter);
        busTimeTableItemListAdapter.notifyDataSetChanged();
        getCurrentDateTime();
        //1900-01-01T08:30:00+05:30
    }

    private void updateTimeRecordSpinner(String currdate ,String currdateback ,String currdateforth) {
        TimeRecord.clear();
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();
        try {
            /*Cursor cursor = db.rawQuery(
                    "Select Distinct TimeTableId from BusTimeTable where " +//CAST(TV as datetime) = CAST('"+currdate+"' as datetime) and
                            " CAST(ST as datetime) > CAST((select date('now','-30 minute')) as datetime) " +
                            "and CAST(ST as datetime) < CAST((select date('now','+30 minute')) as datetime) order by CAST(TV as datetime) ASC", null);
*/
            Cursor cursor1 = db.rawQuery(
                    "Select TimeTableId from BusTimeTable where " +//CAST(TV as datetime) = CAST('"+currdate+"' as datetime) and
                            " TV between (select '"+currdate+"')" +
                            "and (select '"+currdateforth+"') " +
                            "order by TV ASC", null);
            if (cursor1.getCount() != 0) {
                cursor1.moveToFirst();

                    CurrTimeRecord = cursor1.getString(0);
               // } while (cursor1.moveToNext());
            }

            Cursor cursor = db.rawQuery(
                    "Select TimeTableId from BusTimeTable where " +//CAST(TV as datetime) = CAST('"+currdate+"' as datetime) and
                            " TV between (select '"+currdateback+"')" +
                            "and (select '"+currdateforth+"') " +
                            "order by TV ASC", null);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {
                    TimeRecord.add(cursor.getString(0));
                } while (cursor.moveToNext());

                cursor.close();
                db.close();
                db1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getItemPosition2()
    {
        String timerecordid = CurrTimeRecord;                //TimeRecord.get(0);

        for (int position=0; position<busTimeTableItemBeanlist.size(); position++) {
            if (busTimeTableItemBeanlist.get(position).getTimeTableId().equals(timerecordid))
            {   return position;}
        }
        return 0;
    }

    public int getItemPosition()
    {
        String timerecordid = //CurrTimeRecord;
         TimeRecord.get(0);

        for (int position=0; position<busTimeTableItemBeanlist.size(); position++) {
            if (busTimeTableItemBeanlist.get(position).getTimeTableId().equals(timerecordid))
            {   return position;}
        }
        return 0;
    }

    private void getCurrentDateTime() {
        String currdate, currdateback, currdateforth;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        //DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss+00:00");
        try {
        Calendar cal = Calendar.getInstance();
        currdate = dateFormat.format(cal.getTime());
            Date date = dateFormat.parse(currdate);
            currdate = dateFormat2.format(date);
        cal.add(cal.MINUTE, -15);
        currdateback = dateFormat.format(cal.getTime());
            date = dateFormat.parse(currdateback);
            currdateback = dateFormat2.format(date);
        cal.add(cal.MINUTE,30);
        currdateforth = dateFormat.format(cal.getTime());
            date = dateFormat.parse(currdateforth);
            currdateforth = dateFormat2.format(date);
        Log.e("currdate",currdate);

        Log.e("currdateback",currdateback);
        Log.e("currdateforth",currdateforth);

        updateTimeRecordSpinner(currdate,currdateback,currdateforth);
        highlightListItem();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void highlightListItem() {
       /* busTimeTableItemListAdapter = (BusTimeTableItemListAdapter) BusTimeTableList.getAdapter();
        busTimeTableItemListAdapter.setSelectedItem(getItemPosition());*/

        //scrollView.setSelection(position);
        // in some cases, it may be necessary to re-set adapter (as in the line below)
        BusTimeTableList.setAdapter(busTimeTableItemListAdapter);
        busTimeTableItemListAdapter.notifyDataSetChanged();
        BusTimeTableList.setSelection(getItemPosition());
        busTimeTableItemListAdapter.setSelectedItem(getItemPosition2());
    }

   /* private int getPosition() {
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "Select TimeTableId from BusTimeTable where " +//CAST(TV as datetime) = CAST('"+currdate+"' as datetime) and
                        " TV between (select '"+currdate+"')" +
                        "order by TV ASC", null);

    }*/

    public class GetRouteStationTableAsync extends AsyncTask<ArrayList<String>, Void, String>{
        SpotsDialog SPdialog;

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            // TODO Auto-generated method stub
            utility ut = new utility();
            DatabaseHelper db = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            for(int z =0; z<TimeTableIdList.size();z++ ) {
                String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetRouteStationPRTC?InstallationId="
                        + Common.INSTALLATIONID
                        + "&Networkcode="
                        + ""
                        + "&TimeTable="
                        + TimeTableIdList.get(z);

                Log.e("csn status", "url : " + url);
                url = url.replaceAll(" ", "%20");
                try {
                    responsemsg = ut.httpGet(url);
                    //sql.execSQL("DROP TABLE IF EXISTS RouteStationTable");
                    sql.execSQL("CREATE TABLE IF NOT EXISTS RouteStationTable(RouteStationId TEXT, TimeTableId TEXT, StationMasterId TEXT" +
                            ",InstallationId TEXT, ETATime TEXT, stationName TEXT)");

                    Log.e("RouteStationTable", "resmsg : " + responsemsg);

                    if (responsemsg.contains("<RouteStationId>")) {
                        sop = "valid";
                        String columnName, columnValue;

                        Cursor cur = sql.rawQuery("SELECT * FROM RouteStationTable", null);
                        ContentValues values1 = new ContentValues();
                        NodeList nl1 = ut.getnode(responsemsg, "Table");
                        Log.e(" data...", " fetch data : " + nl1.getLength());
                        for (int i = 0; i < nl1.getLength(); i++) {
                            Element e = (Element) nl1.item(i);
                            for (int j = 0; j < cur.getColumnCount(); j++) {
                                columnName = cur.getColumnName(j);
                                columnValue = ut.getValue(e, columnName);
                                values1.put(columnName, columnValue);
                            }
                            sql.insert("RouteStationTable", null, values1);
                        }

                        cur.close();

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop == "valid") {
                    //updatelist2();
                } else {
                    //ut.showD(getApplicationContext(),"nodata");
                }
                SPdialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SPdialog = new SpotsDialog(parent);
            SPdialog.setCancelable(false);
            SPdialog.show();
        }
    }

    public void updatelist2() {
        // TODO Auto-generated method stub
        DatabaseHelper db = new DatabaseHelper(parent);
        SQLiteDatabase sql = db.getWritableDatabase();
        viaItemBeanlist = new ArrayList<ViaRouteStnItemBean>();
        viaItemBeanlist.clear();

        Cursor c = sql.rawQuery("Select distinct stationName from RouteStationTable " + "where TimeTableId ='"+TimeTableId
                +"' and InstallationId = '"+Common.INSTALLATIONID+"'", null);
        if (c.getCount()== 0){
            c.close();
            sql.close();
            db.close();
        }else{
            c.moveToFirst();
            int column = 0;
            do{
                viaItemBean = new ViaRouteStnItemBean();
                viaItemBean.setStationName(c.getString( c.getColumnIndex("stationName") ));
                /*viaItemBean.setStationMasterId(c.getString( c.getColumnIndex("StationMasterId") ));
                viaItemBean.setTimeTableId(c.getString( c.getColumnIndex("TimeTableId") ));
                viaItemBean.setETATime(c.getString( c.getColumnIndex("ETATime") ));
                viaItemBean.setInstallationId(c.getString( c.getColumnIndex("InstallationId") ));
                viaItemBean.setRouteStationId(c.getString( c.getColumnIndex("RouteStationId") ));
                */viaItemBeanlist.add(viaItemBean);
            }while(c.moveToNext());
            c.close();
            sql.close();
            db.close();
        }

        viaItemListAdapter = new ViaRouteStationItemListAdapter(getApplicationContext(), viaItemBeanlist);
        listview_via_list.setAdapter(viaItemListAdapter);
        viaItemListAdapter.notifyDataSetChanged();

    }

    public class DataSyncBusReportingAsync extends AsyncTask<String, Void, String>{
        SpotsDialog SPdialog;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            utility ut = new utility();
            DatabaseHelper db = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/DataSyncBusReporting?" +
                    "L_BusReportingID="+"0"+
                    "&CreationLevel="+TimeTableId+
                    "&InstallationId="+Common.INSTALLATIONID+
                    "&LateByMasterId="+"0"+
                    "&BusSchedulerId="+TimeTableId+
                    "&BusNo="+pbusno+
                    "&DutyNo="+pdutyno+
                    "&StartingStationMasterId="+StartingStationMasterId+
                    "&DestinationStationMasterId="+DestinationStationMasterId+
                    "&ViaStationMasterId="+ViaStationMasterId+
                    "&PlatformMasterId="+ppltfno+
                    "&RasonMasterId="+"0"+
                    "&BusTypeMasterId="+BusTypeMasterId+
                    "&DriverBatchCode="+pdriveno+
                    "&ConductorBatchCode="+pconductorno+
                    "&DivisionMasterId="+DivisionMasterId+
                    "&WaveFileName="+""+
                    "&WaveFileFormat="+""+
                    "&Status="+"D"+
                    "&BusInTime="+inTime(psettime)+
                    "&BusOutTime="+psettime+
                    "&ReportingTime="+dateTime(psettime)+
                    "&UniqueId="+Common.YARDID;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                Log.e("DataSync", "resmsg : " + responsemsg);
                String[] parts = responsemsg.split(">");
                parts = parts[2].split("<");
                responsemsg = parts[0];
                Log.e("DataSync", "resmsgsplit : " + responsemsg);

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result.equals("Y")) {
                    DatabaseHelper db = new DatabaseHelper(getBaseContext());
                    SQLiteDatabase sql = db.getWritableDatabase();
                    db.updateBusTimeTable(TimeTableId);

                    Toast.makeText(parent,"Data Updated Successfully",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(parent,"Error : "+ result,Toast.LENGTH_LONG).show();
                }
                updatelist();
                SPdialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SPdialog = new SpotsDialog(parent);
            SPdialog.setCancelable(true);
            SPdialog.show();
        }
    }

    public class DataSyncBusReportingAsyncExtra extends AsyncTask<String, Void, String>{
        SpotsDialog SPdialog;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            utility ut = new utility();
            DatabaseHelper db = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/DataSyncBusReporting?" +
                    "L_BusReportingID="+"0"+
                    "&CreationLevel="+"0"+
                    "&InstallationId="+Common.INSTALLATIONID+
                    "&LateByMasterId="+"0"+
                    "&BusSchedulerId="+"0"+
                    "&BusNo="+ebusno+
                    "&DutyNo="+edutyno+
                    "&StartingStationMasterId="+getFromId(efrom)+
                    "&DestinationStationMasterId="+getToId(eto)+
                    "&ViaStationMasterId="+getViaId(evia)+
                    "&PlatformMasterId="+epltfno+
                    "&RasonMasterId="+"0"+
                    "&BusTypeMasterId="+"0"+
                    "&DriverBatchCode="+edriveno+
                    "&ConductorBatchCode="+econductorno+
                    "&DivisionMasterId="+"0"+
                    "&WaveFileName="+""+
                    "&WaveFileFormat="+""+
                    "&Status="+"E"+
                    "&BusInTime="+inTime(esettime)+
                    "&BusOutTime="+esettime+
                    "&ReportingTime="+dateTime(esettime)+
                    "&UniqueId="+Common.YARDID;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                Log.e("DataSync", "resmsg : " + responsemsg);
                String[] parts = responsemsg.split(">");
                parts = parts[2].split("<");
                responsemsg = parts[0];
                Log.e("DataSync", "resmsgsplit : " + responsemsg);

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result.equals("Y")) {
                    /*DatabaseHelper db = new DatabaseHelper(getBaseContext());
                    SQLiteDatabase sql = db.getWritableDatabase();
                    db.updateBusTimeTable(TimeTableId);*/

                    Toast.makeText(parent,"Data Updated Successfully",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(parent,"Error : "+ result,Toast.LENGTH_LONG).show();
                }
                updatelist();
                SPdialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SPdialog = new SpotsDialog(parent);
            SPdialog.setCancelable(true);
            SPdialog.show();
        }
    }

    private String getViaId(String evia) {
            String id;
            DatabaseHelper db1 = new DatabaseHelper(parent);
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select ViaStationMasterId from BusTimeTable where ViaStation = '"
                    +evia+"'", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getString(0);

                cursor.close();
                sql.close();
                db1.close();
                return id;

            } else {

                cursor.close();
                sql.close();
                db1.close();
                return null;
            }
    }

    private String getToId(String eto) {
        String id;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase sql = db1.getWritableDatabase();
        Cursor cursor = sql.rawQuery("Select DestinationStationMasterId from BusTimeTable where DestinationStation = '"
                +eto+"'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getString(0);

            cursor.close();
            sql.close();
            db1.close();
            return id;

        } else {

            cursor.close();
            sql.close();
            db1.close();
            return null;
        }
    }

    private String getFromId(String efrom) {
        String id;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase sql = db1.getWritableDatabase();
        Cursor cursor = sql.rawQuery("Select StartingStationMasterId from BusTimeTable where StartingStation = '"
                +efrom+"'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getString(0);

            cursor.close();
            sql.close();
            db1.close();
            return id;

        } else {

            cursor.close();
            sql.close();
            db1.close();
            return null;
        }
    }


    private String inTime(String psettime) {
        String intime = psettime;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm");
        try {
            Date date = dateFormat2.parse(psettime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, -5);

            intime = dateFormat2.format(calendar.getTime());
            Log.e("Time", intime);
        } catch (ParseException e) {
        }
        return intime;
    }

    private String dateTime(String psettime) {
        String intime = psettime;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm");
        try {
            Date date2 = dateFormat2.parse(psettime);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date2);
            Calendar c = Calendar.getInstance();
            c.setTime(date);/*
            c.set(Calendar.YEAR,c.get(Calendar.YEAR));
            c.set(Calendar.MONTH,c.get(Calendar.MONTH));
            c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
            c.set(Calendar.SECOND,c.get(Calendar.SECOND));*/
            c.set(Calendar.HOUR,calendar.get(Calendar.HOUR));
            c.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
            intime = dateFormat1.format(c.getTime());
            Log.e("dateTime", intime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return intime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.url_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (NetworkUtils.isNetworkAvailable(parent)) {
                    async = null;
                    async = new GetBusTimeTableAsync();
                    async.execute();
                }
                return true;
            /*case R.id.busCancel:
                    Toast.makeText(parent, "Work In Progress", Toast.LENGTH_LONG).show();
                return true;*/
            case R.id.extraBus:
                //Toast.makeText(parent, "Work In Progress", Toast.LENGTH_LONG).show();
                updateFromSpinner();
                updateToSpinner();
                updateViaSpinner();
                setPromptforExtraBus();
                return true;
            /*case R.id.selectInstallationId:
                Intent i = new Intent(parent,SelectStationActivity.class);
                startActivity(i);
                finish();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setPromptforExtraBus() {


        LayoutInflater li = LayoutInflater.from(parent);
        View promptsView = li.inflate(R.layout.prompt_extrabus, null);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText editTextextraDutyno = (EditText) promptsView.findViewById(R.id.editTextextraDutyno);
        final EditText editTextextraBusno = (EditText) promptsView.findViewById(R.id.editTextextraBusno);
        final EditText editTextextraPlatform = (EditText) promptsView.findViewById(R.id.editTextextraPlatform);
        final EditText editTextextraTime = (EditText) promptsView.findViewById(R.id.editTextextraTime);
        final EditText editTextextraDriver = (EditText) promptsView.findViewById(R.id.editTextextraDriver);
        final EditText editTextextraConductor = (EditText) promptsView.findViewById(R.id.editTextextraConductor);

        final AutoCompleteTextView editTextextraFrom = (AutoCompleteTextView) promptsView.findViewById(R.id.editTextextraFrom);
        final AutoCompleteTextView editTextextraTo = (AutoCompleteTextView) promptsView.findViewById(R.id.editTextextraTo);
        final AutoCompleteTextView editTextextraVia = (AutoCompleteTextView) promptsView.findViewById(R.id.editTextextraVia);

        editTextextraTime.setText(currtime());
        editTextextraTime.setKeyListener(null);
        editTextextraTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(parent, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTextextraTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(parent,
                android.R.layout.select_dialog_item, eFromList);
        //adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editTextextraFrom.setThreshold(1);
        editTextextraFrom.setAdapter(adapterFrom);
        editTextextraFrom.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                editTextextraFrom.showDropDown();
                return false;
            }
        });

        ArrayAdapter<String> adapterTo = new ArrayAdapter<String>(parent,
                android.R.layout.select_dialog_item, eToList);
        //adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editTextextraTo.setThreshold(1);
        editTextextraTo.setAdapter(adapterTo);
        editTextextraTo.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                editTextextraTo.showDropDown();
                return false;
            }
        });

        ArrayAdapter<String> adapterVia = new ArrayAdapter<String>(parent,
                android.R.layout.select_dialog_item, eViaList);
        //adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editTextextraVia.setThreshold(1);
        editTextextraVia.setAdapter(adapterVia);
        editTextextraVia.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                editTextextraVia.showDropDown();
                return false;
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Toast.makeText(parent, "Work In Progress", Toast.LENGTH_LONG).show();

                                // get user input and set it to result
                                edutyno = editTextextraDutyno.getText().toString();
                                ebusno = editTextextraBusno.getText().toString();
                                epltfno = editTextextraPlatform.getText().toString();
                                esettime = editTextextraTime.getText().toString();
                                efrom = editTextextraFrom.getText().toString();
                                eto = editTextextraTo.getText().toString();
                                evia = editTextextraVia.getText().toString();
                                edriveno = editTextextraDriver.getText().toString();
                                econductorno = editTextextraConductor.getText().toString();
                                if (epltfno == null || editTextextraPlatform.getText().toString().equalsIgnoreCase("")) {
                                    editTextextraPlatform.setError("Please Enter Platform");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                } else if (ebusno == null || editTextextraBusno.getText().toString().equalsIgnoreCase("")) {
                                    editTextextraBusno.setError("Please Enter Bus");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                } else if (esettime == null || editTextextraTime.getText().toString().equalsIgnoreCase("")) {
                                    editTextextraTime.setError("Please Enter Time");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                }else if (efrom == null || editTextextraFrom.getText().toString().equalsIgnoreCase("")) {
                                    editTextextraFrom.setError("Please Select Source");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                }else if (eto == null || editTextextraTo.getText().toString().equalsIgnoreCase("")) {
                                    editTextextraTo.setError("Please Select Destination");
                                    //Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if (NetworkUtils.isNetworkAvailable(parent)) {
                                        asyncTaskextra = null;
                                        asyncTaskextra = new DataSyncBusReportingAsyncExtra();
                                        asyncTaskextra.execute();
                                    }
                                    //Toast.makeText(parent, "Data Updated Successfully", Toast.LENGTH_LONG).show();
                                }
                                // updatelist();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                updatelist();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private String currtime() {
        String currTime = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        //DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss+00:00");
        try {
            Calendar cal = Calendar.getInstance();
            currTime = dateFormat.format(cal.getTime());
            Date date = dateFormat.parse(currTime);
            currTime = dateFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currTime;
    }

    private void updateFromSpinner() {
        eFromList.clear();
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "Select Distinct StartingStation from BusTimeTable order by StartingStation ASC", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                eFromList.add(cursor.getString(0));
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            db1.close();
        }

    }

    private void updateToSpinner() {
        eToList.clear();
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "Select Distinct DestinationStation from BusTimeTable order by DestinationStation ASC", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                eToList.add(cursor.getString(0));
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            db1.close();
        }
    }

    private void updateViaSpinner() {
        eViaList.clear();
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "Select Distinct ViaStation from BusTimeTable order by ViaStation ASC", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                eViaList.add(cursor.getString(0));
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            db1.close();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (dbvalue()){
                            Log.e("Refresh","Refresh");
                            updatelist();
                        }else {//
                            if (NetworkUtils.isNetworkAvailable(parent)) {
                                async = null;
                                async = new GetBusTimeTableAsync();
                                async.execute();
                            }
                        }
                    }
                });
            }
        }, 0, 40000);//}, 0, 60000*5); // updates each 40 secs
    }


}
