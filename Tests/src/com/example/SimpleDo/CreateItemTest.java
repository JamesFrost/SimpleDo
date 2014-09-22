package com.example.SimpleDo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.*;

/**
 * Test class for CreateItem
 * Created by James Frost on 15/06/2014.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class CreateItemTest extends ActivityInstrumentationTestCase2<CreateItem> {

    private CreateItem createItem;
    private Button doneButton;
    private ToggleButton toggleButtonDate;
    private ToggleButton toggleButtonTime;
    private ToggleButton toggleButtonReminder;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView textViewTime;
    private EditText toDoItemName;
    private Spinner groupSpinner;
    private Spinner prioritySpinner;
    private RelativeLayout relativeLayout;
    private TextView textViewReminder;

    @SuppressLint("NewApi")
    public CreateItemTest() {
        super(CreateItem.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        createItem = getActivity();

        relativeLayout = (RelativeLayout) createItem.findViewById(R.id.relativeLayout);
        doneButton = (Button) createItem.findViewById(R.id.button);

        toDoItemName = (EditText) createItem.findViewById(R.id.toDoItemName);
        groupSpinner = (Spinner) createItem.findViewById(R.id.groupSpinner);
        prioritySpinner = (Spinner) createItem.findViewById(R.id.prioritySpinner);

        toggleButtonDate = (ToggleButton) createItem.findViewById(R.id.somedayToggleButton);
        toggleButtonTime = (ToggleButton) createItem.findViewById(R.id.timeToggleButton);
        datePicker = (DatePicker) createItem.findViewById(R.id.datePicker);
        timePicker = (TimePicker) createItem.findViewById(R.id.timePicker);
        textViewTime = (TextView) createItem.findViewById(R.id.TimeText);
    }

    public void testPreconditions() throws Exception {
        System.out.println("Testing preconditions...");

//        assertNotNull("toggleButtonTime is null", toggleButtonTime);
        assertNotNull("createItem is null", createItem);
        assertNotNull("toggleButtonDate is null", toggleButtonDate);
        assertNotNull("relativeLayout is null", relativeLayout);
        assertNotNull("doneButton is null", doneButton);
        assertNotNull("toDoItemName is null", toDoItemName);
//        assertNotNull("datePicker is null", datePicker);
//        assertNotNull("timePicker is null", timePicker);
//        assertNotNull("textViewTime null", textViewTime);
    }

    public void testInitialVisibilityOfViews() throws Exception {
        assertTrue(View.VISIBLE == toggleButtonDate.getVisibility());
        assertTrue(View.VISIBLE == toDoItemName.getVisibility());
        assertTrue(View.VISIBLE == groupSpinner.getVisibility());
        assertTrue(View.VISIBLE == prioritySpinner.getVisibility());
        assertTrue(relativeLayout.getChildCount() == 0);
    }

    public void testDateToggleButton() throws Exception {
        TouchUtils.clickView(this, toggleButtonDate);

        toggleButtonTime = (ToggleButton) createItem.findViewById(R.id.timeToggleButton);
        datePicker = (DatePicker) createItem.findViewById(R.id.datePicker);
        textViewTime = (TextView) createItem.findViewById(R.id.TimeText);

        assertTrue(View.VISIBLE == textViewTime.getVisibility());
        assertTrue(View.VISIBLE == toggleButtonTime.getVisibility());
        assertTrue(View.VISIBLE == datePicker.getVisibility());
        assertTrue(relativeLayout.getChildCount() == 3);
    }

    public void testTimeToggleButton() throws Exception {
        TouchUtils.clickView(this, toggleButtonDate);
        toggleButtonTime = (ToggleButton) createItem.findViewById(R.id.timeToggleButton);
        TouchUtils.clickView(this, toggleButtonTime);

        textViewTime = (TextView) createItem.findViewById(R.id.TimeText);
        toggleButtonTime = (ToggleButton) createItem.findViewById(R.id.timeToggleButton);
        datePicker = (DatePicker) createItem.findViewById(R.id.datePicker);
        timePicker = (TimePicker) createItem.findViewById(R.id.timePicker);
        textViewReminder = (TextView) createItem.findViewById(R.id.reminderText);
        toggleButtonReminder = (ToggleButton) createItem.findViewById(R.id.reminderToggleButton);

        assertTrue(View.VISIBLE == textViewTime.getVisibility());
        assertTrue(View.VISIBLE == toggleButtonTime.getVisibility());
        assertTrue(View.VISIBLE == datePicker.getVisibility());
        assertTrue(View.VISIBLE == timePicker.getVisibility());
        assertTrue(View.VISIBLE == textViewReminder.getVisibility());
        assertTrue(View.VISIBLE == toggleButtonReminder.getVisibility());
        assertEquals(relativeLayout.getChildCount(), 6);
    }

    public void testDoneButton_toDoItemNameNotNullAndToast() throws Exception {
        assertTrue(toDoItemName.getText().toString().equals(""));
        TouchUtils.clickView(this, doneButton);
    }
}
