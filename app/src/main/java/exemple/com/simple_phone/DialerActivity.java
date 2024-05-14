package exemple.com.simple_phone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;


import static android.Manifest.permission.CALL_PHONE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;


public class DialerActivity extends AppCompatActivity {
    EditText phoneNumberInput;
    private static final int REQUEST_PERMISSION = 0;

    Button callButton; // Khai báo nút gọi

    private void appendNumber(String number) {
        String currentText = phoneNumberInput.getText().toString();
        phoneNumberInput.setText(currentText + number);
    }

    private void deleteLastNumber() {
        String currentText = phoneNumberInput.getText().toString();
        if (!currentText.isEmpty()) {
            String newText = currentText.substring(0, currentText.length() - 1);
            phoneNumberInput.setText(newText);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        callButton = findViewById(R.id.callButton);

        int[] buttonIds = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appendNumber(button.getText().toString());
                }
            });
        }

        Button delButton = findViewById(R.id.DelButton);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastNumber();
            }
        });

        callButton.setOnClickListener(v -> makeCall());

        if (getIntent().getData() != null)
            phoneNumberInput.setText(getIntent().getData().getSchemeSpecificPart());

        offerReplacingDefaultDialer();

        phoneNumberInput.setOnEditorActionListener((v, actionId, event) -> {
            makeCall();
            return true;
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        offerReplacingDefaultDialer();

        phoneNumberInput.setOnEditorActionListener((v, actionId, event) -> {
            makeCall();
            return true;
        });
    }


    @SuppressLint("MissingPermission")
    private void makeCall() {
        // If permission to call is granted
        if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
            // Create the Uri from phoneNumberInput
            Uri uri = Uri.parse("tel:"+phoneNumberInput.getText());
            // Start call to the number in input
            startActivity(new Intent(Intent.ACTION_CALL, uri));
        } else {
            // Request permission to call
            ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<Integer> grantRes = new ArrayList<>();
        // Add every result to the array
        for (Integer grantResult: grantResults) grantRes.add(grantResult);

        if (requestCode == REQUEST_PERMISSION && grantRes.contains(PERMISSION_GRANTED)) {
            makeCall();
        }
    }

    private void offerReplacingDefaultDialer() {
        TelecomManager systemService = this.getSystemService(TelecomManager.class);
        if (systemService != null && !systemService.getDefaultDialerPackage().equals(this.getPackageName())) {
        startActivity((new Intent(ACTION_CHANGE_DEFAULT_DIALER)).putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.getPackageName()));
        }
    }

    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onDialerButtonClicked(View view) {
;
    }

    @Override
    public void onBackPressed() {
        App.showExitConfirmationDialog(this);
    }

}
