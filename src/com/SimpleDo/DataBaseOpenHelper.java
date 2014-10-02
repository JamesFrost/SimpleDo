package com.SimpleDo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class to help open the database.
 *
 * Created by James Frost on 13/08/2014.
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_ITEMS = "toDoItems";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMPLETE = "complete";
    public static final String COLUMN_GROUP = "itemgroup";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_REMINDER = "reminder";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_TIME_SET = "time_set";

    private static final String DATABASE_NAME = "toDoItems.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_ITEMS + "(" +
            COLUMN_ID + " integer primary key autoincrement," +
            COLUMN_NAME + " text not null," +
            COLUMN_DATE + " text not null," +
            COLUMN_COMPLETE + " text not null," +
            COLUMN_GROUP + " text not null," +
            COLUMN_PRIORITY + " text not null," +
            COLUMN_REMINDER + " text not null," +
            COLUMN_EVENT_ID + " integer not null," +
            COLUMN_TIME_SET + " text not null);";

    public DataBaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.w(DataBaseOpenHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data"
//        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }
}
