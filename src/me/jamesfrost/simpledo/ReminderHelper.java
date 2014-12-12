package me.jamesfrost.simpledo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Handles all reminders
 *
 * Created by James Frost on 12/12/2014.
 */
public class ReminderHelper {

    private DateTimeFormatter formatter;

    public ReminderHelper() {
        formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Set a reminder for an item using the phones native calendar.
     *
     * @param toDoItem The item to set a reminder for
     */
    public void addReminder(ToDoItem toDoItem, ContentResolver cr) {

        String eventUriStr = "content://com.android.calendar/events";
        ContentValues event = new ContentValues();
        // id, We need to choose from our mobile for primary its 1
        event.put("calendar_id", 1);
        event.put("title", toDoItem.getName());
        event.put("eventTimezone", "GMT");

//        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        String test = toDoItem.getDate().toString(formatter);
        LocalDateTime date = formatter.parseLocalDateTime(test).minusHours(1);

        long localMillis = date.toDateTime(DateTimeZone.UTC).getMillis();

        event.put("dtstart", localMillis);
        event.put("dtend", localMillis);
        event.put("hasAlarm", 1);
        //If it is bithday alarm or such kind (which should remind me for whole day) 0 for false, 1 for true
        // values.put("allDay", 1);

        Uri eventUri = cr.insert(Uri.parse(eventUriStr), event);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());
        toDoItem.setEventID(eventID);

        String reminderUriString = "content://com.android.calendar/reminders";
        ContentValues reminderValues = new ContentValues();
        reminderValues.put("event_id", eventID);
        // Default value of the system. Minutes is a integer
        reminderValues.put("minutes", 1);
        // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
        reminderValues.put("method", 1);
        cr.insert(Uri.parse(reminderUriString), reminderValues);
    }

    /**
     * Deletes the calendar event for a toDoItem.
     *
     * @param toDoItem The item to the delete the calendar event for
     */
    public void deleteReminder(ToDoItem toDoItem, ContentResolver cr) {
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, toDoItem.getEventID());
        cr.delete(deleteUri, null, null);
    }
}
