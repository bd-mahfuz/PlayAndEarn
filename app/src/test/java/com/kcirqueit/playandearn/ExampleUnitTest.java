package com.kcirqueit.playandearn;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    List<String> list = new ArrayList();



    @Test
    public void addition_isCorrect() {


        list.add("Kamal");
        list.add("Jamal");
        list.add("Rahim");
        list.add("Karim");



        for (int i = 0; i <list.size(); i++) {
            if (list.get(i).equals("Jamal")){
                list.remove(i);
                list.add(i,"Robin");
                break;
            }
        }
        for (String i : list) {
            System.out.println(i);
        }

        assertEquals(4, 2 + 2);
    }
}