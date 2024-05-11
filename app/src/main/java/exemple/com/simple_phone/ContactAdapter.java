package exemple.com.simple_phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;

    // Định nghĩa BroadcastReceiver
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Cập nhật lại danh sách liên hệ
            DBHelper dbHelper = new DBHelper(context);
            List<Contact> updatedContacts = dbHelper.getAllContacts();
            setContacts(updatedContacts);
        }
    };


    public ContactAdapter(Context context) {
        this.context = context;
        contactList = new ArrayList<>();

        // Đăng ký BroadcastReceiver ở đây
        IntentFilter intentFilter = new IntentFilter("UPDATE_CONTACT_LIST");
        context.registerReceiver(broadcastReceiver, intentFilter);
    }



    public void setContacts(List<Contact> contacts) {
        this.contactList = contacts;
        notifyDataSetChanged();
    }

    public void addContact(Contact contact) {
        contactList.add(contact);
        notifyDataSetChanged();
    }

    public void updateContact(Contact contact) {
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).getId() == contact.getId()) {
                contactList.set(i, contact);
                notifyItemChanged(i);
                break;
            }
        }
    }



    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textViewName.setText(contact.getName());
        holder.textViewPhoneNumber.setText(contact.getPhoneNumber());

        // Xử lý sự kiện khi người dùng nhấp vào một liên hệ
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Truyền dữ liệu của liên hệ sang ContactDetailActivity
                Intent intent = new Intent(context, ContactDetailActivity.class);
                intent.putExtra("contact", contact);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("ContactAdapter", "getItemCount: " + contactList.size());
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhoneNumber;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
        }
    }

    // Đảm bảo rằng bạn hủy đăng ký BroadcastReceiver khi không cần nữa
    public void onDestroy() {
        context.unregisterReceiver(broadcastReceiver);
    }

}
