package com.example.SimpleDo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.Date;

/**
 * Activity where the user enters the relevant information for a To Do item.
 *
 * Created by James on 23/05/2014.
 */
public class CreateItem extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText toDoItemName;
    private Button button;
    private View.OnClickListener droidTapListener;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner groupSpinner;
    private Spinner prioritySpinner;
    private ToggleButton somedayToggleButton;
    private LinearLayout linearLayout;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo_item);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        toDoItemName = (EditText) findViewById(R.id.toDoItemName);
        button = (Button) findViewById(R.id.button);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        somedayToggleButton = (ToggleButton) findViewById(R.id.somedayToggleButton);

        somedayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearLayout.removeView(datePicker);
                    linearLayout.removeView(timePicker);
                } else {
                    linearLayout.addView(datePicker);
                    linearLayout.addView(timePicker);
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
                        Intent intent = new Intent(CreateItem.this, SimpleDo.class);
                        intent.putExtra("newToDoItem", new ToDoItem(toDoItemName.getText().toString().trim(), createDate(), groupSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString()));
//                        alarm.setOnetimeTimer(getApplicationContext());
                        setResult(100, intent);
                        finish();
                }
            }
        };
        button.setOnClickListener(droidTapListener);
        somedayToggleButton.setOnClickListener(droidTapListener);

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
    private Date createDate() {
        if (!somedayToggleButton.isChecked()) {
            Date date = new Date();
            date.setSeconds(0);
            date.setMinutes(timePicker.getCurrentMinute());
            date.setHours(timePicker.getCurrentHour());
            date.setDate(datePicker.getDayOfMonth());
            date.setMonth(datePicker.getMonth());
            date.setYear(datePicker.getYear());
            return date;
        } else return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
