package com.example.SimpleDo;

import org.joda.time.base.BaseLocal;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Represents a task.
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

    /**
     * Constructs an instance.
     *
     * @param name     The name
     * @param date     The due date (Null if no due date set)
     * @param group    The group (No Group, Work, Personal)
     * @param priority The priority (No Priority, Low, Medium, High)
     * @param timeSet
     */
    public ToDoItem(String name, BaseLocal date, String group, String priority, boolean timeSet) {
        this.name = name;
        this.date = date;
        this.group = group;
        this.priority = priority;
        this.timeSet = timeSet;
        complete = false;

        if (!(this.group.equals("No Group") || this.group.equals("Work") || this.group.equals("Personal"))) throw new IllegalArgumentException("No such group exists.");
        if (!(this.priority.equals("No Priority") || this.priority.equals("Low") || this.priority.equals("Medium") || this.priority.equals("High"))) throw new IllegalArgumentException("No such priority exists.");
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

    /**
     * Returns null if no reminder has been set.
     * @return eventID
     */
    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
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

    /**
     * Returns null if no date has been set.
     * @return date
     */
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
