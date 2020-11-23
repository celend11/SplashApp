package splashapp.android.nttd.cas.com.new_splash_app.Util;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class JsonFileUtil {

    //    private static final String POSFILENAME = "PosConnection.json";
//    private static final String OPERATIONFILENAME = "Operation.json";
//    private static final String PASSWORDPATH = "Setting_Password.json";
//    private static final String FirmwarePATH = "Setting_Firmware.json";
//    private static final String POSFILENAME = "/CastlesFile/PosConnection_user.json";
//    private static final String OPERATIONFILENAME = "/CastlesFile/Operation_user.json";
//    private static final String PASSWORDPATH = "/CastlesFile/Setting_Password_user.json";
//    private static final String FirmwarePATH = "/CastlesFile/Setting_Firmware_user.json";
    private static final String POSFILENAME = "/CastlesFile/PosConnection.json";
    private static final String OPERATIONFILENAME = "/CastlesFile/Operation.json";
    private static final String PASSWORDPATH = "/CastlesFile/Setting_Password.json";
    private static final String FirmwarePATH = "/CastlesFile/Setting_Firmware.json";
    private static final String NetworkSelect = "/CastlesFile/Setting_NetworkSelect.json";
    private static final String SyncTime = "/CastlesFile/SyncTime";
    private static final String PinPadControl = "/CastlesFile/PinPadControl.json";
    private static final String PinpadSound = "CastlesFile/Setting_PinpadSound.json";

    public void checkJsonFile() {
        String posStr = readFromFile(POSFILENAME);
        if (posStr.isEmpty()) {
            createPosJson();
        }

        String passwordStr = readFromFile(PASSWORDPATH);
        if (passwordStr.isEmpty()) {
            createPasswordJson();
        }

        String operationStr = readFromFile(OPERATIONFILENAME);
        if (operationStr.isEmpty()) {
            createOperationJson();
        }

        String firmwareStr = readFromFile(FirmwarePATH);
        if (firmwareStr.isEmpty()) {
            createFirmwareJson();
        }

        String networkSelectStr = readFromFile(NetworkSelect);
        if (networkSelectStr.isEmpty()) {
            createNetworkSelectJson();
        }

        String pinpadSoundStr = readFromFile(PinpadSound);
        if (pinpadSoundStr.isEmpty()) {
            createPinpadSoundJson();
        }
    }

    public String readFromFile(String fileName) {

//        final File path =
//                Environment.getExternalStoragePublicDirectory
//                        (
//                                //Environment.DIRECTORY_PICTURES
//                                Environment.getDataDirectory() + "/CastlesFile/"
//                        );
//        final File path = Environment.getExternalStorageDirectory();
        final File path = Environment.getDataDirectory();


        // Make sure the path directory exists.
//        if(!path.exists())
//        {
//            // Make it, if it doesn't exit
//            path.mkdirs();
//        }

        final File file = new File(path, fileName);

        String ret = "";

        try {
            file.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(file);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            ret = myOutWriter.append("").toString();
//
//            myOutWriter.close();
//
//            fOut.flush();
//            fOut.close();

            String receiveString = "";
            StringBuffer buffer = new StringBuffer();

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            if (fis != null) {
                while ((receiveString = reader.readLine()) != null) {
                    buffer.append(receiveString);
                }
            }
            fis.close();
            ret = buffer.toString();

//            InputStream inputStream = IndexMainActivity.getAppContext().openFileInput(fileName);
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
        } catch (FileNotFoundException e) {
            android.util.Log.d("MyApp", "File not found: " + e.toString());
        } catch (IOException e) {
            android.util.Log.d("MyApp", "Can not read file: " + e.toString());
        }

        android.util.Log.d("MyApp", "File String: " + ret);
        return ret;
    }

    public void writeToFile(String data, String fileName) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(IndexMainActivity.getAppContext().openFileOutput(fileName, Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            android.util.Log.d("MyApp", "File write failed: " + e.toString());
//        }

//        final File path =
//                Environment.getExternalStoragePublicDirectory
//                        (
//                                //Environment.DIRECTORY_PICTURES
//                                Environment.getDataDirectory() + "/CastlesFile/"
//                        );
//        final File path = Environment.getExternalStorageDirectory();
        final File path = Environment.getDataDirectory();

        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, fileName);

        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            if (fileName.equals(NetworkSelect)) {
                file.setWritable(true, true);
                file.setReadable(true, false);
            } else if (fileName.equals(PinpadSound)) {
                file.setWritable(true, false);
                file.setReadable(true, false);
            }
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            android.util.Log.d("MyApp", "File write failed: " + e.toString());
        }
    }

    // POS
    public void createPosJson() {
        JSONArray jArray = new JSONArray();
//        JSONObject jsonObj = new JSONObject();
        try {
            JSONObject jsonObj1 = new JSONObject();
            jsonObj1.put("PosConnection", true);
            jArray.put(jsonObj1);

            JSONObject jsonObj2 = new JSONObject();
            jsonObj2.put("Interface", "UART");
            jArray.put(jsonObj2);

            JSONObject jsonObj3 = new JSONObject();
            jsonObj3.put("BlockSize", 256);
            jArray.put(jsonObj3);

            JSONObject jsonObj4 = new JSONObject();
            JSONArray jArray1 = new JSONArray();
            JSONObject jsonObj41 = new JSONObject();
            jsonObj41.put("bps", 9600);
            jArray1.put(jsonObj41);
            JSONObject jsonObj42 = new JSONObject();
            jsonObj42.put("ParityCheck", "None");
            jArray1.put(jsonObj42);
            jsonObj4.put("Serial", jArray1);
            jArray.put(jsonObj4);

        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }

        writeToFile(jArray.toString(), POSFILENAME);
    }

    public JSONArray getPosJson() {
        String data = readFromFile(POSFILENAME);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jsonArray;
    }

    public void savePosJson(String data) {
        writeToFile(data, POSFILENAME);
    }

    // password
    public void createPasswordJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("password", "70352f41061eda4ff3c322094af068ba70c3b38b");
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }

        writeToFile(jsonObj.toString(), PASSWORDPATH);
    }

    public JSONObject getPasswordJson() {
        String data = readFromFile(PASSWORDPATH);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jobj;
    }

    public void savePasswordJson(String data) {
        writeToFile(data, PASSWORDPATH);
    }

    // Operation
    public void createOperationJson() {
        JSONArray jArray = new JSONArray();
//        JSONObject jsonObj = new JSONObject();
        try {
            JSONObject jsonObj0 = new JSONObject();
            jsonObj0.put("DefaultApp", "packname");
            jArray.put(jsonObj0);

            JSONObject jsonObj1 = new JSONObject();
            jsonObj1.put("RebootTime", 3);
            jArray.put(jsonObj1);

            JSONObject jsonObj2 = new JSONObject();
            jsonObj2.put("RefreshMethod", "reboot");
            jArray.put(jsonObj2);

            JSONObject jsonObj3 = new JSONObject();
            jsonObj3.put("AutoRunAppication", "無");
            jArray.put(jsonObj3);


            JSONObject jsonObj4 = new JSONObject();
            JSONArray jArray1 = new JSONArray();

            JSONObject jsonObj41 = new JSONObject();
            JSONArray jArray2 = new JSONArray();

            JSONObject jsonObj411 = new JSONObject();
            jsonObj411.put("protocal", "NTP");
            jArray2.put(jsonObj411);
            JSONObject jsonObj412 = new JSONObject();
            jsonObj412.put("url", "ntp.nict.jp");
            jArray2.put(jsonObj412);
            JSONObject jsonObj413 = new JSONObject();
            jsonObj413.put("AutoUpdateTime", true);
            jArray2.put(jsonObj413);
            JSONObject jsonObj414 = new JSONObject();
            jsonObj414.put("SyncInterval", 5);
            jArray2.put(jsonObj414);

            jsonObj41.put("TimeCorrection", jArray2);
            jArray1.put(jsonObj41);

            JSONObject jsonObj42 = new JSONObject();
            jsonObj42.put("FWAutoUpdate", true);
            jArray1.put(jsonObj42);

            jsonObj4.put("Other", jArray1);
            jArray.put(jsonObj4);

        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }

        writeToFile(jArray.toString(), OPERATIONFILENAME);
    }

    public JSONArray getOperationJson() {
        String data = readFromFile(OPERATIONFILENAME);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jsonArray;
    }

    public void saveOperationJson(String data) {
        writeToFile(data, OPERATIONFILENAME);
    }

    // firmware
    public void createFirmwareJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("firmware", "有");

            jsonObj.put("NTPURL", "ntp.nict.jp");
            jsonObj.put("HTTPURL", "http://ntp-a1.nict.go.jp/cgi-bin/ntp");
            jsonObj.put("SAVETIME", "1970/01/01 00:00:00");
            jsonObj.put("SAVETIMEnew", "1970/01/01 00:00:00");
            jsonObj.put("DefaultApp", "");

        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }

        writeToFile(jsonObj.toString(), FirmwarePATH);
    }

    public JSONObject getFirmwareJson() {
        String data = readFromFile(FirmwarePATH);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jobj;
    }

    public void saveFirmwareJson(String data) {
        writeToFile(data, FirmwarePATH);
    }

    // network select
    public void createNetworkSelectJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("networkSelect", "WiFi");
            jsonObj.put("bottomBase", "なし");
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }

        writeToFile(jsonObj.toString(), NetworkSelect);
    }

    public JSONObject getNetworkSelectJson() {
        String data = readFromFile(NetworkSelect);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jobj;
    }

    public void saveNetworkSelectJson(String data) {
        writeToFile(data, NetworkSelect);
    }

    // get SyncTime
    public String getSyncTimeJson() {
        String data = readFromFile(SyncTime);

        return data;
    }

    // get PinPadControl
    public JSONObject getPinPadControlJson() {
        String data = readFromFile(PinPadControl);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jobj;
    }

    // network select
    public void createPinpadSoundJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("PinpadSound", true);
            jsonObj.put("SoundVolume", 100);
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }

        writeToFile(jsonObj.toString(), PinpadSound);
    }

    public JSONObject getPinpadSoundJson() {
        String data = readFromFile(PinpadSound);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jobj;
    }

    public void savePinpadSoundJson(String data) {
        writeToFile(data, PinpadSound);
    }
}