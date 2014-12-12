package me.jamesfrost.simpledo;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Handles all date checks
 *
 * Created by James Frost on 12/12/2014.
 */
public class DateHelper {

    /**
     * Checks if a task falls on today's date.
     *
     * @param toDoItem The task to perform the check for
     * @return True if today's date
     */
    public boolean isTodaysDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {

            if (toDoItem.getDate() instanceof LocalDate) {
                LocalDate lt = new LocalDate();
                return lt.equals(toDoItem.getDate());
            } else {
                LocalDateTime ldt = new LocalDateTime();
                LocalDate ld = new LocalDate();
                LocalDate test = new LocalDate(toDoItem.getDate().get(DateTimeFieldType.year()), toDoItem.getDate().get(DateTimeFieldType.monthOfYear()), toDoItem.getDate().get(DateTimeFieldType.dayOfMonth()));
                return toDoItem.getDate().isAfter(ldt) && test.isBefore(ld.plusDays(1));
            }

        } else return false;
    }

    /**
     * Checks if a task falls on tomorrows date.
     *
     * @param toDoItem The task to perform the check for
     * @return True if tomorrows's date
     */
    public boolean isTomorrowsDate(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {

            if (toDoItem.getDate() instanceof LocalDate) {
                LocalDate lt = new LocalDate();
                return toDoItem.getDate().equals(lt.plusDays(1));
            } else {
                LocalDate lt = new LocalDate();
                LocalDate test = new LocalDate(toDoItem.getDate().get(DateTimeFieldType.year()), toDoItem.getDate().get(DateTimeFieldType.monthOfYear()), toDoItem.getDate().get(DateTimeFieldType.dayOfMonth()));
                return test.equals(lt.plusDays(1));
            }
        } else return false;
    }

    /**
     * Checks if a task is over due.
     *
     * @param toDoItem The task to perform the check for
     * @return True if over due
     */
    public boolean isOverDue(ToDoItem toDoItem) {
        if (toDoItem.getDate() != null) {
            if (toDoItem.getDate() instanceof LocalDate) {
                LocalDate lt = new LocalDate();
                return toDoItem.getDate().isBefore(lt);
            } else {
                LocalDateTime ldt = new LocalDateTime();
                return toDoItem.getDate().isBefore(ldt);
            }
        } else return false;
    }
}
