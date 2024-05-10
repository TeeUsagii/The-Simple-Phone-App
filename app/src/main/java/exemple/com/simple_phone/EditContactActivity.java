package exemple.com.simple_phone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditContactActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        // Ánh xạ các thành phần giao diện
        editTextName = findViewById(R.id.editTextName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        btnSave = findViewById(R.id.buttonSave); // Ánh xạ btnSave ở đây

        // Nhận dữ liệu của liên hệ từ Intent và hiển thị lên giao diện
        Contact contact = getIntent().getParcelableExtra("contact");
        if (contact != null) {
            editTextName.setText(contact.getName());
            editTextPhoneNumber.setText(contact.getPhoneNumber());
        }

        // Gán OnClickListener cho btnSave
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin liên hệ từ EditText
                String name = editTextName.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();

                // Tạo một đối tượng Contact mới
                Contact updatedContact = new Contact(contact.getId(), name, phoneNumber);

                // Cập nhật liên hệ trong cơ sở dữ liệu
                updateContactInDB(updatedContact);

                // Đóng activity EditContactActivity
                finish();
            }
        });
    }

    private void updateContactInDB(Contact contact) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.updateContact(contact);
    }
}
