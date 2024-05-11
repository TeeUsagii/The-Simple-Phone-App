package exemple.com.simple_phone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {
    private ContactAdapter contactAdapter;

    // Trong phương thức cập nhật liên hệ
    Intent broadcastIntent = new Intent("UPDATE_CONTACT_LIST");

    public void sendBroadcastMethod() {
        // Tạo Intent
        Intent broadcastIntent = new Intent("UPDATE_CONTACT_LIST");

        // Gửi broadcast
        sendBroadcast(broadcastIntent);
    }

    private void callContact(Contact contact) {
        // Thực hiện logic để gọi liên hệ ở đây
        // Ví dụ: Mở màn hình gọi điện thoại với số điện thoại của liên hệ
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail);

        setTitle("Thông tin liên hệ");

        // Nhận dữ liệu của liên hệ từ Intent
        Contact contact = getIntent().getParcelableExtra("contact");

        // Khai báo biến textViewName
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);

        // Xử lý sự kiện khi người dùng nhấn vào nút sửa
        Button buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện hoạt động sửa liên hệ
                editContact(contact);
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút xóa
        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện hoạt động xóa liên hệ
                deleteContact(contact);
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút gọi
        Button buttonCall = findViewById(R.id.buttonCall);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện logic để gọi liên hệ ở đây
                callContact(contact); // Gọi phương thức callContact() để thực hiện cuộc gọi
            }
        });

    }

    private void editContact(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Liên Hệ");

        View viewInflated = getLayoutInflater().inflate(R.layout.edit_contact_dialog, null);
        final EditText editTextName = viewInflated.findViewById(R.id.editTextName);
        final EditText editTextPhoneNumber = viewInflated.findViewById(R.id.editTextPhoneNumber);

        editTextName.setText(contact.getName());
        editTextPhoneNumber.setText(contact.getPhoneNumber());

        builder.setView(viewInflated);

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editTextName.getText().toString();
                String newPhoneNumber = editTextPhoneNumber.getText().toString();

                // Cập nhật thông tin liên hệ
                contact.setName(newName);
                contact.setPhoneNumber(newPhoneNumber);

                // Cập nhật thông tin liên hệ trong cơ sở dữ liệu
                DBHelper dbHelper = new DBHelper(ContactDetailActivity.this);
                dbHelper.updateContact(contact);

                // Gửi broadcast để thông báo cho KontactActivity cần cập nhật lại danh sách liên hệ
                Intent intent = new Intent("UPDATE_CONTACT_LIST");
                sendBroadcast(intent);

                // Trả về kết quả cho KontactActivity và kết thúc activity hiện tại
                Intent returnIntent = new Intent();
                returnIntent.putExtra("updatedContact", contact);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Lấy lại thông tin của liên hệ từ cơ sở dữ liệu và hiển thị lên giao diện
        loadContactInformation();
    }

    private void loadContactInformation() {
        // Nhận dữ liệu của liên hệ từ Intent hoặc từ cơ sở dữ liệu (tùy thuộc vào cách bạn thiết lập)
        Contact contact = getIntent().getParcelableExtra("contact");

        // Hiển thị thông tin chi tiết của liên hệ trên layout
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);

        // Kiểm tra xem contact có tồn tại không trước khi hiển thị
        if (contact != null) {
            textViewName.setText(contact.getName());
            textViewPhoneNumber.setText(contact.getPhoneNumber());
        }
    }

    private void deleteContact(Contact contact) {
        // Hiển thị hộp thoại xác nhận xóa
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn xóa liên hệ này?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Xóa liên hệ từ cơ sở dữ liệu
                        DBHelper dbHelper = new DBHelper(ContactDetailActivity.this);
                        dbHelper.deleteContact(contact);

                        // Quay trở lại KontactActivity
                        Intent intent = new Intent(ContactDetailActivity.this, KontactActivity.class);
                        startActivity(intent);
                        finish(); // Đóng ContactDetailActivity
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Người dùng không xác nhận xóa, không làm gì cả
                    }
                });
        // Hiển thị hộp thoại
        builder.create().show();
    }


}
