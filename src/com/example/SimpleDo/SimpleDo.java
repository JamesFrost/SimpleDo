package com.example.SimpleDo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimpleDo extends Activity {

    private LinearLayout linearLayoutToday;
    private LinearLayout linearLayoutTomorrow;
    private LinearLayout linearLayoutFuture;
    private LinearLayout linearLayoutSomeday;
    private Button go;
    private EditText toDoName;
    private View.OnClickListener droidTapListener;
    private ArrayList<ToDoItem> toDoList;
    private Calendar now;
    private ListView testThree;
    private AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPlanetTitles = getResources().getStringArray(R.array.filters);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        toDoName = (EditText) findViewById(R.id.toDoName);
        linearLayoutToday = (LinearLayout) findViewById(R.id.linearLayoutToday);
        linearLayoutTomorrow = (LinearLayout) findViewById(R.id.linearLayoutTommorw);
        linearLayoutFuture = (LinearLayout) findViewById(R.id.linearLayoutFuture);
        linearLayoutSomeday = (LinearLayout) findViewById(R.id.linearLayoutSomeday);
        testThree = (ListView) findViewById(R.id.listView);
        testThree.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        go = (Button) findViewById(R.id.go);
        droidTapListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.go:
                        Intent intent = new Intent(SimpleDo.this, CreateItem.class);
                        intent.putExtra("toDoItemName", toDoName.getText().toString().trim());
                        toDoName.setText("");
                        startActivityForResult(intent, 100);
                }
            }
        };
        go.setOnClickListener(droidTapListener);

        toDoList = new ArrayList<ToDoItem>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            Bundle bundle = data.getExtras();
            if (resultCode == 100 && bundle != null) {
                addItem((ToDoItem) data.getSerializableExtra("newToDoItem"));
            }
        }
    }

    private CheckBox ch;

    /**
     * Class which adds a checkbox to the relevant layout and links it to the correct toDoItem object.
     *
     * @param toDoItem The ToDoItem to add.
     */
    private void addItem(final ToDoItem toDoItem) {
        now = Calendar.getInstance();
        toDoList.add(toDoItem);
        ch = new CheckBox(this);

//        setReminder(toDoItem);

//        addReminder(2014, 6, 5, 19, 47, "Test");

        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", "A Test Event from android app");
        startActivity(intent);

        ch.setText(toDoItem.getName() + " " + toDoItem.getDueTime());
        registerForContextMenu(ch);
        if (toDoItem.getDate() == null) {
            linearLayoutSomeday.addView(ch);
        } else if (isTodaysDate(toDoItem)) {
            linearLayoutToday.addView(ch);
        } else if (isTomorrowsDate(toDoItem)) {
            linearLayoutTomorrow.addView(ch);
        } else {
            linearLayoutFuture.addView(ch);
        }
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoItem.setComplete(((CheckBox) view).isChecked());
//                toDoItem.setComplete(((CheckedTextView) view).isChecked());
            }
        });
    }

    private void setReminder(ToDoItem toDoItem) {
        Intent myIntent = new Intent(getApplicationContext(), NotificationService.class);
        myIntent.putExtra("TaskName", toDoItem.getName());
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.set(AlarmManager.RTC_WAKEUP, toDoItem.getDate().getTime(), pendingIntent);
    }

    private void addReminder(int statrYear, int startMonth, int startDay, int startHour, int startMinut, String title) {
        // Convert start of begin time of reminder in milliseconds.
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(statrYear, startMonth, startDay, startHour, startMinut);
        long startMillis = beginTime.getTimeInMillis();

        // String to access default google calendar of device for Event setting.
        String eventUriString = "content://com.android.calendar/events";

        // Creation of Event.
        ContentValues eventValues = new ContentValues();
        // Set calendar as 1 for default calendar.
        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        // Set title as user define.
        eventValues.put(CalendarContract.Events.TITLE, title);
        // Set description as user define.
        eventValues.put(CalendarContract.Events.DESCRIPTION, "SimpleDo");
        // Set location as user define.
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, "GMT");
        // Set start time as system time or time converted in milliseconds.
        eventValues.put(CalendarContract.Events.DTSTART, startMillis);
        // Set status of event as 1.
        eventValues.put("eventStatus", 1);
        // Set visibility of event as 3 (public).
        eventValues.put("visibility", 3);
        // Set transparency as 0. No other app seen through reminder.
        eventValues.put("transparency", 0);
        // Set alarm as 1. Ringing.
        eventValues.put(CalendarContract.Events.HAS_ALARM, 1);

        // Set Event in calendar.
        Uri eventUri = getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        // Getting ID of event in Long.
        assert eventUri != null;
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        /***************** Event: Reminder(with alert) Adding reminder to event *******************/
        // String to access default google calendar of device for reminder setting.
        String reminderUriString = "content://com.android.calendar/reminders";
        ContentValues reminderValues = new ContentValues();

        // Set reminder on Event ID.
        reminderValues.put("event_id", eventID);
        // Set reminder minute before.
        reminderValues.put("minutes", 1);
        // Set method of reminder
        reminderValues.put("method", 1);

        @SuppressWarnings("unused")
        //Setting reminder in calendar on Event.
                Uri reminderUri = getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
    }

    private long calculateReminderTime(ToDoItem toDoItem) {
        Calendar calendar = Calendar.getInstance();
//        GregorianCalendar currentDay=new  GregorianCalendar (calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),0);
//        GregorianCalendar nextDay = new GregorianCalendar(2014, toDoItem.getMonth(), toDoItem.getDay(), toDoItem.getHour(), toDoItem.getMin(), 0);
        GregorianCalendar nextDay = new GregorianCalendar();
        toDoItem.getDate().getTime();

//        return nextDay.getTimeInMillis()-currentDay. getTimeInMillis();
        return nextDay.getTimeInMillis() - System.currentTimeMillis();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                if (info != null) {
                    System.out.println("It works!" + info.targetView);
                }
                return true;
            case R.id.quick_reschedule:
                System.out.println("It works!");
                return true;
            case R.id.delete:
                System.out.println("It works!");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    /**
     * Method which decides if a ToDoItem object falls on today's date.
     *
     * @param toDoItem The ToDoItem object.
     * @return boolean
     */
    private boolean isTodaysDate(ToDoItem toDoItem) {
        return toDoItem.getYear() == getCurrentYear() && toDoItem.getMonth() == getCurrentMonth() && toDoItem.getDay() == getCurrentDay();
    }

    /**
     * Method which decides if a ToDoItem object falls on tomorrows date.
     *
     * @param toDoItem The ToDoItem object.
     * @return boolean True if date is today's date.
     */
    private boolean isTomorrowsDate(ToDoItem toDoItem) {
        return toDoItem.getYear() == getCurrentYear() && toDoItem.getMonth() == getCurrentMonth() && toDoItem.getDay() == getCurrentDay() + 1;
    }

    private int getCurrentYear() {
        return now.get(Calendar.YEAR);
    }

    private int getCurrentMonth() {
        return now.get(Calendar.MONTH);
    }

    private int getCurrentDay() {
        return now.get(Calendar.DAY_OF_WEEK);
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}

