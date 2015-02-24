package client.potlach.com.potlachandroid.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import client.potlach.com.potlachandroid.model.Gift;

/**
 * Created by thiago on 11/21/14.
 */
public class GiftTouchesTrackerOpenHelper extends SQLiteOpenHelper {

    public static String DBNAME = "PhotoGift";
    final private static Integer VERSION = 1;
    final static String TABLE_NAME = "gifts";
    final static String GIFT_TOUCHES = "touches";
    final static String _ID = "_id";
    final static String[] columns = { _ID, GIFT_TOUCHES };

    final private static String CREATE_CMD =

            "CREATE TABLE "+ TABLE_NAME +" (" + _ID
                    + " INTEGER PRIMARY KEY NOT NULL, "
                    + GIFT_TOUCHES + " INTEGER NOT NULL)";

    final private Context mContext;


    public GiftTouchesTrackerOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(DBNAME);
    }

    // Adding new gift
    public void addGift(Gift gift) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, gift.getId()); // Gift Id
        values.put(GIFT_TOUCHES, gift.getLikeTouches()); // Gift number of touches

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting single gift
    public Long getGiftTouches(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {GIFT_TOUCHES}, _ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null && cursor.moveToFirst())
            return Long.parseLong(cursor.getString(0));
        else
            return null;
    }

    // Getting All gifts
    public List<Gift> getAllGifts() {
        return null;
    }

    // Getting gifts Count
    public int getGiftsCount() {
        return 0;
    }

    // Updating single gifts
    public int updateGiftTouches(Gift gift) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GIFT_TOUCHES, gift.getLikeTouches()); // Gift number of touches

        // updating row
        return db.update(TABLE_NAME, values, _ID + " = ?",
                new String[] { String.valueOf(gift.getId()) });
    }

    // Deleting single gift
    public void deleteGift(Gift gift) {

    }
}
