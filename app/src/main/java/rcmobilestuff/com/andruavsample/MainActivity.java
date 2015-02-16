package rcmobilestuff.com.andruavsample;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.andruav.protocol.AndruavSettings;
import com.andruav.protocol.commands.TextMessages.AndruavCMD;
import com.andruav.protocol.commands.TextMessages.AndruavMessage_RemoteExecute;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    MainActivity Me;

    Handler mUIHandle;
    Button btnConnect;
    Button btnBeep;
    EditText txtAccessCode;
    EditText txtUnitID;
    EditText txtLog;


    /***
     * Called by Event Bus
     * @param andruavCMD
     */
    public void onEvent (AndruavCMD andruavCMD)
    {
        Message msg = new Message();
        msg.obj = andruavCMD;
        mUIHandle.sendMessage(msg);
    }

    public void onEvent (String message)
    {
        Message msg = new Message();
        msg.obj = message;
        mUIHandle.sendMessage(msg);
    }

    private void initGUI()
    {
        mUIHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String htmlText;
                if (AndruavCMD.class.isInstance(msg.obj))
                {
                    AndruavCMD andruavCMD = (AndruavCMD) msg.obj;
                    htmlText = "<font color=#36AB36>recieved from:" + andruavCMD.senderName   + "</font><font color=#3636AB> message type:" + andruavCMD.andruavMessageBase.getClass().toString()+"</font><br>";
                    txtLog.append(Html.fromHtml(htmlText));
                }
                else if (String.class.isInstance(msg.obj))
                {
                    htmlText = "<font color=#AB3636> " + (String)msg.obj   + "</font><br>";
                    txtLog.append(Html.fromHtml(htmlText));
                    btnBeep.setEnabled(true);
                }
            }
        };

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnBeep = (Button) findViewById(R.id.btnBeep);
        txtAccessCode = (EditText) findViewById(R.id.edtAccessCode);
        txtUnitID = (EditText) findViewById(R.id.edtUnitName);
        txtLog = (EditText) findViewById(R.id.edtLog);
        btnBeep.setEnabled(false);
        btnBeep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AndruavMessage_RemoteExecute  andruavMessage_RemoteExecute = new AndruavMessage_RemoteExecute();
                andruavMessage_RemoteExecute.RemoteCommandID =AndruavMessage_RemoteExecute.RemoteCommand_MAKEBEEP;

                App.andruavWSClient.broadcastTextMessageToGroup(andruavMessage_RemoteExecute , false);
            }
          });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtAccessCode.getText().toString().isEmpty())
                {
                    Toast.makeText(Me,"Access Code is empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if (txtUnitID.getText().toString().isEmpty())
                {
                    Toast.makeText(Me,"Unit Name is empty.", Toast.LENGTH_LONG).show();
                    return ;
                }

                AndruavSettings.Account_SID = txtAccessCode.getText().toString();
                AndruavSettings.UnitID = txtUnitID.getText().toString();
                AndruavSettings.Description = "This is a test";
                AndruavSettings.encryptionEnabled = false;
                AndruavSettings.hasTelemetry = false;
                AndruavSettings.isCGS = true;
                App.stopAndruavWS();
                App.startAndruavWS(txtAccessCode.getText().toString());
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Me = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initGUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.andruav.com"));
                startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application can handle this request."
                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Me);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        App.stopAndruavWS();
                        System.exit(2);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
