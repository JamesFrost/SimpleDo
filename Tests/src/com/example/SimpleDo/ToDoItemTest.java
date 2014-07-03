package com.example.SimpleDo;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Test class for ToDoItem
 *
 * @author James Frost
 */
public class ToDoItemTest extends TestCase {

    public void testGetDueTime() throws Exception {
        System.out.println("Testing ToDoItem getDueTime()");
        Date date = null;
        ToDoItem toDoItem = new ToDoItem("Test", date, "No group", "Not set");
        assertEquals("", toDoItem.getDueTime());
    }

    public void testIsOverDue() throws Exception {
        System.out.println("Testing ToDoItem getDueTime()");
        Date date = new Date();
        ToDoItem toDoItem = new ToDoItem("Test", date, "No group", "Not set");
        assertEquals(true, toDoItem.isOverDue());
    }
}
