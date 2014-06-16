package com.example.SimpleDo;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Test class
 * Created by James on 15/06/2014.
 */
public class ToDoItemTest  extends TestCase{

    public void testGetDueTime() throws Exception {
        System.out.println("Testing ToDoItem getDueTime()");
        Date date = null;
        ToDoItem toDoItem = new ToDoItem("Test", date, "No group", "Not set");
        assertEquals("", toDoItem.getDueTime());
    }
}
