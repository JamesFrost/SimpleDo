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

import java.util.Date;

import static android.R.style.Theme_Holo_InputMethod;

/**
 * Activity where the user enters the relevant information for a To Do item.
 *
 * @author James Frost
 */
public class CreateItem extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText toDoItemName;
    private Button button;
    private View.OnClickListener droidTapListener;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner groupSpinner;
    private Spinner prioritySpinner;
    private ToggleButton dateToggleButton;
    private ToggleButton timeToggleButton;
    private ToggleButton reminderToggleButton;
    private RelativeLayout relativeLayout;
    private TextView date;
    private TextView time;
    private TextView reminder;

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
        button = (Button) findViewById(R.id.button);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        dateToggleButton = (ToggleButton) findViewById(R.id.somedayToggleButton);
        timeToggleButton = (ToggleButton) findViewById(R.id.timeToggleButton);
        reminderToggleButton = (ToggleButton) findViewById(R.id.reminderToggleButton);
        date = (TextView) findViewById(R.id.DateText);
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

        droidTapListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button:
                        if (!toDoItemName.getText().toString().matches("")) { //if the name field is not empty
                            Intent intent = new Intent(CreateItem.this, SimpleDo.class);
                            intent.putExtra("newToDoItem", new ToDoItem(toDoItemName.getText().toString().trim(), createDate(), groupSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), timeToggleButton.isChecked()));
                            intent.putExtra("reminder", reminderToggleButton.isChecked());
                            setResult(100, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "The task name is empty!", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        };
        button.setOnClickListener(droidTapListener);
        dateToggleButton.setOnClickListener(droidTapListener);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toDoItemName.setText(bundle.getString("toDoItemName"));
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
     * A method that creates a date object from the user selected date and time information from the date and time pickers.
     *
     * @return date
     */
    private BaseLocal createDate() {
        if (dateToggleButton.isChecked()) {
            if(timeToggleButton.isChecked()) {
                LocalDateTime ldt = new LocalDateTime(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                return ldt;
            } else {
                LocalDate ld = new LocalDate(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                System.out.println("DatePickerMonth: " + datePicker.getMonth());
                return ld;
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
