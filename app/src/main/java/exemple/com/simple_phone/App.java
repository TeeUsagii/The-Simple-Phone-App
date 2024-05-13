package exemple.com.simple_phone;

import android.app.Application;
import androidx.appcompat.app.AppCompatActivity;


import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Log
        Timber.plant(new Timber.DebugTree());
    }
}
