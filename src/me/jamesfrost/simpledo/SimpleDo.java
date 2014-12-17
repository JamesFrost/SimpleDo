package me.jamesfrost.simpledo;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
public class SimpleDo extends Activity implements Constants, DeleteDialog.NoticeDialogListener {

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
    private CheckBox checkBoxToBeDeleted;
    private ReminderHelper reminderHelper;
    private ToDoItemSorter toDoItemSorter;

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

        new SimpleEula(this).show();

        Context mContext;
        mContext = getApplicationContext();
        assert mContext != null;
        textViewNoItems = new TextView(mContext);
        textViewNoItems.setText(noItemsText);
        textViewNoItems.setTextSize(17);
        textViewNoItems.setTypeface(null, Typeface.ITALIC);

        toDoItemSorter = new ToDoItemSorter();

        formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        reminderHelper = new ReminderHelper();

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
                        intent.putExtra(KEY_NAME, toDoName.getText().toString().trim());
                        intent.putExtra(KEY_TODOLIST, toDoList);
                        toDoName.setText("");
                        startActivityForResult(intent, REQUEST_CODE_ADD_ITEM);
                }
            }
        };
        go.setOnClickListener(droidTapListener);

        toDoList = new ArrayList<ToDoItem>();
        toDoList = dataSource.getAllItems();
        toDoList = toDoItemSorter.sortToDoList(toDoList);
        drawerItemClickListener.filter(drawerList.getCheckedItemPosition());

        getOverflowMenu();
    }

    /**
     * Makes the action overflow button visible, even on devices with a menu button.
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

    /**
     * Sorts an ArrayList of ToDoItems by date using a bubble sort algorithm.
     *
     * @param toDoList The list to sort
     */
//    private void sortToDoList(ArrayList<ToDoItem> toDoList) {
//        for (int i = toDoList.size() - 1; i >= 0; i--) {
//            for (int j = 0; j < i; j++) {
//                if (toDoList.get(j).getDate() != null && toDoList.get(j + 1).getDate() != null) {
//                    if (toDoList.get(j).getDate() instanceof LocalDateTime && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
//                        if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
//                            ToDoItem temp = toDoList.get(j);
//                            toDoList.set(j, toDoList.get(j + 1));
//                            toDoList.set(j + 1, temp);
//                        }
//                    } else if (!(toDoList.get(j).getDate() instanceof LocalDateTime) && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
//                        if (toDoList.get(j).getDate().isAfter(((LocalDateTime) toDoList.get(j + 1).getDate()).toLocalDate())) {
//                            ToDoItem temp = toDoList.get(j);
//                            toDoList.set(j, toDoList.get(j + 1));
//                            toDoList.set(j + 1, temp);
//                        } else if (toDoList.get(j).getDate().isEqual(((LocalDateTime) toDoList.get(j + 1).getDate()).toLocalDate())) {
//                            ToDoItem temp = toDoList.get(j);
//                            toDoList.set(j, toDoList.get(j + 1));
//                            toDoList.set(j + 1, temp);
//                        }
//                    } else if (toDoList.get(j).getDate() instanceof LocalDate && toDoList.get(j + 1).getDate() instanceof LocalDate) {
//                        if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
//                            ToDoItem temp = toDoList.get(j);
//                            toDoList.set(j, toDoList.get(j + 1));
//                            toDoList.set(j + 1, temp);
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * @param requestCode
     * @param resultCode  100 for adding a new item, 200 for editing an item and 300 for quick reschedule
     * @param data        Date sent from activity returning a result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            Bundle bundle = data.getExtras();

            if (resultCode == REQUEST_CODE_ADD_ITEM && bundle != null) { //Add item result
                ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra(KEY_NEWTODOITEM);
                dataSource.createItem(toDoItem);

                if (bundle.getBoolean(KEY_REMINDER)) {
                    reminderHelper.addReminder(toDoItem, getContentResolver());
                }

                toDoList.add(toDoItem);
                toDoList = toDoItemSorter.sortToDoList(toDoList);
                drawerItemClickListener.filter(drawerList.getCheckedItemPosition());

            } else if (resultCode == REQUEST_CODE_EDIT_ITEM && bundle != null) { //Edit item result
                ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra(KEY_NEWTODOITEM);
                ToDoItem oldToDoItem = (ToDoItem) bundle.get(KEY_OLDTODOITEM);

                if (bundle.getBoolean(KEY_REMINDER) != oldToDoItem.isReminder()) {
                    if (bundle.getBoolean(KEY_REMINDER))
                        reminderHelper.addReminder(toDoItem, getContentResolver());
                    else
                        reminderHelper.deleteReminder(toDoItem, getContentResolver());
                }

                toDoList.remove(oldToDoItem);
                toDoList.add(toDoItem);
                toDoList = toDoItemSorter.sortToDoList(toDoList);

                dataSource.deleteItem(oldToDoItem);
                dataSource.createItem(toDoItem);
                drawerItemClickListener.filter(drawerList.getCheckedItemPosition());

            } else if (resultCode == REQUEST_CODE_QUICK_RESCHEDULE && bundle != null) { //Quick Reschedule
                ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra(KEY_NEWTODOITEM);
                ToDoItem oldToDoItem = (ToDoItem) bundle.get(KEY_OLDTODOITEM);

                assert toDoItem != null;
                if (oldToDoItem.isReminder() && toDoItem.getDate() != null && !(toDoItem.getDate() instanceof LocalDateTime)) {
                    reminderHelper.deleteReminder(oldToDoItem, getContentResolver());
                } else
                    toDoItem.setEventID(oldToDoItem.getEventID());

                toDoList.remove(oldToDoItem);
                toDoList.add(toDoItem);
                toDoList = toDoItemSorter.sortToDoList(toDoList);
                toDoList = toDoItemSorter.sortToDoList(toDoList);

                dataSource.deleteItem(oldToDoItem);
                dataSource.createItem(toDoItem);
                drawerItemClickListener.filter(drawerList.getCheckedItemPosition());

            }
        }
    }

    /**
     * Adds a checkbox to the relevant layout.
     *
     * @param toDoItem The ToDoItem to add
     */
    private void addItem(final ToDoItem toDoItem) {
        final CheckBox ch = new CheckBox(this);

        final DateHelper dateHelper = new DateHelper();

        ch.setTextColor(Color.LTGRAY);
        registerForContextMenu(ch);

        if (drawerList.getCheckedItemPosition() != DrawerItemClickListener.POSITION_OF_COMPLETED) {
            if (toDoItem.getDate() == null && !toDoItem.isComplete()) {
                ch.setText(toDoItem.getName());
                linearLayoutSomeday.addView(ch);
            } else if (dateHelper.isTodaysDate(toDoItem)) {
                if (toDoItem.getDate() instanceof LocalDateTime)
                    ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxTime));
                else ch.setText(toDoItem.getName());
                linearLayoutToday.addView(ch);
                if (toDoItem.isComplete()) {
                    ch.setChecked(true);
                }

            } else if (dateHelper.isTomorrowsDate(toDoItem)) {
                if (toDoItem.getDate() instanceof LocalDateTime)
                    ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxTime));
                else ch.setText(toDoItem.getName());
                linearLayoutTomorrow.addView(ch);
                if (toDoItem.isComplete()) {
                    ch.setChecked(true);
                }

            } else if (dateHelper.isOverDue(toDoItem) && !toDoItem.isComplete()) {
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
        } else { //drawerList.getCheckedItemPosition() == 4
            if (toDoItem.getDate() == null && toDoItem.isComplete()) {
                ch.setText(toDoItem.getName());
                linearLayoutSomeday.addView(ch);
                ch.setChecked(true);
            } else if (dateHelper.isTodaysDate(toDoItem) && toDoItem.isComplete()) {
                if (toDoItem.getDate() instanceof LocalDateTime)
                    ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxTime));
                else ch.setText(toDoItem.getName());
                linearLayoutToday.addView(ch);
                if (toDoItem.isComplete()) {
                    ch.setChecked(true);
                }

            } else if (dateHelper.isTomorrowsDate(toDoItem) && toDoItem.isComplete()) {
                if (toDoItem.getDate() instanceof LocalDateTime)
                    ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxTime));
                else ch.setText(toDoItem.getName());
                linearLayoutTomorrow.addView(ch);
                if (toDoItem.isComplete()) {
                    ch.setChecked(true);
                }

            } else if (toDoItem.isComplete() && !dateHelper.isOverDue(toDoItem)) {
                if (toDoItem.getDate() instanceof LocalDateTime)
                    ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxDateTime));
                else ch.setText(toDoItem.getName() + " - " + toDoItem.getDate().toString(formatterCheckBoxDate));
                linearLayoutFuture.addView(ch);
                ch.setChecked(true);
            }
        }
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toDoItem.setComplete(((CheckBox) view).isChecked());
                dataSource.updateItemCompleteStatus(toDoItem);

                if (dateHelper.isOverDue(toDoItem)) {
                    linearLayoutOverdue.removeView(ch);
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                } else if (toDoItem.getDate() == null) {
                    linearLayoutSomeday.removeView(ch);
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                } else if (!dateHelper.isTodaysDate(toDoItem) && !dateHelper.isTomorrowsDate(toDoItem)) {
                    linearLayoutFuture.removeView(ch);
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                }

                if (toDoItem.isComplete() && toDoItem.isReminder()) {
                    reminderHelper.deleteReminder(toDoItem, getContentResolver());
                } else if (toDoItem.isReminder()) {
                    reminderHelper.addReminder(toDoItem, getContentResolver());
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

        switch (item.getItemId()) {
            case R.id.edit:
                for (ToDoItem a : toDoList) {

                    if (mLastViewTouched.getText().toString().contains(a.getName())) {

                        if ((a.getDate() instanceof LocalDateTime && (mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxDateTime)) || ((mLastViewTouched.getParent() == linearLayoutToday || mLastViewTouched.getParent() == linearLayoutTomorrow) && mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxTime))))) ||
                                (a.getDate() instanceof LocalDate && (mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxDate)) || (mLastViewTouched.getParent() == linearLayoutToday || mLastViewTouched.getParent() == linearLayoutTomorrow))) ||
                                (a.getDate() == null)) {

                            Intent intent = new Intent(SimpleDo.this, EditItem.class);
                            intent.putExtra(KEY_NAME, a.getName());
                            intent.putExtra(KEY_GROUP, a.getGroup());
                            intent.putExtra(KEY_PRIORITY, a.getPriority());
                            intent.putExtra(KEY_REMINDER, a.isReminder());
                            if (a.getDate() != null)
                                intent.putExtra(KEY_DATE, a.getDate().toString(formatter));
                            intent.putExtra(KEY_OLDTODOITEM, a);
                            intent.putExtra(KEY_TODOLIST, toDoList);
                            startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM);
                            break;
                        }
                    }
                }
                return true;
            case R.id.quick_reschedule:
                for (ToDoItem a : toDoList) {
                    if (mLastViewTouched.getText().toString().contains(a.getName())) {

                        if ((a.getDate() instanceof LocalDateTime && (mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxDateTime)) || ((mLastViewTouched.getParent() == linearLayoutToday || mLastViewTouched.getParent() == linearLayoutTomorrow) && mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxTime))))) ||
                                (a.getDate() instanceof LocalDate && (mLastViewTouched.getText().toString().contains(a.getDate().toString(formatterCheckBoxDate)) || (mLastViewTouched.getParent() == linearLayoutToday || mLastViewTouched.getParent() == linearLayoutTomorrow))) ||
                                (a.getDate() == null)) {

                            Intent intent = new Intent(SimpleDo.this, QuickReschedule.class);
                            if (a.getDate() != null)
                                intent.putExtra(KEY_DATE, a.getDate().toString(formatter));
                            intent.putExtra(KEY_OLDTODOITEM, a);
                            intent.putExtra(KEY_TODOLIST, toDoList);
                            startActivityForResult(intent, REQUEST_CODE_QUICK_RESCHEDULE);
                            break;
                        }
                    }
                }
                return true;
            case R.id.delete:

                checkBoxToBeDeleted = mLastViewTouched;
                DialogFragment newFragment = new DeleteDialog();
                test = newFragment.getId();
                Bundle bundle = new Bundle();
                bundle.putString(KEY_CHECKBOXTOBODELETEDNAME, checkBoxToBeDeleted.getText().toString());

                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "delete");

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    int test;

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
//            case R.id.help:
//                //start help activity
//                break;
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

    public ListView getDrawerList() {
        return drawerList;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Iterator<ToDoItem> setIterator = toDoList.iterator();
        while (setIterator.hasNext()) {
            ToDoItem currentElement = setIterator.next();
            if (checkBoxToBeDeleted.getText().toString().contains(currentElement.getName())) {

                if ((currentElement.getDate() instanceof LocalDateTime && (checkBoxToBeDeleted.getText().toString().contains(currentElement.getDate().toString(formatterCheckBoxDateTime)) || ((checkBoxToBeDeleted.getParent() == linearLayoutToday || checkBoxToBeDeleted.getParent() == linearLayoutTomorrow) && checkBoxToBeDeleted.getText().toString().contains(currentElement.getDate().toString(formatterCheckBoxTime))))) ||
                        (currentElement.getDate() instanceof LocalDate && (checkBoxToBeDeleted.getText().toString().contains(currentElement.getDate().toString(formatterCheckBoxDate)) || (checkBoxToBeDeleted.getParent() == linearLayoutToday || checkBoxToBeDeleted.getParent() == linearLayoutTomorrow))) ||
                        (currentElement.getDate() == null)) {

                    dataSource.deleteItem(currentElement);
                    setIterator.remove();
                    drawerItemClickListener.filter(drawerList.getCheckedItemPosition());
                    break;
                }
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        private static final String FILTER_TITLE_NO_FILTER = "SimpleDo";
        private static final String FILTER_TITLE_HIGH_PRIORITY = "High Priority";
        private static final String FILTER_TITLE_MEDIUM_PRIORITY = "Medium Priority";
        private static final String FILTER_TITLE_LOW_PRIORITY = "Low Priority";
        private static final String FILTER_TITLE_NO_PRIORITY = "No Priority";
        private static final String FILTER_TITLE_NOT_COMPLETED = "Not Completed";
        private static final String FILTER_TITLE_COMPLETED = "Completed";

        public static final String FILTER_WORK = "Work";
        private static final String FILTER_PERSONAL = "Personal";
        private static final String FILTER_SCHOOL = "School";
        private static final String FILTER_HIGH_PRIORITY = "High";
        private static final String FILTER_MEDIUM_PRIORITY = "Medium";
        private static final String FILTER_LOW_PRIORITY = "Low";
        private static final String FILTER_NO_PRIORITY = "Not Set";
        private static final String FILTER_NO_GROUP = "No Group";

        private static final int POSITION_OF_NO_FILTER = 0;
        private static final int POSITION_OF_HIGH_PRIORITY = 1;
        private static final int POSITION_OF_MEDIUM_PRIORITY = 2;
        private static final int POSITION_OF_LOW_PRIORITY = 3;
        private static final int POSITION_OF_NO_PRIORITY = 4;
        public static final int POSITION_OF_COMPLETED = 5;
        private static final int POSITION_OF_NOT_COMPLETE = 6;
        private static final int POSITION_OF_WORK = 7;
        private static final int POSITION_OF_PERSONAL = 8;
        private static final int POSITION_OF_SCHOOL = 9;
        private static final int POSITION_OF_NO_GROUP = 10;

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
         * Applies the user selected filter.
         *
         * @param position The position of the filter selected
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
            } else if (position == POSITION_OF_NO_PRIORITY) {
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getPriority().equals(FILTER_NO_PRIORITY)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_TITLE_NO_PRIORITY);
            } else if (position == POSITION_OF_COMPLETED) {
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.isComplete()) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_TITLE_COMPLETED);
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
            } else if (position == POSITION_OF_SCHOOL) { //Filter by school
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getGroup().equals(FILTER_SCHOOL)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_SCHOOL);
            } else if (position == POSITION_OF_NO_GROUP) { //Filter by no grou[
                for (ToDoItem toDoItem : toDoList) {
                    if (toDoItem.getGroup().equals(FILTER_NO_GROUP)) {
                        addItem(toDoItem);
                    }
                }
                setTitle(FILTER_NO_GROUP);
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