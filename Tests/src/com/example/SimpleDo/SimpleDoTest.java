package com.example.SimpleDo;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.example.SimpleDo.SimpleDoTest \
 * com.example.SimpleDo.tests/android.test.InstrumentationTestRunner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class SimpleDoTest extends ActivityInstrumentationTestCase2<SimpleDo> {

    private SimpleDo simpleDo;
    private TextView textViewOverDue;
    private TextView textViewToday;
    private TextView textViewTomorrow;
    private TextView textViewFuture;
    private TextView textViewSomeday;


    public SimpleDoTest() {
        super("com.example.SimpleDo", SimpleDo.class);
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
