package exemple.com.simple_phone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.pm.PackageManager;
import android.telecom.TelecomManager;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 1002;
    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 1001;
    ContactDAO contactDAO;

    List<Contact> list = new ArrayList<>();

    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Danh bạ");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSION);
        } else {
            // Quyền đã được cấp, yêu cầu đặt làm ứng dụng gọi mặc định
            requestSetDefaultDialer();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestSetDefaultDialer();
        }

        RecyclerView rvContact = findViewById(R.id.rvContact);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        contactDAO = new ContactDAO(this);
        list.addAll(contactDAO.select());
        adapter = new ContactAdapter(this, list);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvContact.setLayoutManager(manager);
        rvContact.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            View view1 = LayoutInflater.from(this).inflate(R.layout.add_contact_dialog, null);
            builder1.setView(view1);
            Dialog dialog1 = builder1.create();
            dialog1.show();

            EditText edtTitle = view1.findViewById(R.id.edtTitleADD);
            EditText edtPhone = view1.findViewById(R.id.edtPhoneADD);
            Button btnCancel = view1.findViewById(R.id.btnCancelADD);
            Button btnSave = view1.findViewById(R.id.btnSaveADD);

            btnCancel.setOnClickListener(v1 -> {
                dialog1.dismiss();
            });


            btnSave.setOnClickListener(v1 -> {

                if (edtPhone.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                UUID uuid = UUID.randomUUID();
                Contact contact = new Contact(
                        uuid.toString(),
                        edtTitle.getText().toString(),
                        edtPhone.getText().toString().trim());

                if (contactDAO.insert(contact)) {
                    list.clear();
                    list.addAll(contactDAO.select());
                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();
                }
            });
        });

    }

    private void requestSetDefaultDialer() {
        RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
        if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
            if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
            } else {
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            if (resultCode == RESULT_OK) {
            } else {
                Toast.makeText(this, "Không thể đặt ứng dụng làm ứng dụng gọi mặc định", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, yêu cầu đặt làm ứng dụng gọi mặc định
                requestSetDefaultDialer();
            } else {
                Toast.makeText(this, "Quyền CALL_PHONE bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onContactsButtonClicked(View view) {

    }

    // Chuyển qua view quay số điện thoại
    public void onDialerButtonClicked(View view) {
        Intent intent = new Intent(this, DialerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

        @Override
        public void onBackPressed() {
            App.showExitConfirmationDialog(this);
        }


}