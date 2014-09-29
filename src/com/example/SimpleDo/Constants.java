package com.example.SimpleDo;

/**
 * Stores constants.
 *
 * Created by James Frost on 24/09/2014.
 */
public interface Constants {
    //Keys used to pass data between activities
    public static final String KEY_NEWTODOITEM = "newToDoItem";
    public static final String KEY_OLDTODOITEM = "oldToDoItem";
    public static final String KEY_REMINDER = "reminder";
    public static final String KEY_TODOLIST = "toDoList";
    public static final String KEY_GROUP = "group";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_DATE = "date";
    public static final String KEY_NAME = "toDoItemName";
    public static final String KEY_CHECKBOXTOBODELETEDNAME = "checkBoxToBeDeletedName";
    //Warning messages
    public static final String TOAST_EMPTY_NAME_WARNING = "The task name is empty!";
    public static final String TOAST_DUPLICATE_ITEM_WARNING = "Task with that name already exists!";
    //Activity results codes
    public static final int REQUEST_CODE_ADD_ITEM = 100;
    public static final int REQUEST_CODE_EDIT_ITEM = 200;
    public static final int REQUEST_CODE_QUICK_RESCHEDULE = 300;
    //Delete dialog text
    public static final String DELETE_CONFORMATION_MESSAGE = "It will be gone forever!";
    public  static final String POSITIVE_BUTTON_TEXT = "Confirm";
    public static final String NEGATIVE_BUTTON_TEXT = "Cancel";
}
