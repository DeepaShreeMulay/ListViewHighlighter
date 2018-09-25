package com.vritti.yard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Admin-3 on 6/7/2017.
 */

public class utility {

    public static DefaultHttpClient httpClient = new DefaultHttpClient();

    public static String httpGet(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Check for successful response code or throw error
        // if (conn.getResponseCode() != 200) {
        // throw new IOException(conn.getResponseMessage());
        // }

        // Buffer the result into a string
        BufferedReader buffrd = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = buffrd.readLine()) != null) {
            sb.append(line);
        }

        buffrd.close();
        conn.disconnect();
        return sb.toString();
    }

    public static Object OpenPostConnection(String url, String FinalObj) {
        String res = null;
        Object response = null;
        try {
            URL url1 = new URL(url);
            HttpPost httppost = new HttpPost(url.toString());
            StringEntity se = new StringEntity(FinalObj.toString());
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            ResponseHandler responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httppost, responseHandler);
            Log.i("Common Data", response + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String OpenConnection(String url) {
        String res = null;
        try {
            URL url1 = new URL(url);
            HttpGet httppost;
            httppost= new HttpGet(url);
            HttpResponse response ;
            response= httpClient.execute(httppost);

            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public NodeList getnode(String xml, String Tag) {

        //Log.e("get node", " xml :" + xml + " tag: " + Tag);

        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
            //Log.e("get node", " doc: " + doc);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        NodeList nl = doc.getElementsByTagName(Tag);
        //Log.e("get node", " nl: " + nl);
        //Log.e("get node", " nl len: " + nl.getLength());
        return nl;
    }

    public String getValue(Element e, String str) {
        NodeList n = e.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public String getBusTimeTable() {
        String BusTimeTable = "CREATE TABLE IF NOT EXISTS BusTimeTable(TimeTableId TEXT, ST TEXT, TV TEXT, StartingStationMasterId TEXT" +
                ", DestinationStationMasterId TEXT, ViaStationMasterId TEXT, DivisionMasterId TEXT" +
                ", PlatformMasterId TEXT, BusTypeMasterId TEXT, StartingStation TEXT, DestinationStation TEXT" +
                ", BusTypeName TEXT, ViaStation TEXT, DivisionName TEXT, DutyNo TEXT, DeleteFlag TEXT, DeleteDate TEXT)";
        return BusTimeTable;

    }

    public static String getInstallationId(Context parent) {
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase sql = db1.getWritableDatabase();
        Cursor cursor = sql.rawQuery("Select * from SelectInstallationId", null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Common.INSTALLATIONID = cursor.getString(0);
            Common.STATIONNAME = cursor.getString(1);
            Common.NETWORKCODE = cursor.getString(2);
            Common.YARDID = cursor.getString(3);

            cursor.close();
            sql.close();
            db1.close();
            return Common.INSTALLATIONID;

        } else {

            cursor.close();
            sql.close();
            db1.close();
            return null;
        }
    }

}
