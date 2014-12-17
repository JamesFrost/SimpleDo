package me.jamesfrost.simpledo;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Sorts ArrayLists of ToDoItems.
 *
 * Created by James Frost on 17/12/2014.
 */
public class ToDoItemSorter {


    /**
     * Sorts an ArrayList of ToDoItems by date using a bubble sort algorithm.
     *
     * @param toDoList List of items to sort
     */
    public ArrayList<ToDoItem> sortToDoList(ArrayList<ToDoItem> toDoList) {
        for (int i = toDoList.size() - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (toDoList.get(j).getDate() != null && toDoList.get(j + 1).getDate() != null) {
                    if (toDoList.get(j).getDate() instanceof LocalDateTime && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                        if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        }
                    } else if (!(toDoList.get(j).getDate() instanceof LocalDateTime) && toDoList.get(j + 1).getDate() instanceof LocalDateTime) {
                        if (toDoList.get(j).getDate().isAfter(((LocalDateTime) toDoList.get(j + 1).getDate()).toLocalDate())) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        } else if (toDoList.get(j).getDate().isEqual(((LocalDateTime) toDoList.get(j + 1).getDate()).toLocalDate())) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        }
                    } else if (toDoList.get(j).getDate() instanceof LocalDate && toDoList.get(j + 1).getDate() instanceof LocalDate) {
                        if (toDoList.get(j).getDate().isAfter(toDoList.get(j + 1).getDate())) {
                            ToDoItem temp = toDoList.get(j);
                            toDoList.set(j, toDoList.get(j + 1));
                            toDoList.set(j + 1, temp);
                        }
                    }
                }
            }
        }
        return toDoList;
    }
}
