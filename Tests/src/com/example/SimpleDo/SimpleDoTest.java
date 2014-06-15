package com.example.SimpleDo;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

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

    public SimpleDoTest() {
        super("com.example.SimpleDo", SimpleDo.class);
    }


}
