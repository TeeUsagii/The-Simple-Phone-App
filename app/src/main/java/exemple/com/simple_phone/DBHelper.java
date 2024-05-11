package exemple.com.simple_phone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột'

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";

    // Câu lệnh tạo bảng
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_PHONE_NUMBER + " TEXT)";

    private Context context; // Thêm biến context

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Lưu trữ context
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);

        // Thêm người liên hệ mặc định vào cơ sở dữ liệu khi tạo bảng
        addDefaultContact(db);
    }

    // Phương thức để thêm người liên hệ mặc định vào cơ sở dữ liệu
    private void addDefaultContact(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Exam");
        values.put(COLUMN_PHONE_NUMBER, "0123456789");
        db.insert(TABLE_CONTACTS, null, values);
    }


    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("phone_number", contact.getPhoneNumber());
        db.insert("contacts", null, values);
        db.close();
    }

    public String getContactNameByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;

        // Truy vấn cơ sở dữ liệu để lấy tên người dùng dựa trên số điện thoại
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{COLUMN_NAME},
                COLUMN_PHONE_NUMBER + " = ?", new String[]{phoneNumber},
                null, null, null);

        // Kiểm tra xem có dữ liệu trả về không và lấy tên người dùng nếu có
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            cursor.close();
        }

        // Đóng kết nối cơ sở dữ liệu
        db.close();

        return name;
    }


    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)));
                // Thêm đối tượng Contact vào danh sách
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }


    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý khi cần nâng cấp cơ sở dữ liệu
    }
}
