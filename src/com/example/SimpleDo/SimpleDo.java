package com.example.SimpleDo;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import android.provider.CalendarContract;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Main Activity.
 *
 * @author James Frost
 */
public class SimpleDo extends Activity {

    private RelativeLayout relativeLayout;
    private LinearLayout linearLayoutOverdue;
    private LinearLayout linearLayoutToday;
    private LinearLayout linearLayoutTomorrow;
    private LinearLayout linearLayoutFuture;
    private LinearLayout linearLayoutSomeday;
    private EditText toDoName;
    private ArrayList<ToDoItem> toDoList;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private DrawerItemClickListener drawerItemClickListener;
    private ItemsDataSource dataSource;
    private TextView textViewOverDue;
    private TextView textViewToday;
    private TextView textViewTomorrow;
    private TextView textViewFuture;
    private TextView textViewSomeday;
    private LinearLayout mainLinearLayout;
    private TextView textViewNoItems;
    private DateTimeFormatter formatter;
    private CheckBox mLastViewTouched;

    private static final String noItemsText = "Nothing to do, add something!";
    private static final DateTimeFormatter formatterCheckBoxDateTime = DateTimeFormat.forPattern("dd/MM/yyyy - HH:mm");
    private static final DateTimeFormatter formatterCheckBoxTime = DateTimeFormat.forPattern("HH:mm");
    private static final DateTimeFormatter formatterCheckBoxDate = DateTimeFormat.forPattern("dd/MM/yyyy");

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Context mContext;
        mContext = getApplicationContext();
        assert mContext != null;
        textViewNoItems = new TextView(mContext);
        textViewNoItems.setText(noItemsText);
        textViewNoItems.setTextSize(17);
        textViewNoItems.setTypeface(null, Typeface.ITALIC);

        formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        dataSource = new ItemsDataSource(this);
        dataSource.open();

        textViewOverDue = (TextView) findViewById(R.id.overDue);
        textViewToday = (TextView) findViewById(R.id.today);
        textViewTomorrow = (TextView) findViewById(R.id.tomorrow);
        textViewFuture = (TextView) findViewById(R.id.future);
        textViewSomeday = (TextView) findViewById(R.id.someday);
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);

        String[] filterArray = getResources().getStringArray(R.array.filters);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, filterArray));
        // Set the list's click listener
        drawerItemClickListener = new DrawerItemClickListener();
        drawerList.setItemChecked(0, true);
        drawerList.setOnItemClickListener(drawerItemClickListener);

        toDoName = (EditText) findViewById(R.id.toDoName);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        linearLayoutOverdue = (LinearLayout) findViewById(R.id.linearLayoutOverdue);
        linearLayoutToday = (LinearLayout) findViewById(R.id.linearLayoutToday);
        linearLayoutTomorrow = (LinearLayout) findViewById(R.id.linearLayoutTommorw);
        linearLayoutFuture = (LinearLayout) findViewById(R.id.linearLayoutFuture);
        linearLayoutSomeday = (LinearLayout) findViewById(R.id.linearLayoutSomeday);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Button go = (Button) findViewById(R.id.go);
        View.OnClickListener droidTapListener = new View.OnClickListener() {
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
        toDoList = dataSource.getAllItems();
        drawerItemClickListener.filter(drawerList.getCheckedItemPosition());

        for (ToDoItem a : toDoList) {
            System.out.println("Name: " + a.getName() + " Complete: " + a.isComplete());
        }

        getOverflowMenu();
    }

    /**
     * Method that makes sure the action overflow button is always shown, even on devices with a menu button.
     */
    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            Bundle bundle = data.getExtras();

            if (resultCode == 100 && bundle != null) { //Add item result

                ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra("newToDoItem");
                dataSource.createItem(toDoItem);

                if (bundle.getBoolean("reminder")) {
                    addReminder(toDoItem);
                }

                toDoList.add(toDoItem);

                //Bubble sort - sorts toDoList by date
                for (int i = toDoList.size() - 1; i >= 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (toDoList.get(j).getDate() instanceof LocalDateTime && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                            if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
                                ToDoItem temp = toDoList.get(j);
                                toDoList.set(j, toDoList.get(j + 1));
                                toDoList.set(j + 1, temp);
                            }
                        } else if (!(toDoList.get(j).getDate() instanceof LocalDateTime) && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        }
                    }
                }

                drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
            } else if (resultCode == 200 && bundle != null) { //Edit item result
                ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra("newToDoItem");
                ToDoItem oldToDoItem = (ToDoItem) bundle.get("oldToDoItem");

                if (bundle.getBoolean("reminder") != oldToDoItem.isReminder()) {
                    if (bundle.getBoolean("reminder"))
                        addReminder(toDoItem);
                    else
                        deleteReminder(toDoItem);
                }

                toDoList.remove(oldToDoItem);
                toDoList.add(toDoItem);

                //Bubble sort - sorts toDoList by date
                for (int i = toDoList.size() - 1; i >= 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (toDoList.get(j).getDate() instanceof LocalDateTime && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                            if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
                                ToDoItem temp = toDoList.get(j);
                                toDoList.set(j, toDoList.get(j + 1));
                                toDoList.set(j + 1, temp);
                            }
                        } else if (!(toDoList.get(j).getDate() instanceof LocalDateTime) && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        }
                    }
                }

                dataSource.deleteItem(oldToDoItem);
                dataSource.createItem(toDoItem);

                drawerItemClickListener.filter(drawerList.getCheckedItemPosition());

            } else if (resultCode == 300 && bundle != null) { //Quick Reschedule
                ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra("newToDoItem");
                ToDoItem oldToDoItem = (ToDoItem) bundle.get("oldToDoItem");

                assert toDoItem != null;
                if (oldToDoItem.isReminder() && toDoItem.getDate() != null && !(toDoItem.getDate() instanceof LocalDateTime)) {
                    deleteReminder(oldToDoItem);
                } else
                    toDoItem.setEventID(oldToDoItem.getEventID());

                toDoList.remove(oldToDoItem);
                toDoList.add(toDoItem);

                //Bubble sort - sorts toDoList by date
                for (int i = toDoList.size() - 1; i >= 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (toDoList.get(j).getDate() instanceof LocalDateTime && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                            if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
                                ToDoItem temp = toDoList.get(j);
                                toDoList.set(j, toDoList.get(j + 1));
                                toDoList.set(j + 1, temp);
                            }
                        } else if (!(toDoList.get(j).getDate() instanceof LocalDateTime) && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        }
                    }
                }

                dataSource.deleteItem(oldToDoItem);
                dataSource.createItem(toDoItem);

                drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
            }
        }
    }

    /**
     * Set a reminder for an item using the phones native calendar.
     *
     * @param toDoItem The item to set a reminder for.
     */
    private void addReminder(ToDoItem toDoItem) {

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

        ContentResolver cr = getContentResolver();
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
     * @param toDoItem The item to the delete the calendar event for.
     */
    private void deleteReminder(ToDoItem toDoItem) {
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, toDoItem.getEventID());
        int rows = getContentResolver().delete(deleteUri, null, null);
        final String DEBUG_TAG = "MyActivity";
        Log.i(DEBUG_TAG, "Rows deleted: " + rows);
    }

    /**
     * Class which adds a checkbox to the relevant layout and links it to the correct toDoItem object.
     *
     * @param toDoItem The ToDoItem to add.
     */
    private void addItem(final ToDoItem toDoItem) {
        final CheckBox ch = new CheckBox(this);

        ch.setTextColor(Color.LTGRAY);
        registerForContextMenu(ch);

//        DateTimeFormatter formatterCheckBoxDateTime = DateTimeFormat.forPattern("dd/MM/yyyy - HH:mm");
//        DateTimeFormatter formatterCheckBoxTime = DateTimeFormat.forPattern("HH:mm");
//        DateTimeFormatter formatterCheckBoxDate = DateTimeFormat.forPattern("dd/MM/yyyy");

        if (toDoItem.getDate() == null && !toDoItem.isComplete()) {
            ch.setText(toDoItem.getName());
            linearLayoutSomeday.addView(ch);
        } else if (isTodaysDate(toDoItem)) {
            if (toDoItem.getDate() instanceof LocalDateTime)
                ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxTime));
            else ch.setText(toDoItem.getName());
            linearLayoutToday.addView(ch);
            if (toDoItem.isComplete()) {
                ch.setChecked(true);
            }

        } else if (isTomorrowsDate(toDoItem)) {
            if (toDoItem.getDate() instanceof LocalDateTime)
                ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxTime));
            else ch.setText(toDoItem.getName());
            linearLayoutTomorrow.addView(ch);
            if (toDoItem.isComplete()) {
                ch.setChecked(true);
            }

        } else if (isOverDue(toDoItem) && !toDoItem.isComplete()) {
            if (toDoItem.getDate() instanceof LocalDateTime)
                ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxDateTime));
            else ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxDate));
            linearLayoutOverdue.addView(ch);
        } else if (!toDoItem.isComplete()) {
            if (toDoItem.getDate() instanceof LocalDateTime)
                ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxDateTime));
            else ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxDate));
            linearLayoutFuture.addView(ch);
        }

        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toDoItem.setComplete(((CheckBox) view).isChecked());
                dataSource.updateItemCompleteStatus(toDoItem);

                if (isOverDue(toDoItem)) {
                    linearLayoutOverdue.removeView(ch);
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                } else if (toDoItem.getDate() == null) {
                    linearLayoutSomeday.removeView(ch);
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                } else if (!isTodaysDate(toDoItem) && !isTomorrowsDate(toDoItem)) {
                    linearLayoutFuture.removeView(ch);
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                }

                if (toDoItem.isComplete() && toDoItem.isReminder()) {
                    deleteReminder(toDoItem);
                } else if (toDoItem.isReminder()) {
                    addReminder(toDoItem);
                }

            }
        });

        ch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLastViewTouched = ch;
                return false;
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

//        DateTimeFormatter formatterCheckBoxDateTime = DateTimeFormat.forPattern("dd/MM/yyyy - HH:mm");
//        DateTimeFormatter formatterCheckBoxTime = DateTimeFormat.forPattern("HH:mm");
//        DateTimeFormatter formatterCheckBoxDate = DateTimeFormat.forPattern("dd/MM/yyyy");

        switch (item.getItemId()) {
            case R.id.edit:
                for (ToDoItem a : toDoList) {
                    // && ((mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxDateTime)) || mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxDate)) || mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxTime))   ) || (mLastViewTouched.getParent()==linearLayoutSomeday || mLastViewTouched.getParent()==linearLayoutToday || mLastViewTouched.getParent()==linearLayoutTomorrow))
                    if (mLastViewTouched.getText().toString().contains(a.getName())) { //What if more than one checkbox share a name / have similar names?
                        Intent intent = new Intent(SimpleDo.this, EditItem.class);
                        intent.putExtra("toDoItemName", a.getName());
                        intent.putExtra("group", a.getGroup());
                        intent.putExtra("priority", a.getPriority());
                        intent.putExtra("reminder", a.isReminder());
                        if (a.getDate() != null)
                            intent.putExtra("date", a.getDate().toString(formatter));
                        intent.putExtra("oldToDoItem", a);
                        startActivityForResult(intent, 200);
                        break;
                    }
                }
                return true;
            case R.id.quick_reschedule:
                for (ToDoItem a : toDoList) {
                    if (mLastViewTouched.getText().toString().contains(a.getName())) {
                        Intent intent = new Intent(SimpleDo.this, QuickReschedule.class);
                        if (a.getDate() != null)
                            intent.putExtra("date", a.getDate().toString(formatter));
                        intent.putExtra("oldToDoItem", a);
                        startActivityForResult(intent, 300);
                        break;
                    }
                }
                return true;
            case R.id.delete:
                Iterator<ToDoItem> setIterator = toDoList.iterator();
                while (setIterator.hasNext()) {
                    ToDoItem currentElement = setIterator.next();
                    if (mLastViewTouched.getText().toString().contains(currentElement.getName())) {
                        dataSource.deleteItem(currentElement);
                        setIterator.remove();
                        drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                        break;
                    }
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
//            case R.id.settings:
//                Intent intent = new Intent(SimpleDo.this, SettingsActivity.class);
//                startActivity(intent);
            case R.id.help:
                //start help activity
                break;
            case R.id.about:
                Intent intent = new Intent(SimpleDo.this, AboutDialog.class);
                startActivity(intent);
                break;
        }
        return true;
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
     * @return boolean True if today's date.
     */
    private boolean isTodaysDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {

            if (toDoItem.getDate() instanceof LocalDate) {
                LocalDate lt = new LocalDate();
                return lt.equals(toDoItem.getDate());
            } else {
                LocalDateTime ldt = new LocalDateTime();
                LocalDate ld = new LocalDate();
                LocalDate test = new LocalDate(toDoItem.getDate().get(DateTimeFieldType.year()), toDoItem.getDate().get(DateTimeFieldType.monthOfYear()), toDoItem.getDate().get(DateTimeFieldType.dayOfMonth()));
                return toDoItem.getDate().isAfter(ldt) && test.isBefore(ld.plusDays(1));
            }

        } else return false;
    }

    /**
     * Method which decides if a ToDoItem object falls on tomorrows date.
     *
     * @param toDoItem The ToDoItem object.
     * @return boolean True if date is tomorrows's date.
     */
    private boolean isTomorrowsDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {

            if (toDoItem.getDate() instanceof LocalDate) {
                LocalDate lt = new LocalDate();
                return toDoItem.getDate().equals(lt.plusDays(1));
            } else {
                LocalDate lt = new LocalDate();
                LocalDate test = new LocalDate(toDoItem.getDate().get(DateTimeFieldType.year()), toDoItem.getDate().get(DateTimeFieldType.monthOfYear()), toDoItem.getDate().get(DateTimeFieldType.dayOfMonth()));
                return test.equals(lt.plusDays(1));
            }
        } else return false;
    }

    /**
     * Method which checks if a ToDoItem object is over due.
     *
     * @param toDoItem
     * @returns
     */
    private boolean isOverDue(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {
            if (toDoItem.getDate() instanceof LocalDate) {
                LocalDate lt = new LocalDate();
                return toDoItem.getDate().isBefore(lt);
            } else {
                LocalDateTime ldt = new LocalDateTime();
                return toDoItem.getDate().isBefore(ldt);
            }
        } else return false;
    }

    public ListView getDrawerList() {
        return drawerList;
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        private static final String FILTER_TITLE_NO_FILTER = "Simple Do";
        private static final String FILTER_TITLE_HIGH_PRIORITY = "High Priority";
        private static final String FILTER_TITLE_MEDIUM_PRIORITY = "Medium Priority";
        private static final String FILTER_TITLE_LOW_PRIORITY = "Low Priority";
        private static final String FILTER_TITLE_NOT_COMPLETED = "Not Completed";

        private static final String FILTER_WORK = "Work";
        private static final String FILTER_PERSONAL = "Personal";
        private static final String FILTER_HIGH_PRIORITY = "High";
        private static final String FILTER_MEDIUM_PRIORITY = "Medium";
        private static final String FILTER_LOW_PRIORITY = "Low";

        private static final int POSITION_OF_NO_FILTER = 0;
        private static final int POSITION_OF_HIGH_PRIORITY = 1;
        private static final int POSITION_OF_MEDIUM_PRIORITY = 2;
        private static final int POSITION_OF_LOW_PRIORITY = 3;
        private static final int POSITION_OF_NOT_COMPLETE = 4;
        private static final int POSITION_OF_WORK = 5;
        private static final int POSITION_OF_PERSONAL = 6;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            drawerList.setItemChecked(position, true);
            filter(position);
            drawerLayout.closeDrawer(drawerList);
        }

        /**
         * Method which applies user selected filter.
         *
         * @param position The filter selected
         */
        public void filter(int position) {

            linearLayoutToday.removeAllViews();
            linearLayoutTomorrow.removeAllViews();
            linearLayoutFuture.removeAllViews();
            linearLayoutSomeday.removeAllViews();
            linearLayoutOverdue.removeAllViews();

            if (position == POSITION_OF_NO_FILTER) { // No Filter
                for (ToDoItem toDoItem : toDoList) {
                    addItem(toDoItem);
                }
                setTitle(FILTER_TITLE_NO_FILTER);
            } else if (position == POSITION_OF_HIGH_PRIORITY) { //Only show high priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals(FILTER_HIGH_PRIORITY)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_TITLE_HIGH_PRIORITY);
            } else if (position == POSITION_OF_MEDIUM_PRIORITY) { //Only show medium priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals(FILTER_MEDIUM_PRIORITY)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_TITLE_MEDIUM_PRIORITY);
            } else if (position == POSITION_OF_LOW_PRIORITY) { //Only show low priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals(FILTER_LOW_PRIORITY)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_TITLE_LOW_PRIORITY);
            } else if (position == POSITION_OF_NOT_COMPLETE) { //Only show not completed items
                for (ToDoItem toDoItem : toDoList) {
                    if (!toDoItem.isComplete()) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_TITLE_NOT_COMPLETED);
            } else if (position == POSITION_OF_WORK) { //Filter by work
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getGroup().equals(FILTER_WORK)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_WORK);
            } else if (position == POSITION_OF_PERSONAL) { //Filter by personal
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getGroup().equals(FILTER_PERSONAL)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_PERSONAL);
            }
            updateTextViews();
        }

        /**
         * Removes/Adds TextView headers depending on if there are views in relevant layout
         */
        public void updateTextViews() {

            if (linearLayoutOverdue.getChildCount() == 0) {
                textViewOverDue.setVisibility(View.GONE);
                linearLayoutOverdue.setVisibility(View.GONE);
            } else {
                textViewOverDue.setVisibility(View.VISIBLE);
                linearLayoutOverdue.setVisibility(View.VISIBLE);
            }

            if (linearLayoutFuture.getChildCount() == 0) {
                textViewFuture.setVisibility(View.GONE);
                linearLayoutFuture.setVisibility(View.GONE);
            } else {
                textViewFuture.setVisibility(View.VISIBLE);
                linearLayoutFuture.setVisibility(View.VISIBLE);
            }

            if (linearLayoutSomeday.getChildCount() == 0) {
                textViewSomeday.setVisibility(View.GONE);
                linearLayoutSomeday.setVisibility(View.GONE);
            } else {
                textViewSomeday.setVisibility(View.VISIBLE);
                linearLayoutSomeday.setVisibility(View.VISIBLE);
            }

            if (linearLayoutTomorrow.getChildCount() == 0) {
                textViewTomorrow.setVisibility(View.GONE);
                linearLayoutTomorrow.setVisibility(View.GONE);
            } else {
                textViewTomorrow.setVisibility(View.VISIBLE);
                linearLayoutTomorrow.setVisibility(View.VISIBLE);
            }

            if (linearLayoutToday.getChildCount() == 0) {
                textViewToday.setVisibility(View.GONE);
                linearLayoutToday.setVisibility(View.GONE);
            } else {
                textViewToday.setVisibility(View.VISIBLE);
                linearLayoutToday.setVisibility(View.VISIBLE);
            }

            if (linearLayoutOverdue.getChildCount() == 0 && linearLayoutFuture.getChildCount() == 0 && linearLayoutSomeday.getChildCount() == 0 && linearLayoutTomorrow.getChildCount() == 0 && linearLayoutToday.getChildCount() == 0) {
                if (textViewNoItems.getParent() == null)
                    mainLinearLayout.addView(textViewNoItems);
            } else if (textViewNoItems.getParent() == mainLinearLayout) {
                mainLinearLayout.removeView(textViewNoItems);
            }

            linearLayoutSomeday.requestLayout();
            linearLayoutFuture.requestLayout();
            linearLayoutTomorrow.requestLayout();
            linearLayoutToday.requestLayout();
            linearLayoutOverdue.requestLayout();
            relativeLayout.requestLayout();

        }
    }
}