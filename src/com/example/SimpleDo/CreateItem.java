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

import java.util.ArrayList;

/**
 * Activity where the user creates a ToDoItem.
 *
 * @author James Frost
 */
public class CreateItem extends Activity implements AdapterView.OnItemSelectedListener, Constants {

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
    private ArrayList<ToDoItem> toDoList;

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

        View.OnClickListener droidTapListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button:

                        boolean notFailed = true;

                        if (toDoItemName.getText().toString().matches("")) { //if the name field is empty
                            Toast.makeText(getApplicationContext(), TOAST_EMPTY_NAME_WARNING, Toast.LENGTH_SHORT).show();
                            notFailed = false;
                        } else if (toDoItemName.getText().toString().contains("-")) {
                            Toast.makeText(getApplicationContext(), TOAST_HYPHEN_WARNING, Toast.LENGTH_SHORT).show();
                            notFailed = false;
                        }

                        if (notFailed) {
                            for (ToDoItem a : toDoList) {
                                if (a.getName().equals(toDoItemName.getText().toString().trim())) {
                                    Toast.makeText(getApplicationContext(), TOAST_DUPLICATE_ITEM_WARNING, Toast.LENGTH_SHORT).show();
                                    notFailed = false;
                                    break;
                                }
                            }
                        }

                        if (notFailed) {
                            Intent intent = new Intent(CreateItem.this, SimpleDo.class);
                            intent.putExtra(KEY_NEWTODOITEM, new ToDoItem(toDoItemName.getText().toString().trim(), createDate(), groupSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), timeToggleButton.isChecked()));
                            intent.putExtra(KEY_REMINDER, reminderToggleButton.isChecked());
                            setResult(REQUEST_CODE_ADD_ITEM, intent);
                            finish();
                        }

                }
            }
        };
        button.setOnClickListener(droidTapListener);
        dateToggleButton.setOnClickListener(droidTapListener);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toDoItemName.setText(bundle.getString(KEY_NAME));
            toDoList = (ArrayList<ToDoItem>) bundle.get(KEY_TODOLIST);
        }
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
