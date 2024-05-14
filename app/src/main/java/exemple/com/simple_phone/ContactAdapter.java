package exemple.com.simple_phone;

import static android.Manifest.permission.CALL_PHONE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.core.content.ContextCompat;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private static final int CALL_PHONE_REQUEST = 1;
    private final Context context;
    private final List<Contact> list;

    private ContactDAO contactDAO;

    public ContactAdapter(Context context, List<Contact> list) {
        this.context = context;
        this.list = list;
        this.contactDAO = new ContactDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = list.get(position);

        holder.tvTitle.setText(contact.getTitle());
        holder.tvPhone.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(v -> {
            showPickerDialog(contact);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }

    private void showPickerDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.show_dialog_layout, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnCall = view.findViewById(R.id.btnCall);

        btnUpdate.setOnClickListener(v -> {
            dialog.dismiss();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.edit_contact_dialog, null);
            builder1.setView(view1);
            Dialog dialog1 = builder1.create();
            dialog1.show();

            EditText edtTitle = view1.findViewById(R.id.edtTitle);
            EditText edtPhone = view1.findViewById(R.id.edtPhone);
            Button btnCancel = view1.findViewById(R.id.btnCancel);
            Button btnSave = view1.findViewById(R.id.btnSave);

            edtTitle.setText(contact.getTitle());
            edtPhone.setText(contact.getPhoneNumber());

            btnCancel.setOnClickListener(v1 -> {
                dialog1.dismiss();
            });


            btnSave.setOnClickListener(v1 -> {
                if (!edtTitle.getText().toString().isEmpty()) {
                    contact.setTitle(edtTitle.getText().toString());
                }

                if (!edtPhone.getText().toString().isEmpty()) {
                    contact.setPhoneNumber(edtPhone.getText().toString().trim());
                }

                if (contactDAO.update(contact)) {
                    list.clear();
                    list.addAll(contactDAO.select());
                    notifyDataSetChanged();
                    dialog1.dismiss();
                }
            });
        });

        btnCall.setOnClickListener(v1 -> {
            dialog.dismiss();
            String phoneNumber = contact.getPhoneNumber();
            makeCall(phoneNumber);
        });

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            new AlertDialog.Builder(context)
                    .setTitle("Cảnh báo!")
                    .setMessage("Bạn có muốn xóa liên hệ?")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (contactDAO.delete(contact)) {
                                list.clear();
                                list.addAll(contactDAO.select());
                                notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

    }

    private void makeCall(String phoneNumber) {
        // Kiểm tra quyền CALL_PHONE
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Thực hiện cuộc gọi
            Uri uri = Uri.parse("tel:" + phoneNumber);
            context.startActivity(new Intent(Intent.ACTION_CALL, uri));
        } else {
            // Yêu cầu quyền CALL_PHONE nếu chưa được cấp
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST);
        }
    }
}
