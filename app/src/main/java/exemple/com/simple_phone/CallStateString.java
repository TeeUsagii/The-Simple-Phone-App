package exemple.com.simple_phone;

import android.annotation.SuppressLint;

import timber.log.Timber;

final class CallStateString {
    @SuppressLint("TimberArgCount")
    static String asString(int $receiver) {
        String state;
        switch($receiver) {
            case 0:
                state = "Gọi mới";
                break;
            case 1:
                state = "Đang gọi...";
                break;
            case 2:
                state = "Cuộc gọi đến";
                break;
            case 3:
                state = "Giữ máy";
                break;
            case 4:
                state = "Đã kết nối";
                break;
            case 5:
            case 6:
            default:
                Timber.w("Unknown state %s", $receiver, new Object[0]);
                state = "UNKNOWN";
                break;
            case 7:
                state = "Đã ngắt kết nối";
                break;
            case 8:
                state = "SELECT_PHONE_ACCOUNT";
                break;
            case 9:
                state = "Đang kết nối...";
                break;
            case 10:
                state = "Đang ngắt kết nối";
        }

        return state;
    }
}
