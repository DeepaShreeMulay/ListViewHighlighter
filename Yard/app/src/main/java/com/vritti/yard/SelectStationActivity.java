package com.vritti.yard;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class SelectStationActivity extends Activity {
	private Context parent;
	private AutoCompleteTextView edNC, edSN;
	private EditText edYardId;
	private Button btnSave, btnUpdate;
	String responsemsg = "m", sop;
	private DatabaseHelper databaseHelper;
	private static GetInstallationMasterAsync async;

	private ArrayList<String> NetworkList, StnList;
	private String InstallationId,StationName, NetworkCode,YardId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stationselection);
		initViews();
		if (dbvalue()){
			updateNetworkSpinner();
			ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(parent,
					android.R.layout.select_dialog_item, NetworkList);
			edNC.setThreshold(1);
			edNC.setAdapter(adapter1);

		}else{//
			if (NetworkUtils.isNetworkAvailable(parent)) {
				async = null;
				async = new GetInstallationMasterAsync();
				async.execute();
			}
		}

		setListeners();
	}

	private void updateNetworkSpinner() {
		NetworkList.clear();
		DatabaseHelper db1 = new DatabaseHelper(parent);
		SQLiteDatabase db = db1.getWritableDatabase();

		Cursor cursor = db.rawQuery(
				"Select Distinct NetworkCode from InstallationMaster order by NetworkCode ASC", null);

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			do {
				NetworkList.add(cursor.getString(0));
			} while (cursor.moveToNext());

			cursor.close();
			db.close();
			db1.close();
		}

	}
	private void updateStnSpinner() {
		StnList.clear();
		DatabaseHelper db1 = new DatabaseHelper(parent);
		SQLiteDatabase db = db1.getWritableDatabase();
		String netcode = edNC.getText().toString();

		Cursor cursor = db.rawQuery(
				"Select Distinct InstallationDesc from InstallationMaster where NetworkCode='"
						+netcode+"' order by InstallationDesc ASC", null);

		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			do {
				StnList.add(cursor.getString(0));
			} while (cursor.moveToNext());

			cursor.close();
			db.close();
			db1.close();
		}

	}

	private boolean dbvalue(){
		try{
			DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
			SQLiteDatabase sql = db1.getWritableDatabase();
			Cursor cursor = sql.rawQuery("Select * from InstallationMaster", null);
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

	private void checkDBforStation() {
		// TODO Auto-generated method stub
		Common.INSTALLATIONID = utility.getInstallationId(parent);
		
		if (Common.INSTALLATIONID != null) {
			startActivity(new Intent(parent, MainActivity.class));
			finish();
		}
	}

	private void initViews() {
		parent = SelectStationActivity.this;

		checkDBforStation();

		databaseHelper = new DatabaseHelper(parent);
		edNC = (AutoCompleteTextView) findViewById(R.id.edNC);
		edNC.setKeyListener(null);
		edSN = (AutoCompleteTextView) findViewById(R.id.edSN);
		edSN.setKeyListener(null);
		edYardId = (EditText) findViewById(R.id.edYardId);
		btnSave = (Button) findViewById(R.id.btnSave);
		//btnUpdate = (Button) findViewById(R.id.btnUpdate);
		NetworkList = new ArrayList<String>();
		StnList = new ArrayList<String>();
	}

	private void setListeners() {
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NetworkCode = edNC.getText().toString();
				StationName = edSN.getText().toString();
				YardId = edYardId.getText().toString();
				InstallationId = get_InstallationId(StationName,NetworkCode);

				saveIdToDb();
				checkDBforStation();

			}
		});

		edNC.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {   }

			@Override
			public void afterTextChanged(Editable s) {
				updateStnSpinner();
				ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(parent,
						android.R.layout.select_dialog_item, StnList);
				edSN.setThreshold(1);
				edSN.setAdapter(adapter2);
			}
		});

		edNC.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				edNC.showDropDown();
				return false;
			}
		});

		edSN.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				edSN.showDropDown();
				return false;
			}
		});


	}

	private String get_InstallationId(String stationName, String networkCode) {
		String id;
		DatabaseHelper db1 = new DatabaseHelper(parent);
		SQLiteDatabase sql = db1.getWritableDatabase();
		Cursor cursor = sql.rawQuery("Select InstalationId from InstallationMaster where NetworkCode = '"
				+networkCode+"' and InstallationDesc = '"+stationName+"'", null);
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

	protected void saveIdToDb() {
		databaseHelper.AddSelectInstallationId(InstallationId, StationName, NetworkCode,YardId);
	}


	public class GetInstallationMasterAsync extends AsyncTask<String, Void, String> {
		SpotsDialog SPdialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			utility ut = new utility();
			DatabaseHelper db = new DatabaseHelper(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";

			Log.e("Insta", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				sql.execSQL("DROP TABLE IF EXISTS InstallationMaster");
				sql.execSQL("CREATE TABLE InstallationMaster(InstalationId TEXT, InstalationName TEXT, InstallationDesc TEXT" +
						", Address TEXT, NetworkCode TEXT, LastbusReporting TEXT, LastAdvDate TEXT" +
						", ServerTime TEXT, SubNetworkCode TEXT)");

				Log.e("Installation", "resmsg : " + responsemsg);

				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					String columnName, columnValue;

					Cursor cur = sql.rawQuery("SELECT * FROM InstallationMaster", null);
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
						sql.insert("InstallationMaster",null, values1);
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
					updateNetworkSpinner();
					ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(parent,
							android.R.layout.select_dialog_item, NetworkList);
					edNC.setThreshold(1);
					edNC.setAdapter(adapter1);
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



}