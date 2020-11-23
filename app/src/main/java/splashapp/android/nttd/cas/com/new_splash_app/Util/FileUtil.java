package splashapp.android.nttd.cas.com.new_splash_app.Util;

import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileUtil {
    private static final String LOG_TAG = "Cmds";
    private static Lock lock = new ReentrantLock();
    private static int con = 0;

    private static String read_file_retry (String path) {
        lock.lock();

        String readData = "";
        FileReader file;

        try {
            file = new FileReader(path);
            BufferedReader br = new BufferedReader(file);
            String temp = br.readLine();
            while (temp != null) {
                readData += temp;
                temp = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lock.unlock();

        return readData;
    }

    public static void write_file(String path, String data) {
        lock.lock();

        try {
            FileWriter file = new FileWriter(path, false);
            BufferedWriter br = new BufferedWriter(file);
            File filePermissions = new File(path);
            filePermissions.setExecutable(false, false);
            filePermissions.setWritable(true, false);
            filePermissions.setReadable(true, false);

            br.write(data);
            br.flush();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        lock.unlock();
    }

    public static String read_file(String path) {
        lock.lock();

        String readData = "";
        try {
            FileReader file = new FileReader(path);
            BufferedReader br = new BufferedReader(file);

            String temp = br.readLine();
            while (temp != null) {
                readData += temp;
                temp = br.readLine();
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lock.unlock();

        if ("".equals(readData) && con < 5) {
            Log.d(LOG_TAG, "read file : no data");
            SystemClock.sleep(100);
            readData = read_file_retry(path);
            con++;
        }
        con = 0;

        return readData;
    }

    public static String command(String command, String command1) {
        String result = "";
        String line;
        String[] cmd = new String[]{command, command1};
        String workdirectory = "/system/bin/";
        try {
            ProcessBuilder bulider = new ProcessBuilder(cmd);
            bulider.directory(new File(workdirectory));
            bulider.redirectErrorStream(true);
            Process process = bulider.start();
            InputStream in = process.getInputStream();
            InputStreamReader isrout = new InputStreamReader(in);
            BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

            while ((line = brout.readLine()) != null) {
                result += line;
            }
            in.close();

        } catch (Exception e) {
            Log.d(LOG_TAG, "command Error : " + e);
        }
        return result;
    }
}