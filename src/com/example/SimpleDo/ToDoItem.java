package com.example.SimpleDo;

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
    private Date date;
    //Is the task complete
    private boolean complete;
    //The group the task belongs to
    private String group;
    //The priority of the task
    private String priority;
    //Is a reminder set
    private boolean reminder;
    //Has the user specified a time
    private boolean timeSet;

    private Calendar now;

    /**
     * Constructor for the ToDoItem class.
     *
     * @param name  The name of the task.
     * @param date  The due date of the task.
     * @param group The group of the task.
     */
    public ToDoItem(String name, Date date, String group, String priority, boolean timeSet) {
        this.name = name;
        this.date = date;
        this.group = group;
        this.priority = priority;
        this.timeSet = timeSet;

        complete = false;
    }

    /**
     * Checks if the item is overdue.
     *
     * @return
     */
    public boolean isOverDue() {
        if (date != null && !complete) {
            now = Calendar.getInstance();
            return getYear() < getCurrentYear() ||
                    getYear() == getCurrentYear() && getMonth() < getCurrentMonth() ||
                    getYear() == getCurrentYear() && getMonth() == getCurrentMonth() && getDay() < getCurrentDay() ||
                    getYear() == getCurrentYear() && getMonth() == getCurrentMonth() && getDay() == getCurrentDay() && getHour() < getCurrentHour() ||
                    getYear() == getCurrentYear() && getMonth() == getCurrentMonth() && getDay() == getCurrentDay() && getHour() == getCurrentHour() && getMin() < getCurrentMin();
        } else return false;
    }

    public String getDueTime() {
        if (date != null && timeSet) return "" + date.getHours() + ":" + date.getMinutes();
        else return "";
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

    public int getYear() {
        return date.getYear();
    }

    public int getMonth() {
        return date.getMonth();
    }

    public int getDay() {
        return date.getDay();
    }

    public int getHour() {
        return date.getHours();
    }

    public int getMin() {
        return date.getMinutes();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
