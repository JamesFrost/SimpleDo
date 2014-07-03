package com.example.SimpleDo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

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
    private Button go;
    private EditText toDoName;
    private View.OnClickListener droidTapListener;
    private ArrayList<ToDoItem> toDoList;
    private Calendar now;
    private ListView testThree;
    private String[] mPlanetTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPlanetTitles = getResources().getStringArray(R.array.filters);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        toDoName = (EditText) findViewById(R.id.toDoName);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        linearLayoutOverdue = (LinearLayout) findViewById(R.id.linearLayoutOverdue);
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
            if (resultCode == 100 && bundle != null) {

                if (bundle.getBoolean("reminder")) {
                    addReminder((ToDoItem) data.getSerializableExtra("newToDoItem"));
                }
                toDoList.add((ToDoItem) data.getSerializableExtra("newToDoItem"));
                addItem((ToDoItem) data.getSerializableExtra("newToDoItem"));
            }
        }
    }

    /**
     * Set a reminder for an item using the phones native calendar.
     *
     * @param toDoItem The item to set a reminder for.
     */
    private void addReminder(ToDoItem toDoItem) {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
//        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", toDoItem.getName());
        startActivity(intent);
    }

    /**
     * Class which adds a checkbox to the relevant layout and links it to the correct toDoItem object.
     *
     * @param toDoItem The ToDoItem to add.
     */
    private void addItem(final ToDoItem toDoItem) {
        now = Calendar.getInstance();
        final CheckBox ch = new CheckBox(this);

        ch.setText(toDoItem.getName() + " " + toDoItem.getDueTime());
//        registerForContextMenu(ch);

        if (toDoItem.getDate() == null && !toDoItem.isComplete()) {
            linearLayoutSomeday.addView(ch);
        } else if (isTodaysDate(toDoItem)) {
            linearLayoutToday.addView(ch);
            if (toDoItem.isComplete()) {
                ch.setChecked(true);
            }

        } else if (isTomorrowsDate(toDoItem)) {
            linearLayoutTomorrow.addView(ch);
            if (toDoItem.isComplete()) {
                ch.setChecked(true);
            }

        } else if (toDoItem.isOverDue() && !toDoItem.isComplete()) {
            linearLayoutOverdue.addView(ch);
        } else if (!isPastDate(toDoItem) && !toDoItem.isComplete()) {
            linearLayoutFuture.addView(ch);
        }


        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toDoItem.isOverDue()) {
                    linearLayoutOverdue.removeView(ch);
                } else if (toDoItem.getDate() == null) {
                    linearLayoutSomeday.removeView(ch);
                } else if (!isTodaysDate(toDoItem) && !isTomorrowsDate(toDoItem)) {
                    linearLayoutFuture.removeView(ch);
                }
                toDoItem.setComplete(((CheckBox) view).isChecked());
            }
        });
        ch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerForContextMenu(ch);
                return false;
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(SimpleDo.this, SettingsActivity.class);
                startActivity(intent);
            case R.id.help:
                //start help activity
                break;
            case R.id.about:
                //start about activity
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

    private boolean isPastDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {
            return toDoItem.getYear() < getCurrentYear() || toDoItem.getYear() == getCurrentYear() && toDoItem.getMonth() < getCurrentMonth() || toDoItem.getYear() == getCurrentYear() && toDoItem.getMonth() == getCurrentMonth() && toDoItem.getDay() < getCurrentDay();
        } else return false;
    }

    /**
     * Method which decides if a ToDoItem object falls on today's date.
     *
     * @param toDoItem The ToDoItem object.
     * @return boolean
     */
    private boolean isTodaysDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {
            return toDoItem.getYear() == getCurrentYear() && toDoItem.getMonth() == getCurrentMonth() && toDoItem.getDay() == getCurrentDay();
        } else return false;
    }

    /**
     * Method which decides if a ToDoItem object falls on tomorrows date.
     *
     * @param toDoItem The ToDoItem object.
     * @return boolean True if date is today's date.
     */
    private boolean isTomorrowsDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {
            return toDoItem.getYear() == getCurrentYear() && toDoItem.getMonth() == getCurrentMonth() && toDoItem.getDay() == getCurrentDay() + 1;
        } else return false;
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
            drawerList.setItemChecked(position, true);
            filter(position);
            drawerLayout.closeDrawer(drawerList);
        }

        /**
         * Method which applies user selected filter.
         *
         * @param position The filter selected
         */
        private void filter(int position) {

            linearLayoutToday.removeAllViews();
            linearLayoutTomorrow.removeAllViews();
            linearLayoutFuture.removeAllViews();
            linearLayoutSomeday.removeAllViews();
            linearLayoutOverdue.removeAllViews();

            if (position == 0) { // No Filter
                for (ToDoItem toDoItem : toDoList) {
                    addItem(toDoItem);
                }
                setTitle("Simple Do");
            } else if (position == 1) { //Only show high priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals("High")) {
                        addItem(toDoItem);
                    }
                }
                setTitle("High Priority");
            } else if (position == 2) { //Only show medium priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals("Medium")) {
                        addItem(toDoItem);
                    }
                }
                setTitle("Medium Priority");
            } else if (position == 3) { //Only show low priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals("Low")) {
                        addItem(toDoItem);
                    }
                }
                setTitle("Low Priority");
            } else if (position == 4) { //Only show not completed items
                for (ToDoItem toDoItem : toDoList) {
                    if (!toDoItem.isComplete()) {
                        addItem(toDoItem);
                    }
                }
                setTitle("Not Completed");
            } else if (position == 5) { //Filter by work
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getGroup().equals("Work")) {
                        addItem(toDoItem);
                    }
                }
                setTitle("Work");
            } else if (position == 6) { //Filter by personal
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getGroup().equals("Personal")) {
                        addItem(toDoItem);
                    }
                }
                setTitle("Personal");
            }
        }
    }
}

