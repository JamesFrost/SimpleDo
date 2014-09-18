package com.example.SimpleDo;

import org.joda.time.base.BaseLocal;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Class for To Do items.
 *
 * @author James Frost
 */
public class ToDoItem implements Serializable {

    //The name of the task
    private String name;
    //The due date
    private BaseLocal date;
    //Is the task complete
    private boolean complete;
    //The group the task belongs to
    private String group;
    //The priority of the task
    private String priority;
    //Is a reminder set
    private boolean reminder;
    //The eventID of the reminder - this will only be set if reminder == true
    private long eventID;
    //Has the user specified a time
    private boolean timeSet;
    //id used to SQLite Database
    private long id;

    private Calendar now;

    /**
     * Constructor for the ToDoItem class.
     *
     * @param name  The name of the task.
     * @param date  The due date of the task.
     * @param group The group of the task (No Group, Work, Personal) .
     * @param priority The priority of the task (No Priority, Low, Medium, High).
     * @param timeSet
     */
    public ToDoItem(String name, BaseLocal date, String group, String priority, boolean timeSet) {
        this.name = name;
        this.date = date;
        this.group = group;
        this.priority = priority;
        this.timeSet = timeSet;
        complete = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ToDoItem) // check to make sure o isn't null, and that o is a Dummy object
        {
            if (((ToDoItem) o).getId() == id) return true;
        }
        return false; // either o is null or isn't a Dummy object
    }

    public boolean isTimeSet() {
        return timeSet;
    }

    public void setTimeSet(boolean timeSet) {
        this.timeSet = timeSet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    private int getCurrentYear() {
        now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
    }

    private int getCurrentMonth() {
        return now.get(Calendar.MONTH);
    }

    private int getCurrentHour() {
        return now.get(Calendar.HOUR_OF_DAY);
    }

    private int getCurrentMin() {
        return now.get(Calendar.MINUTE);
    }

    private int getCurrentDay() {
        return now.get(Calendar.DAY_OF_WEEK);
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public BaseLocal getDate() {
        return date;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getName() {
        return name;
    }
}
