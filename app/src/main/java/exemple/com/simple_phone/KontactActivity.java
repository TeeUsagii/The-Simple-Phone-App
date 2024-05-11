package exemple.com.simple_phone;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class KontactActivity extends AppCompatActivity {
    private Button buttonAddContact;
    private RecyclerView recyclerViewContacts;
    private ContactAdapter contactAdapter;
    private static final int REQUEST_CODE_EDIT_CONTACT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontact);

        setTitle("Danh bạ");

        buttonAddContact = findViewById(R.id.buttonAddContact);
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);

        // Khởi tạo ContactAdapter
        contactAdapter = new ContactAdapter(this);
        recyclerViewContacts.setAdapter(contactAdapter);

        // Thiết lập layout manager cho RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewContacts.setLayoutManager(layoutManager);

        // Thực hiện lấy dữ liệu từ cơ sở dữ liệu và thêm vào Adapter
        DBHelper dbHelper = new DBHelper(this);
        List<Contact> contacts = dbHelper.getAllContacts();
        contactAdapter.setContacts(contacts);

        // Xử lý sự kiện khi nhấn nút "Thêm người liên hệ mới"
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactDialog();
            }
        });
    }

    private void loadContactList() {
        // Thực hiện các thao tác cần thiết để cập nhật danh sách liên hệ, chẳng hạn như lấy dữ liệu từ cơ sở dữ liệu và cập nhật RecyclerView adapter
        DBHelper dbHelper = new DBHelper(this);
        List<Contact> contacts = dbHelper.getAllContacts();
        contactAdapter.setContacts(contacts);
    }
    private void refreshCurrentPage() {
        loadContactList();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Gọi phương thức refresh trang hiện tại ở đây
            refreshCurrentPage();
        }
    };

    // Trong phương thức onResume() của KontactActivity
    @Override
    protected void onResume() {
        super.onResume();
        // Đăng ký BroadcastReceiver để lắng nghe các phát sóng từ ContactDetailActivity
        IntentFilter intentFilter = new IntentFilter("UPDATE_CONTACT_LIST");
        registerReceiver(broadcastReceiver, intentFilter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_CONTACT && resultCode == RESULT_OK) {
            if (data != null) {
                // Kiểm tra xem có danh sách liên hệ được trả về hay không
                if (data.hasExtra("updatedContactList")) {
                    ArrayList<Contact> updatedContactList = data.getParcelableArrayListExtra("updatedContactList");
                    if (updatedContactList != null) {
                        // Cập nhật danh sách liên hệ trong ContactAdapter
                        contactAdapter.setContacts(updatedContactList);
                    }
                } else if (data.hasExtra("updatedContact")) { // Kiểm tra xem có liên hệ cụ thể được cập nhật hay không
                    Contact updatedContact = data.getParcelableExtra("updatedContact");
                    if (updatedContact != null) {
                        // Cập nhật liên hệ cụ thể trong danh sách và cập nhật Adapter
                        contactAdapter.updateContact(updatedContact);
                    }
                } else if (data.hasExtra("newContact")) { // Kiểm tra xem có liên hệ mới được thêm hay không
                    Contact newContact = data.getParcelableExtra("newContact");
                    if (newContact != null) {
                        // Thêm liên hệ mới vào danh sách và cập nhật Adapter
                        contactAdapter.addContact(newContact);
                    }
                }
            }
        }
    }




    // Đảm bảo rằng bạn hủy đăng ký BroadcastReceiver khi không cần nữa
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    public void onContactsButtonClicked(View view) {

    }

    // Chuyển qua view quay số điện thoại
    public void onDialerButtonClicked(View view) {
        Intent intent = new Intent(this, DialerActivity.class);
        startActivity(intent);
    }
    public void updateContactList(List<Contact> updatedContactList) {
        // Cập nhật danh sách liên hệ trong RecyclerView adapter
        contactAdapter.setContacts(updatedContactList);
    }


    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_contact, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextPhoneNumber = dialogView.findViewById(R.id.editTextPhoneNumber);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);

        final AlertDialog dialog = builder.create();
        dialog.show();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();

                // Kiểm tra xem liệu tên và số điện thoại có rỗng hay không
                if (!name.isEmpty() && !phoneNumber.isEmpty()) {
                    // Tạo một đối tượng Contact mới
                    Contact contact = new Contact(name, phoneNumber);

                    // Thêm dữ liệu vào cơ sở dữ liệu
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.addContact(contact);

                    // Thêm dữ liệu vào RecyclerView
                    contactAdapter.addContact(contact);

                    // Hiển thị thông báo cho người dùng
                    Toast.makeText(getApplicationContext(), "Đã thêm: " + name + ", " + phoneNumber, Toast.LENGTH_SHORT).show();

                    // Đóng dialog
                    dialog.dismiss();
                } else {
                    // Nếu các trường không hợp lệ, bạn có thể hiển thị thông báo hoặc xử lý khác tùy thuộc vào yêu cầu của ứng dụng
                    Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
