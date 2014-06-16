package com.example.SimpleDo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Main Activity.
 *
 * @author James Frost
 */
public class SimpleDo extends Activity {

    private RelativeLayout relativeLayout;
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

    /**
     * Class which adds a checkbox to the relevant layout and links it to the correct toDoItem object.
     *
     * @param toDoItem The ToDoItem to add.
     */
    private void addItem(final ToDoItem toDoItem) {
        now = Calendar.getInstance();
        toDoList.add(toDoItem);
        CheckBox ch = new CheckBox(this);
        Calendar cal = Calendar.getInstance();

        //Set reminder using the phones native calendar
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
//        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", toDoItem.getName());
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

            if (position == 0) { //Only show high priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals("High")) {
                        CheckBox ch = new CheckBox(getApplicationContext());
                        ch.setText(toDoItem.getName() + " " + toDoItem.getDueTime());
                        if (toDoItem.getDate() == null) {
                            linearLayoutSomeday.addView(ch);
                        } else if (isTodaysDate(toDoItem)) {
                            linearLayoutToday.addView(ch);
                        } else if (isTomorrowsDate(toDoItem)) {
                            linearLayoutTomorrow.addView(ch);
                        } else {
                            linearLayoutFuture.addView(ch);
                        }
                    }
                }
            } else if (position == 1) { //Only show medium priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals("Medium")) {
                        CheckBox ch = new CheckBox(getApplicationContext());
                        ch.setText(toDoItem.getName() + " " + toDoItem.getDueTime());
                        if (toDoItem.getDate() == null) {
                            linearLayoutSomeday.addView(ch);
                        } else if (isTodaysDate(toDoItem)) {
                            linearLayoutToday.addView(ch);
                        } else if (isTomorrowsDate(toDoItem)) {
                            linearLayoutTomorrow.addView(ch);
                        } else {
                            linearLayoutFuture.addView(ch);
                        }
                    }
                }
            } else if (position == 2) { //Only show low priority
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals("Low")) {
                        CheckBox ch = new CheckBox(getApplicationContext());
                        ch.setText(toDoItem.getName() + " " + toDoItem.getDueTime());
                        if (toDoItem.getDate() == null) {
                            linearLayoutSomeday.addView(ch);
                        } else if (isTodaysDate(toDoItem)) {
                            linearLayoutToday.addView(ch);
                        } else if (isTomorrowsDate(toDoItem)) {
                            linearLayoutTomorrow.addView(ch);
                        } else {
                            linearLayoutFuture.addView(ch);
                        }
                    }
                }
            } else if (position == 3) { //Only show not completed items
                for (ToDoItem toDoItem : toDoList) {
                    if (!toDoItem.isComplete()) {
                        CheckBox ch = new CheckBox(getApplicationContext());
                        ch.setText(toDoItem.getName() + " " + toDoItem.getDueTime());
                        if (toDoItem.getDate() == null) {
                            linearLayoutSomeday.addView(ch);
                        } else if (isTodaysDate(toDoItem)) {
                            linearLayoutToday.addView(ch);
                        } else if (isTomorrowsDate(toDoItem)) {
                            linearLayoutTomorrow.addView(ch);
                        } else {
                            linearLayoutFuture.addView(ch);
                        }
                    }
                }
            } else if (position == 4) { //Filter by group

            } else if (position == 5) { //Only show overdue tasks

            }
        }
    }
}

