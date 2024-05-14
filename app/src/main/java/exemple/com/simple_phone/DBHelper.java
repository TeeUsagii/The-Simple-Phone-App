package exemple.com.simple_phone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DbName = "ContactDB";

    public DbHelper(@Nullable Context context) {
        super(context, DbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String contactTB = "CREATE TABLE CONTACT(" +
                "id TEXT UNIQUE," +
                "title TEXT," +
                "phoneNumber TEXT UNIQUE);";
        db.execSQL(contactTB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS CONTACT");
            onCreate(db);
        }
    }


    public String getContactNameByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String contactName = null;

        Cursor cursor = db.query(
                "CONTACT",
                new String[]{"title"},
                "phoneNumber = ?",
                new String[]{phoneNumber},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex("title"));
            cursor.close();
        }

        return contactName;
    }
}
