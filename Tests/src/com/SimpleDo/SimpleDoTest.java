package com.SimpleDo;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import me.jamesfrost.simpledo.SimpleDo;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.SimpleDo.SimpleDoTest \
 * com.SimpleDo.tests/android.test.InstrumentationTestRunner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class SimpleDoTest extends ActivityInstrumentationTestCase2<SimpleDo> {

    private SimpleDo simpleDo;
    private TextView textViewOverDue;
    private TextView textViewToday;
    private TextView textViewTomorrow;
    private TextView textViewFuture;
    private TextView textViewSomeday;
    private Button buttonGo;

    public SimpleDoTest() {
        super("com.SimpleDo", SimpleDo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        simpleDo = getActivity();
        textViewOverDue = (TextView) simpleDo.findViewById(R.id.overDue);
        textViewToday = (TextView) simpleDo.findViewById(R.id.today);
        textViewTomorrow = (TextView) simpleDo.findViewById(R.id.tomorrow);
        textViewFuture = (TextView) simpleDo.findViewById(R.id.future);
        textViewSomeday = (TextView) simpleDo.findViewById(R.id.someday);
        buttonGo = (Button) simpleDo.findViewById(R.id.go);
    }

    public void testPreconditions() {
        System.out.println("Testing preconditions...");

        assertNotNull("simpleDo is null", simpleDo);
        assertNotNull("textViewOverDue is null", textViewOverDue);
        assertNotNull("textViewToday is null", textViewToday);
        assertNotNull("textViewTomorrow is null", textViewTomorrow);
        assertNotNull("textViewFuture is null", textViewFuture);
        assertNotNull("textViewSomeday is null", textViewSomeday);
    }

    public void testFilters() throws Exception {

        final String FILTER_TITLE_NO_FILTER = "Simple Do";
        final String FILTER_TITLE_HIGH_PRIORITY = "High Priority";
        final String FILTER_TITLE_MEDIUM_PRIORITY = "Medium Priority";
        final String FILTER_TITLE_LOW_PRIORITY = "Low Priority";
        final String FILTER_TITLE_NOT_COMPLETED = "Not Completed";

        final String FILTER_WORK = "Work";
        final String FILTER_PERSONAL = "Personal";

        final int POSITION_OF_NO_FILTER = 0;
        final int POSITION_OF_HIGH_PRIORITY = 1;
        final int POSITION_OF_MEDIUM_PRIORITY = 2;
        final int POSITION_OF_LOW_PRIORITY = 3;
        final int POSITION_OF_NOT_COMPLETE = 4;
        final int POSITION_OF_WORK = 5;
        final int POSITION_OF_PERSONAL = 6;

        ListView drawerList = simpleDo.getDrawerList();

        drawerList.setSelection(POSITION_OF_NO_FILTER);
        assertEquals(simpleDo.getTitle().toString(), FILTER_TITLE_NO_FILTER);

        drawerList.setSelection(POSITION_OF_HIGH_PRIORITY);
        assertEquals(simpleDo.getTitle().toString(), FILTER_TITLE_HIGH_PRIORITY);

        drawerList.setSelection(POSITION_OF_MEDIUM_PRIORITY);
        assertEquals(simpleDo.getTitle().toString(), FILTER_TITLE_MEDIUM_PRIORITY);

        drawerList.setSelection(POSITION_OF_LOW_PRIORITY);
        assertEquals(simpleDo.getTitle().toString(), FILTER_TITLE_LOW_PRIORITY);

        drawerList.setSelection(POSITION_OF_NOT_COMPLETE);
        assertEquals(simpleDo.getTitle().toString(), FILTER_TITLE_NOT_COMPLETED);

        drawerList.setSelection(POSITION_OF_WORK);
        assertEquals(simpleDo.getTitle().toString(), FILTER_WORK);

        drawerList.setSelection(POSITION_OF_PERSONAL);
        assertEquals(simpleDo.getTitle().toString(), FILTER_PERSONAL);
    }

    public void testTextViewText() throws Exception {
        System.out.println("Testing TextViews...");

        final String expectedOverDue = "OVERDUE";
        final String actualOverDue = textViewOverDue.getText().toString();
        assertEquals(expectedOverDue, actualOverDue);

        final String expectedToday = "TODAY";
        final String actualToday = textViewToday.getText().toString();
        assertEquals(expectedToday, actualToday);

        final String expectedTomorrow = "TOMORROW";
        final String actualTomorrow = textViewTomorrow.getText().toString();
        assertEquals(expectedTomorrow, actualTomorrow);

        final String expectedFuture = "FUTURE";
        final String actualFuture = textViewFuture.getText().toString();
        assertEquals(expectedFuture, actualFuture);

        final String expectedSomeday = "SOMEDAY";
        final String actualSomeday = textViewSomeday.getText().toString();
        assertEquals(expectedSomeday, actualSomeday);
    }
}
