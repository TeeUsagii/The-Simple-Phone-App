package exemple.com.simple_phone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {
    private ContactAdapter contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail);

        // Nhận dữ liệu của liên hệ từ Intent
        Contact contact = getIntent().getParcelableExtra("contact");

        // Hiển thị thông tin chi tiết của liên hệ trên layout
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);

        textViewName.setText(contact.getName());
        textViewPhoneNumber.setText(contact.getPhoneNumber());

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
                // Thực hiện hoạt động gọi liên hệ
                callContact(contact);
            }
        });
    }

    // Phương thức để sửa liên hệ
    private void editContact(Contact contact) {
        // Thực hiện logic để sửa liên hệ ở đây
        // Ví dụ: Chuyển sang màn hình Sửa liên hệ và chuyển dữ liệu qua Intent
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
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


    private void callContact(Contact contact) {
        // Thực hiện logic để gọi liên hệ ở đây
        // Ví dụ: Mở màn hình gọi điện thoại với số điện thoại của liên hệ
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
        startActivity(intent);
    }

}