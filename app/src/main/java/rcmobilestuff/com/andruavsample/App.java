package rcmobilestuff.com.andruavsample;

import android.app.Application;


import com.andruav.protocol.AndruavSettings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import rcmobilestuff.com.andruavsample.Andruav.AndruavWSClient;

/**
 * Created by M.Hefny on 15-Feb-15.
 */
public class App   extends Application {

    public static AndruavWSClient andruavWSClient;


    @Override
    public void onCreate() {
        super.onCreate();

        AndruavSettings.Account_SID = "61";
        AndruavSettings.UnitID= "AndruavTest";
        AndruavSettings.Description = "AndruavTest";
        AndruavSettings.GroupName = "main group";
        AndruavSettings.isCGS = true;
    }

    public static void startAndruavWS (String AccessCode)
    {

        if (App.andruavWSClient == null)
        {
            String param = null;
            try {
                param = URLEncoder.encode("KsEY=123as&SID=" + AccessCode, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String websocketURL = "ws://andruav.com:9510?" + param;
            App.andruavWSClient = new AndruavWSClient(websocketURL,null);
        }
        else {


            if (App.andruavWSClient.isConnected())
                App.andruavWSClient.disconnect();


        }
        App.andruavWSClient.connect();
    }

    public static void stopAndruavWS ()
    {

        if ((App.andruavWSClient!= null) && (App.andruavWSClient.isConnected()))
        {
            App.andruavWSClient.disconnect();
        }

    }
}