package rcmobilestuff.com.andruavsample.Andruav;

import android.media.AudioManager;
import android.media.ToneGenerator;

import com.andruav.protocol.andruav.AndruavWSClientBase_AutoBohn;
import com.andruav.protocol.commands.TextMessages.AndruavCMD;
import com.andruav.protocol.commands.TextMessages.AndruavMessage_ID;
import com.andruav.protocol.commands.TextMessages.AndruavMessage_RemoteExecute;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by M.Hefny on 15-Feb-15.
 */
public class AndruavWSClient extends AndruavWSClientBase_AutoBohn {

    public AndruavWSClient(String uri, List<BasicNameValuePair> extraHeaders) {
        super(uri, extraHeaders);

    }


    @Override
    protected void onTextMessage(AndruavCMD andruavCMD) {
        EventBus.getDefault().post((AndruavCMD)andruavCMD);
    }

    @Override
    protected void onClose(int code, String reason) {
        super.onClose(code, reason);

    }

    @Override
    protected void onError(int code, String reason) {
        super.onError(code, reason);

    }

    @Override
    protected void onAdded(Boolean isSuccess) {
        EventBus.getDefault().post("Unit has been added");

    }


    /**
     * called by internal loop each [monSlowOperationTicks == 10 seconds]
     * can be used to send any recurrent message.
     *
     * @param now
     */
    @Override
    protected void onScheduledTasks(long now) {

    }

    @Override
    protected void executeRemoteExecuteCMD(final AndruavCMD andruavCMD) {
        AndruavMessage_RemoteExecute andruavMessage_remoteExecute = ((AndruavMessage_RemoteExecute) (andruavCMD.andruavMessageBase));
        int CMD_ID = andruavMessage_remoteExecute.RemoteCommandID;
        switch (CMD_ID) {
            case AndruavMessage_RemoteExecute.RemoteCommand_MAKEBEEP:
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                break;
        }
    }
}