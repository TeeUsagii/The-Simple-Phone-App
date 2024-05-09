package exemple.com.simple_phone;

import android.telecom.Call;
import android.telecom.InCallService;

public class CallService extends InCallService {
    @Override
    public void onCallAdded(Call call) {
        new OngoingCall().setCall(call);
        CallActivity.start(this, call);
    }

    @Override
    public void onCallRemoved(Call call) {
        new OngoingCall().setCall(null);
    }
}
