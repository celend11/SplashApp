package splashapp.android.nttd.cas.com.new_splash_app.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemUtil {
    public static String getDeviceModel(String command, String command1) {
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
            e.printStackTrace();
        }
        return result;
    }
}