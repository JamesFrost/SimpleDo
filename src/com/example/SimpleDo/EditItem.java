package com.example.SimpleDo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.base.BaseLocal;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Activity where the user can edit ToDoItems.
 *
 * Created by James Frost on 17/09/2014.
 */
public class EditItem extends Activity implements AdapterView.OnItemSelectedListener, Constants {

    private EditText toDoItemName;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner groupSpinner;
    private Spinner prioritySpinner;
    private ToggleButton dateToggleButton;
    private ToggleButton timeToggleButton;
    private ToggleButton reminderToggleButton;
    private RelativeLayout relativeLayout;
    private TextView time;
    private TextView reminder;
    private ToDoItem oldToDoItem;
    private ArrayList<ToDoItem> toDoList;
    private static final int GROUP_NO_GROUP_INDEX = 0;
    private static final int GROUP_WORK_INDEX = 1;
    private static final int GROUP_PERSONAL_INDEX = 2;
    private static final int PRIORITY_LOW_INDEX = 1;
    private static final int PRIORITY_MEDIUM_INDEX = 2;
    private static final int PRIORITY_HIGH_INDEX = 3;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo_item);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        toDoItemName = (EditText) findViewById(R.id.toDoItemName);
        Button button = (Button) findViewById(R.id.button);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        dateToggleButton = (ToggleButton) findViewById(R.id.somedayToggleButton);
        timeToggleButton = (ToggleButton) findViewById(R.id.timeToggleButton);
        reminderToggleButton = (ToggleButton) findViewById(R.id.reminderToggleButton);
        time = (TextView) findViewById(R.id.TimeText);
        reminder = (TextView) findViewById(R.id.reminderText);

        relativeLayout.removeView(datePicker);
        relativeLayout.removeView(timeToggleButton);
        relativeLayout.removeView(timePicker);
        relativeLayout.removeView(reminderToggleButton);
        relativeLayout.removeView(reminder);
        relativeLayout.removeView(time);

        dateToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    relativeLayout.removeView(datePicker);
                    relativeLayout.removeView(timeToggleButton);
                    relativeLayout.removeView(time);
                    timeToggleButton.setChecked(false);
                } else {
                    relativeLayout.addView(datePicker);
                    relativeLayout.addView(timeToggleButton);
                    relativeLayout.addView(time);
                }
            }
        });

        timeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    relativeLayout.removeView(timePicker);
                    relativeLayout.removeView(reminderToggleButton);
                    relativeLayout.removeView(reminder);
                } else {
                    relativeLayout.addView(timePicker);
                    relativeLayout.addView(reminderToggleButton);
                    relativeLayout.addView(reminder);
                }
            }
        });

        groupSpinner = (Spinner) findViewById(R.id.groupSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.group_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);
        groupSpinner.setOnItemSelectedListener(this);

        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> adapterTwo = ArrayAdapter.createFromResource(this, R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapterTwo);
        prioritySpinner.setOnItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toDoItemName.setText(bundle.getString(KEY_NAME));

            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            BaseLocal date;

            if (bundle.getString(KEY_DATE) == (null)) {
                date = null;
            } else {
                String split[] = bundle.getString(KEY_DATE).split(":");
                if (split[split.length - 1].equals("00")) {
                    date = formatter.parseLocalDateTime(bundle.getString(KEY_DATE));
                } else {
                    split = bundle.getString(KEY_DATE).split(" ");
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

            if (date instanceof LocalDateTime) {
                dateToggleButton.setChecked(true);
                timeToggleButton.setChecked(true);
                datePicker.updateDate(((LocalDateTime) date).getYear(), ((LocalDateTime) date).getMonthOfYear() - 1, ((LocalDateTime) date).getDayOfMonth());
                timePicker.setCurrentHour(((LocalDateTime) date).getHourOfDay());
                timePicker.setCurrentMinute(((LocalDateTime) date).getMinuteOfHour());
            } else if (date instanceof LocalDate) {
                dateToggleButton.setChecked(true);
                datePicker.updateDate(((LocalDate) date).getYear(), ((LocalDate) date).getMonthOfYear() - 1, ((LocalDate) date).getDayOfMonth());
            }

            reminderToggleButton.setChecked(bundle.getBoolean(KEY_REMINDER));

            if (bundle.getString(KEY_GROUP).equals("No Group")) //isn't no group default?
                groupSpinner.setSelection(GROUP_NO_GROUP_INDEX);
            else if (bundle.getString(KEY_GROUP).equals("Work"))
                groupSpinner.setSelection(GROUP_WORK_INDEX);
            else if (bundle.getString(KEY_GROUP).equals("Personal"))
                groupSpinner.setSelection(GROUP_PERSONAL_INDEX);

            if (bundle.getString(KEY_PRIORITY).equals("Low"))
                prioritySpinner.setSelection(PRIORITY_LOW_INDEX);
            else if (bundle.getString(KEY_PRIORITY).equals("Medium"))
                prioritySpinner.setSelection(PRIORITY_MEDIUM_INDEX);
            else if (bundle.getString(KEY_PRIORITY).equals("High"))
                prioritySpinner.setSelection(PRIORITY_HIGH_INDEX);

            oldToDoItem = (ToDoItem) bundle.get(KEY_OLDTODOITEM);
            toDoList = (ArrayList<ToDoItem>) bundle.get("toDoList");

        }

        View.OnClickListener droidTapListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button:

                        boolean notFailed = true;

                        if (toDoItemName.getText().toString().matches("")) { //if the name field is empty
                            Toast.makeText(getApplicationContext(), TOAST_EMPTY_NAME_WARNING, Toast.LENGTH_SHORT).show();
                            notFailed = false;
                        }

                        if (notFailed) {
                            for (ToDoItem a : toDoList) {
                                if (!(a.equals(oldToDoItem)) && a.getName().equals(toDoItemName.getText().toString().trim()) && ((a.getDate() == null && createDate() == null) || (a.getDate() != null && createDate() != null && a.getDate().isEqual(createDate())))) {
                                    Toast.makeText(getApplicationContext(), TOAST_DUPLICATE_ITEM_WARNING, Toast.LENGTH_SHORT).show();
                                    notFailed = false;
                                    break;
                                }
                            }
                        }

                        if (notFailed) {
                            Intent intent = new Intent(EditItem.this, SimpleDo.class);
                            intent.putExtra(KEY_NEWTODOITEM, new ToDoItem(toDoItemName.getText().toString().trim(), createDate(), groupSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), timeToggleButton.isChecked()));
                            intent.putExtra(KEY_REMINDER, reminderToggleButton.isChecked());
                            intent.putExtra(KEY_OLDTODOITEM, oldToDoItem);
                            setResult(200, intent);
                            finish();
                        }
                }
            }
        };
        button.setOnClickListener(droidTapListener);
        dateToggleButton.setOnClickListener(droidTapListener);
    }

    /**
     * Creates a date object using the user selected date and time from the date and time pickers.
     * Null if no date selected.
     *
     * @return a date object with user selected date/time
     */
    private BaseLocal createDate() {
        if (dateToggleButton.isChecked()) {
            if (timeToggleButton.isChecked()) {
                return new LocalDateTime(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
            } else {
                return new LocalDate(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
            }

        } else return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
