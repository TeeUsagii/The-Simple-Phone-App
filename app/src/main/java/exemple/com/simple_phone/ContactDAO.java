package exemple.com.simple_phone;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class ContactDAO {
    private final DbHelper dbHelper;

    public ContactDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public List<Contact> select() {
        List<Contact> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM CONTACT ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    list.add(new Contact(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Log.e(TAG, "Select Failed: " + ex);
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return list;
    }

    public boolean insert(Contact contact) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();

        ContentValues contentValues = new ContentValues();

        contentValues.put("id", contact.getId());
        contentValues.put("title", contact.getTitle());
        contentValues.put("phoneNumber", contact.getPhoneNumber());

        long check = database.insert("CONTACT", null, contentValues);
        database.setTransactionSuccessful();
        database.endTransaction();
        return check != -1;
    }

    public boolean update(Contact contact) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put("id", contact.getId());
        values.put("title", contact.getTitle());
        values.put("phoneNumber", contact.getPhoneNumber());

        long check = database.update("CONTACT", values, "id=?", new String[]{contact.getId()});
        database.setTransactionSuccessful();
        database.endTransaction();
        return check != -1;
    }

    public boolean delete(Contact contact) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();

        long check = database.delete("CONTACT", "id=?", new String[]{contact.getId()});
        database.setTransactionSuccessful();
        database.endTransaction();
        return check != -1;
    }

}
