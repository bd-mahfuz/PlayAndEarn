package com.kcirqueit.playandearn;

import android.content.Context;
import android.util.Log;

import com.kcirqueit.playandearn.utility.DateUtility;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kcirqueit.playandearn", appContext.getPackageName());
    }

    @Test
    public void timeTest() {

        Log.d("djaif", DateUtility.timeStringToMilli("0:30")+"");

        String startAfter = "00:01:30.555";
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        Date date = null;
        try {
            date = dateFormat.parse(startAfter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date.getTime());

        Log.d("timeji",date.getTime()+"");

//        assertEquals("Failed to add new Customer", true, customerDao.save(customer));
    }
}
