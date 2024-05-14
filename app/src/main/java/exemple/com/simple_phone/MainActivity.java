package exemple.com.simple_phone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    ContactDAO contactDAO;

    List<Contact> list = new ArrayList<>();

    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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