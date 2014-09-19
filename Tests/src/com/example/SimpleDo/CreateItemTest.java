package com.example.SimpleDo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.*;

/**
 * Test class for CreateItem
 * Created by James Frost on 15/06/2014.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class CreateItemTest extends ActivityInstrumentationTestCase2<CreateItem> {

    private CreateItem createItem;
    private ToggleButton toggleButtonDate;
    private ToggleButton toggleButtonTime;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView textViewTime;
    private EditText toDoItemName;
    private Spinner groupSpinner;
    private Spinner prioritySpinner;

    @SuppressLint("NewApi")
    public CreateItemTest() {
        super(CreateItem.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        createItem = getActivity();

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
//        assertNotNull("datePicker is null", datePicker);
//        assertNotNull("timePicker is null", timePicker);
//        assertNotNull("textViewTime null", textViewTime);
    }

    public void testInitialVisibilityOfViews() throws Exception {
        assertTrue(View.VISIBLE == toggleButtonDate.getVisibility());
        assertTrue(View.VISIBLE == toDoItemName.getVisibility());
        assertTrue(View.VISIBLE == groupSpinner.getVisibility());
        assertTrue(View.VISIBLE == prioritySpinner.getVisibility());
    }

    public void testDateToggleButton() throws Exception {
        TouchUtils.clickView(this, toggleButtonDate);
        textViewTime = (TextView) createItem.findViewById(R.id.TimeText);
        toggleButtonTime = (ToggleButton) createItem.findViewById(R.id.timeToggleButton);
        assertNotNull("textViewTime is null", textViewTime);
        assertTrue(View.VISIBLE == textViewTime.getVisibility());
        assertTrue(View.VISIBLE == toggleButtonTime.getVisibility());
    }
}
