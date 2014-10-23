package me.jamesfrost.simpledo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.base.BaseLocal;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Activity for quick rescheduling of ToDoItems.
 * <p/>
 * Created by James Frost on 18/09/2014.
 */
public class QuickReschedule extends Activity implements Constants {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private ToggleButton dateToggleButton;
    private ToggleButton timeToggleButton;
    private RelativeLayout relativeLayout;
    private TextView timeTextView;
    private ToDoItem oldToDoItem;
    private ArrayList<ToDoItem> toDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_reschedule);

        timeTextView = (TextView) findViewById(R.id.TimeText);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        dateToggleButton = (ToggleButton) findViewById(R.id.somedayToggleButton);
        timeToggleButton = (ToggleButton) findViewById(R.id.timeToggleButton);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
        Button buttonDone = (Button) findViewById(R.id.buttonDone);

        relativeLayout.removeView(datePicker);
        relativeLayout.removeView(timeToggleButton);
        relativeLayout.removeView(timePicker);
        relativeLayout.removeView(timeTextView);

        dateToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    relativeLayout.removeView(datePicker);
                    relativeLayout.removeView(timeToggleButton);
                    relativeLayout.removeView(timeTextView);
                    timeToggleButton.setChecked(false);
                } else {
                    relativeLayout.addView(datePicker);
                    relativeLayout.addView(timeToggleButton);
                    relativeLayout.addView(timeTextView);
                }
            }
        });

        timeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    relativeLayout.removeView(timePicker);
                } else {
                    relativeLayout.addView(timePicker);
                }
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            toDoList = (ArrayList<ToDoItem>) bundle.get(KEY_TODOLIST);

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

            oldToDoItem = (ToDoItem) bundle.get(KEY_OLDTODOITEM);

        }

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuickReschedule.this, SimpleDo.class);
                intent.putExtra(KEY_NEWTODOITEM, new ToDoItem(oldToDoItem.getName(), createDate(), oldToDoItem.getGroup(), oldToDoItem.getPriority(), timeToggleButton.isChecked()));
                intent.putExtra(KEY_OLDTODOITEM, oldToDoItem);
                setResult(REQUEST_CODE_QUICK_RESCHEDULE, intent);
                finish();
            }
        });
    }

    /**
     * Creates a date object using the user selected date and time from the date and time pickers.
     * Null if no date selected.
     *
     * @return a date object with user selected date/time or null if no date selected
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
}
