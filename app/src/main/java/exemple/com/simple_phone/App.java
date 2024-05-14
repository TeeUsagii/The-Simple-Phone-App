package exemple.com.simple_phone;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;


import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Log
        Timber.plant(new Timber.DebugTree());
    }

    public static void showExitConfirmationDialog(final Context context) {
        AlertDialog show = new AlertDialog.Builder(context)
                .setMessage("Bạn có muốn thoát ứng dụng không?")
                .setCancelable(false)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).finish();
                        } else if (context instanceof DialerActivity) {
                            ((DialerActivity) context).finish();
                        }
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
