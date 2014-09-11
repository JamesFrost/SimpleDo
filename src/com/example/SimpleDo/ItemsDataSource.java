package com.example.SimpleDo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.base.BaseLocal;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by James on 01/09/2014.
 */
public class ItemsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DataBaseOpenHelper dbHelper;
    private String[] allColumns = {
            DataBaseOpenHelper.COLUMN_ID,
            DataBaseOpenHelper.COLUMN_NAME,
            DataBaseOpenHelper.COLUMN_DATE,
            DataBaseOpenHelper.COLUMN_COMPLETE,
            DataBaseOpenHelper.COLUMN_GROUP,
            DataBaseOpenHelper.COLUMN_PRIORITY,
            DataBaseOpenHelper.COLUMN_REMINDER,
            DataBaseOpenHelper.COLUMN_EVENT_ID,
            DataBaseOpenHelper.COLUMN_TIME_SET};

    private DateTimeFormatter formatter;

    public ItemsDataSource(Context context) {
        dbHelper = new DataBaseOpenHelper(context);
        formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    }

    public void updateItemCompleteStatus(ToDoItem toDoItem) {
        ContentValues args = new ContentValues();
        args.put(DataBaseOpenHelper.COLUMN_COMPLETE, toDoItem.isComplete());
        database.update(DataBaseOpenHelper.TABLE_ITEMS, args, DataBaseOpenHelper.COLUMN_ID + "=" + toDoItem.getId(), null);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createItem(ToDoItem toDoItem) {

        ContentValues values = new ContentValues();
        values.put(DataBaseOpenHelper.COLUMN_NAME, toDoItem.getName());

        if (toDoItem.getDate() != null)
            values.put(DataBaseOpenHelper.COLUMN_DATE, toDoItem.getDate().toString(formatter));
        else
            values.put(DataBaseOpenHelper.COLUMN_DATE, "null");
        values.put(DataBaseOpenHelper.COLUMN_COMPLETE, toDoItem.isComplete());
        values.put(DataBaseOpenHelper.COLUMN_GROUP, toDoItem.getGroup());
        values.put(DataBaseOpenHelper.COLUMN_PRIORITY, toDoItem.getPriority());
        values.put(DataBaseOpenHelper.COLUMN_REMINDER, toDoItem.isReminder());
        values.put(DataBaseOpenHelper.COLUMN_EVENT_ID, toDoItem.getEventID());
        values.put(DataBaseOpenHelper.COLUMN_TIME_SET, toDoItem.isTimeSet());

        long insertId = database.insert(DataBaseOpenHelper.TABLE_ITEMS, null,
                values);
        Cursor cursor = database.query(DataBaseOpenHelper.TABLE_ITEMS,
                allColumns, DataBaseOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        toDoItem.setId(cursor.getLong(0));
        cursor.close();
    }

    public void deleteItem(ToDoItem toDoItem) {
        long id = toDoItem.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DataBaseOpenHelper.TABLE_ITEMS, DataBaseOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<ToDoItem> getAllItems() {
        ArrayList<ToDoItem> toDoItems = new ArrayList<ToDoItem>();

        Cursor cursor = database.query(DataBaseOpenHelper.TABLE_ITEMS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ToDoItem toDoItem = cursorToItem(cursor);

            //delete items from database after 1 day
            if (toDoItem.getDate() != null && toDoItem.isComplete()) {
                if (toDoItem.getDate() instanceof LocalDate) {

                    LocalDate lt = new LocalDate();
                    lt = lt.minusDays(1);

                    if (toDoItem.getDate().isBefore(lt)) {
                        deleteItem(toDoItem);
                    } else {
                        toDoItems.add(toDoItem);
                    }

                } else {

                    LocalDateTime ldt = new LocalDateTime();
                    ldt = ldt.minusDays(1);

                    if (toDoItem.getDate().isBefore(ldt)) {
                        deleteItem(toDoItem);
                    } else {
                        toDoItems.add(toDoItem);
                    }
                }

            } else if (toDoItem.isComplete()) {
                deleteItem(toDoItem);
            } else {
                toDoItems.add(toDoItem);
            }

            cursor.moveToNext();
        }
        cursor.close();
        return toDoItems;
    }

    private ToDoItem cursorToItem(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        BaseLocal date;

        if (cursor.getString(2).equals("null")) {
            date = null;
        } else {
            String split[] = cursor.getString(2).split(":");
            if (split[split.length - 1].equals("00")) {
                date = formatter.parseLocalDateTime(cursor.getString(2));
            } else {
                split = cursor.getString(2).split(" ");
                //need better solution
                split = split[0].split("/");

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = split.length - 1; i > -1; i--) {
                    stringBuilder.append(split[i]);
                    if (i != 0) {
                        stringBuilder.append("-");
                    }
                }

                String parseDate = stringBuilder.toString();

                date = new LocalDate(parseDate);
            }
        }

        boolean complete;
        if (cursor.getString(3).equals("0")) complete = false;
        else complete = true;

        String group = cursor.getString(4);
        String priority = cursor.getString(5);
        boolean reminder = Boolean.parseBoolean(cursor.getString(6));
        long eventID = cursor.getLong(7);
        boolean timeSet = Boolean.parseBoolean(cursor.getString(8));

        ToDoItem toDoItem = new ToDoItem(name, date, group, priority, timeSet);
        toDoItem.setId(id);
        toDoItem.setEventID(eventID);
        toDoItem.setComplete(complete);
        toDoItem.setReminder(reminder);
        return toDoItem;
    }
}
