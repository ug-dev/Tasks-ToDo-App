package com.example.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    public void deleteDB(Context context) {
        context.deleteDatabase(Util.DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LISTS_TABLE = "CREATE TABLE " + Util.LISTS_TABLE_NAME + "("
                + Util.KEY_LIST_ID + " INTEGER PRIMARY KEY," + Util.KEY_LIST_NAME + " TEXT,"
                + Util.KEY_LIST_COLOR + " TEXT,"
                + Util.KEY_LIST_TOTAL + " TEXT" + ")";

        db.execSQL(CREATE_LISTS_TABLE);

        String CREATE_TASKS_TABLE = "CREATE TABLE " + Util.TASKS_TABLE_NAME + "("
                + Util.KEY_TASK_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_TASK_NAME + " TEXT,"
                + Util.KEY_TASK_DUE_DATE + " TEXT,"
                + Util.KEY_TASK_REPEAT + " TEXT,"
                + Util.KEY_TASK_REPEAT_DAYS + " TEXT,"
                + Util.KEY_TASK_REMINDER + " TEXT,"
                + Util.KEY_TASK_IS_IMPORTANT + " TEXT,"
                + Util.KEY_TASK_IS_MY_DAY + " TEXT,"
                + Util.KEY_TASK_IS_COMPLETED + " TEXT,"
                + Util.KEY_TASK_CREATED_TIME + " TEXT" + ")";

        db.execSQL(CREATE_TASKS_TABLE);

        String CREATE_USER_TASKS_TABLE = "CREATE TABLE " + Util.USER_TASKS_TABLE_NAME + "("
                + Util.KEY_USER_TASK_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_USER_TASK_NAME + " TEXT,"
                + Util.KEY_USER_TASK_LIST_NAME + " TEXT,"
                + Util.KEY_USER_TASK_DUE_DATE + " TEXT,"
                + Util.KEY_USER_TASK_REPEAT + " TEXT,"
                + Util.KEY_USER_TASK_REPEAT_DAYS + " TEXT,"
                + Util.KEY_USER_TASK_REMINDER + " TEXT,"
                + Util.KEY_USER_TASK_IS_IMPORTANT + " TEXT,"
                + Util.KEY_USER_TASK_IS_MY_DAY + " TEXT,"
                + Util.KEY_USER_TASK_IS_COMPLETED + " TEXT,"
                + Util.KEY_USER_TASK_CREATED_TIME + " TEXT" + ")";

        db.execSQL(CREATE_USER_TASKS_TABLE);

        String CREATE_IMPORTANT_TABLE = "CREATE TABLE " + Util.IMPORTANT_TABLE_NAME + "("
                + Util.KEY_IMPORTANT_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_IMPORTANT_TASK_ID + " TEXT,"
                + Util.KEY_IMPORTANT_NAME + " TEXT,"
                + Util.KEY_IMPORTANT_LIST_NAME + " TEXT,"
                + Util.KEY_IMPORTANT_DUE_DATE + " TEXT,"
                + Util.KEY_IMPORTANT_REPEAT + " TEXT,"
                + Util.KEY_IMPORTANT_REPEAT_DAYS + " TEXT,"
                + Util.KEY_IMPORTANT_REMINDER + " TEXT,"
                + Util.KEY_IMPORTANT_IS_IMPORTANT + " TEXT,"
                + Util.KEY_IMPORTANT_IS_MY_DAY + " TEXT,"
                + Util.KEY_IMPORTANT_IS_COMPLETED + " TEXT,"
                + Util.KEY_IMPORTANT_CREATED_TIME + " TEXT" + ")";

        db.execSQL(CREATE_IMPORTANT_TABLE);

        String CREATE_MY_DAY_TABLE = "CREATE TABLE " + Util.MY_DAY_TABLE_NAME + "("
                + Util.KEY_MY_DAY_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_MY_DAY_TASK_ID + " TEXT,"
                + Util.KEY_MY_DAY_NAME + " TEXT,"
                + Util.KEY_MY_DAY_LIST_NAME + " TEXT,"
                + Util.KEY_MY_DAY_DUE_DATE + " TEXT,"
                + Util.KEY_MY_DAY_REPEAT + " TEXT,"
                + Util.KEY_MY_DAY_REPEAT_DAYS + " TEXT,"
                + Util.KEY_MY_DAY_REMINDER + " TEXT,"
                + Util.KEY_MY_DAY_IS_IMPORTANT + " TEXT,"
                + Util.KEY_MY_DAY_IS_MY_DAY + " TEXT,"
                + Util.KEY_MY_DAY_IS_COMPLETED + " TEXT,"
                + Util.KEY_MY_DAY_CREATED_TIME + " TEXT" + ")";

        db.execSQL(CREATE_MY_DAY_TABLE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + Util.USERS_TABLE_NAME + "("
                + Util.KEY_USER_ID + " INTEGER PRIMARY KEY," + Util.KEY_USER_UID + " TEXT,"
                + Util.KEY_USER_NAME + " TEXT,"
                + Util.KEY_USER_EMAIL + " TEXT" + ")";

        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_DLTS_TABLE = "CREATE TABLE " + Util.DEFAULT_LISTS_THEME_COLORS + "("
                + Util.KEY_DLTS_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_DLTS_COLUMN_1 + " TEXT,"
                + Util.KEY_DLTS_COLUMN_2 + " TEXT,"
                + Util.KEY_DLTS_COLUMN_3 + " TEXT" + ")";

        db.execSQL(CREATE_DLTS_TABLE);

        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + Util.SETTINGS_TABLE_NAME + "("
                + Util.KEY_SETTINGS_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_SOUND + " TEXT,"
                + Util.KYE_CONFIRM_DELETE+ " TEXT,"
                + Util.KEY_ADD_TASK + " TEXT,"
                + Util.KEY_STARRED_TASK + " TEXT,"
                + Util.KEY_MARK_COMPLETE + " TEXT,"
                + Util.KEY_VIBRATE + " TEXT,"
                + Util.KEY_PLAN_YOUR_DAY + " TEXT,"
                + Util.KEY_REMINDERS + " TEXT,"
                + Util.KEY_SWIPE_RIGHT + " TEXT,"
                + Util.KEY_SWIPE_LEFT + " TEXT" + ")";

        db.execSQL(CREATE_SETTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_LISTS_TABLE = String.valueOf(R.string.drop_table_query);
        db.execSQL(DROP_LISTS_TABLE, new String[]{Util.DATABASE_NAME});

        //Create a table again
        onCreate(db);
    }

    //CRUD Operations

    public void addList(ListModel listModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_LIST_NAME, listModel.getList_name());
        values.put(Util.KEY_LIST_COLOR, listModel.getList_color());
        values.put(Util.KEY_LIST_TOTAL, listModel.getList_total());

        //Insert to row
        db.insert(Util.LISTS_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public void addSettings(ArrayList<String> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_SOUND, arrayList.get(0));
        values.put(Util.KYE_CONFIRM_DELETE, arrayList.get(1));
        values.put(Util.KEY_ADD_TASK, arrayList.get(2));
        values.put(Util.KEY_STARRED_TASK, arrayList.get(3));
        values.put(Util.KEY_MARK_COMPLETE, arrayList.get(4));
        values.put(Util.KEY_VIBRATE, arrayList.get(5));
        values.put(Util.KEY_PLAN_YOUR_DAY, arrayList.get(6));
        values.put(Util.KEY_REMINDERS, arrayList.get(7));
        values.put(Util.KEY_SWIPE_RIGHT, arrayList.get(8));
        values.put(Util.KEY_SWIPE_LEFT, arrayList.get(9));

        db.insert(Util.SETTINGS_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public void addThemeColor(ArrayList<String> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_DLTS_COLUMN_1, arrayList.get(0));
        values.put(Util.KEY_DLTS_COLUMN_2, arrayList.get(1));
        values.put(Util.KEY_DLTS_COLUMN_3, arrayList.get(2));

        //Insert to row
        db.insert(Util.DEFAULT_LISTS_THEME_COLORS, null, values);
        db.close();//Closing db connection!
    }

    //Add Task
    public void addTask(TaskModel taskModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_TASK_NAME, taskModel.getTask_title());
        values.put(Util.KEY_TASK_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_TASK_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_TASK_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_TASK_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_TASK_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_TASK_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_TASK_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_TASK_CREATED_TIME, taskModel.getTask_created_time());

        //Insert to row
        db.insert(Util.TASKS_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    //Add User Task
    public void addUserTask(TaskModel taskModel, String userTaskListName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_USER_TASK_NAME, taskModel.getTask_title());
        values.put(Util.KEY_USER_TASK_LIST_NAME, userTaskListName);
        values.put(Util.KEY_USER_TASK_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_USER_TASK_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_USER_TASK_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_USER_TASK_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_USER_TASK_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_USER_TASK_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_USER_TASK_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_USER_TASK_CREATED_TIME, taskModel.getTask_created_time());

        //Insert to row
        db.insert(Util.USER_TASKS_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public void addImportantTask(TaskModel taskModel, String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_IMPORTANT_TASK_ID, taskModel.getTasks_id());
        values.put(Util.KEY_IMPORTANT_NAME, taskModel.getTask_title());
        values.put(Util.KEY_IMPORTANT_LIST_NAME, ListName);
        values.put(Util.KEY_IMPORTANT_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_IMPORTANT_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_IMPORTANT_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_IMPORTANT_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_IMPORTANT_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_IMPORTANT_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_IMPORTANT_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_IMPORTANT_CREATED_TIME, taskModel.getTask_created_time());

        //Insert to row
        db.insert(Util.IMPORTANT_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public void addMyDayTask(TaskModel taskModel, String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_MY_DAY_TASK_ID, taskModel.getTasks_id());
        values.put(Util.KEY_MY_DAY_NAME, taskModel.getTask_title());
        values.put(Util.KEY_MY_DAY_LIST_NAME, ListName);
        values.put(Util.KEY_MY_DAY_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_MY_DAY_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_MY_DAY_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_MY_DAY_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_MY_DAY_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_MY_DAY_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_MY_DAY_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_MY_DAY_CREATED_TIME, taskModel.getTask_created_time());

        //Insert to row
        db.insert(Util.MY_DAY_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public void addRemovedImportantTask(TaskModel taskModel, String ListName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_IMPORTANT_ID, id);
        values.put(Util.KEY_IMPORTANT_TASK_ID, taskModel.getTasks_id());
        values.put(Util.KEY_IMPORTANT_NAME, taskModel.getTask_title());
        values.put(Util.KEY_IMPORTANT_LIST_NAME, ListName);
        values.put(Util.KEY_IMPORTANT_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_IMPORTANT_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_IMPORTANT_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_IMPORTANT_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_IMPORTANT_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_IMPORTANT_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_IMPORTANT_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_IMPORTANT_CREATED_TIME, taskModel.getTask_created_time());

        //Insert to row
        db.insert(Util.IMPORTANT_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public void addRemovedMyDayTask(TaskModel taskModel, String ListName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_MY_DAY_ID, id);
        values.put(Util.KEY_MY_DAY_TASK_ID, taskModel.getTasks_id());
        values.put(Util.KEY_MY_DAY_NAME, taskModel.getTask_title());
        values.put(Util.KEY_MY_DAY_LIST_NAME, ListName);
        values.put(Util.KEY_MY_DAY_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_MY_DAY_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_MY_DAY_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_MY_DAY_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_MY_DAY_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_MY_DAY_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_MY_DAY_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_MY_DAY_CREATED_TIME, taskModel.getTask_created_time());

        //Insert to row
        db.insert(Util.MY_DAY_TABLE_NAME, null, values);
        db.close();//Closing db connection!
    }

    public UserData getUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        UserData userData = new UserData();

        //Select user
        String selectAll = "SELECT * FROM " + Util.USERS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        userData.setUserId(cursor.getString(Integer.parseInt(Util.KEY_USER_UID)));
        userData.setUserName(cursor.getString(Integer.parseInt(Util.KEY_USER_NAME)));
        userData.setUserEmail(cursor.getString(Integer.parseInt(Util.KEY_USER_EMAIL)));

        return userData;
    }

    //Get all contacts
    public List<ListModel> getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<ListModel> listItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.LISTS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                ListModel listModel = new ListModel();
                listModel.setList_id(Integer.parseInt(cursor.getString(0)));
                listModel.setList_name(cursor.getString(1));
                listModel.setList_color(cursor.getString(2));
                listModel.setList_total(cursor.getString(3));

                //add objects to list
                listItems.add(listModel);
            } while (cursor.moveToNext());
        }

        return listItems;
    }

    public List<String> getAllSettings() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> items = new ArrayList<>();
        String selectAll = "SELECT * FROM " + Util.SETTINGS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            items.add(cursor.getString(0));
            items.add(cursor.getString(1));
            items.add(cursor.getString(2));
            items.add(cursor.getString(3));
            items.add(cursor.getString(4));
            items.add(cursor.getString(5));
            items.add(cursor.getString(6));
            items.add(cursor.getString(7));
            items.add(cursor.getString(8));
            items.add(cursor.getString(9));
            items.add(cursor.getString(10));
        }

        return items;
    }

    public List<TaskModel> getAllImportantTask() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                taskModel.setTask_title(cursor.getString(2));
                taskModel.setTask_due_date(cursor.getString(4));
                taskModel.setTask_repeat(cursor.getString(5));
                taskModel.setTask_repeat_days(cursor.getString(6));
                taskModel.setTask_reminder(cursor.getString(7));
                taskModel.setTask_is_important(cursor.getString(8));
                taskModel.setTask_is_my_day(cursor.getString(9));
                taskModel.setTask_is_completed(cursor.getString(10));
                taskModel.setTask_created_time(cursor.getString(11));

                //add objects to list
                taskItems.add(taskModel);
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> getAllMyDayTask() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                taskModel.setTask_title(cursor.getString(2));
                taskModel.setTask_due_date(cursor.getString(4));
                taskModel.setTask_repeat(cursor.getString(5));
                taskModel.setTask_repeat_days(cursor.getString(6));
                taskModel.setTask_reminder(cursor.getString(7));
                taskModel.setTask_is_important(cursor.getString(8));
                taskModel.setTask_is_my_day(cursor.getString(9));
                taskModel.setTask_is_completed(cursor.getString(10));
                taskModel.setTask_created_time(cursor.getString(11));

                //add objects to list
                taskItems.add(taskModel);
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<String> getImportantTaskList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> taskItems = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);
        if (cursor.moveToFirst()) {
            do {
                String ListName = cursor.getString(3);
                taskItems.add(ListName);
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<String> getMyDayTaskList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> taskItems = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);
        if (cursor.moveToFirst()) {
            do {
                String ListName = cursor.getString(3);
                taskItems.add(ListName);
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    //Get all contacts
    public List<TaskModel> getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                taskModel.setTask_title(cursor.getString(1));
                taskModel.setTask_due_date(cursor.getString(2));
                taskModel.setTask_repeat(cursor.getString(3));
                taskModel.setTask_repeat_days(cursor.getString(4));
                taskModel.setTask_reminder(cursor.getString(5));
                taskModel.setTask_is_important(cursor.getString(6));
                taskModel.setTask_is_my_day(cursor.getString(7));
                taskModel.setTask_is_completed(cursor.getString(8));
                taskModel.setTask_created_time(cursor.getString(9));

                //add objects to list
                taskItems.add(taskModel);
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<String> getImportantTaskId(String id, String ListName) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> items = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(3).equals(ListName)) {
                    if (cursor.getString(1).equals(id)) {
                        items.add(cursor.getString(0));
                    }
                }
            } while (cursor.moveToNext());
        }

        return items;
    }

    public List<String> getMyDayTaskId(String id, String ListName) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> items = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(3).equals(ListName)) {
                    if (cursor.getString(1).equals(id)) {
                        items.add(cursor.getString(0));
                    }
                }
            } while (cursor.moveToNext());
        }

        return items;
    }

    public List<TaskModel> getAllFilteredItems(String s) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> items = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                String TaskName = cursor.getString(1).toUpperCase();
                if (TaskName.contains(s.toUpperCase())) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(3));
                    taskModel.setTask_repeat(cursor.getString(4));
                    taskModel.setTask_repeat_days(cursor.getString(5));
                    taskModel.setTask_reminder(cursor.getString(6));
                    taskModel.setTask_is_important(cursor.getString(7));
                    taskModel.setTask_is_my_day(cursor.getString(8));
                    taskModel.setTask_is_completed(cursor.getString(9));
                    taskModel.setTask_created_time(cursor.getString(10));

                    //add objects to list
                    items.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME;
        cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                String TaskName = cursor.getString(1).toUpperCase();
                if (TaskName.contains(s.toUpperCase())) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(2));
                    taskModel.setTask_repeat(cursor.getString(3));
                    taskModel.setTask_repeat_days(cursor.getString(4));
                    taskModel.setTask_reminder(cursor.getString(5));
                    taskModel.setTask_is_important(cursor.getString(6));
                    taskModel.setTask_is_my_day(cursor.getString(7));
                    taskModel.setTask_is_completed(cursor.getString(8));
                    taskModel.setTask_created_time(cursor.getString(9));

                    //add objects to list
                    items.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return items;
    }

    public List<String> getAllFilteredListNames(String s) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> items = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                String TaskName = cursor.getString(1).toUpperCase();
                if (TaskName.contains(s.toUpperCase())) {
                    String str = cursor.getString(2);
                    //add objects to list
                    items.add(str);
                }
            } while (cursor.moveToNext());
        }

        selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME;
        cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                String TaskName = cursor.getString(1).toUpperCase();
                if (TaskName.contains(s.toUpperCase())) {
                    //add objects to list
                    items.add("Tasks");
                }
            } while (cursor.moveToNext());
        }

        return items;
    }

    //Get all contacts
    public String getUserTask(int id) {
        String ListName = "";
        SQLiteDatabase db = this.getReadableDatabase();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);
        int cnt = 0;

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(String.valueOf(id))) {
                    ListName = cursor.getString(2);
                    cnt++;
                }
            } while (cursor.moveToNext());
        }
        Util.IMPORTANT_LIST_POSITION = cnt;

        return ListName;
    }

    public String getThemeColor(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> arrayList = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.DEFAULT_LISTS_THEME_COLORS;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            arrayList.add(cursor.getString(0));
            arrayList.add(cursor.getString(1));
            arrayList.add(cursor.getString(2));
            arrayList.add(cursor.getString(3));
        }

        return arrayList.get(id);
    }

    public ArrayList<String> getThemeColorSize() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> arrayList = new ArrayList<>();

        String selectAll = "SELECT * FROM " + Util.DEFAULT_LISTS_THEME_COLORS;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            arrayList.add(cursor.getString(0));
            arrayList.add(cursor.getString(1));
            arrayList.add(cursor.getString(2));
            arrayList.add(cursor.getString(3));
        }

        return arrayList;
    }

    //Get all contacts
    public List<TaskModel> getAllUserTasks(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(3));
                    taskModel.setTask_repeat(cursor.getString(4));
                    taskModel.setTask_repeat_days(cursor.getString(5));
                    taskModel.setTask_reminder(cursor.getString(6));
                    taskModel.setTask_is_important(cursor.getString(7));
                    taskModel.setTask_is_my_day(cursor.getString(8));
                    taskModel.setTask_is_completed(cursor.getString(9));
                    taskModel.setTask_created_time(cursor.getString(10));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    //Get all contacts
    public List<TaskModel> hideCompletedUserTasks(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    if (cursor.getString(9).equals("false")) {
                        TaskModel taskModel = new TaskModel();
                        taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                        taskModel.setTask_title(cursor.getString(1));
                        taskModel.setTask_due_date(cursor.getString(3));
                        taskModel.setTask_repeat(cursor.getString(4));
                        taskModel.setTask_repeat_days(cursor.getString(5));
                        taskModel.setTask_reminder(cursor.getString(6));
                        taskModel.setTask_is_important(cursor.getString(7));
                        taskModel.setTask_is_my_day(cursor.getString(8));
                        taskModel.setTask_is_completed(cursor.getString(9));
                        taskModel.setTask_created_time(cursor.getString(10));

                        //add objects to list
                        taskItems.add(taskModel);
                    }
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> hideCompletedTasks() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(9).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(3));
                    taskModel.setTask_repeat(cursor.getString(4));
                    taskModel.setTask_repeat_days(cursor.getString(5));
                    taskModel.setTask_reminder(cursor.getString(6));
                    taskModel.setTask_is_important(cursor.getString(7));
                    taskModel.setTask_is_my_day(cursor.getString(8));
                    taskModel.setTask_is_completed(cursor.getString(9));
                    taskModel.setTask_created_time(cursor.getString(10));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> hideCompletedImportant() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(10).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                    taskModel.setTask_title(cursor.getString(2));
                    taskModel.setTask_due_date(cursor.getString(4));
                    taskModel.setTask_repeat(cursor.getString(5));
                    taskModel.setTask_repeat_days(cursor.getString(6));
                    taskModel.setTask_reminder(cursor.getString(7));
                    taskModel.setTask_is_important(cursor.getString(8));
                    taskModel.setTask_is_my_day(cursor.getString(9));
                    taskModel.setTask_is_completed(cursor.getString(10));
                    taskModel.setTask_created_time(cursor.getString(11));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> hideCompletedMyDay() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(10).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                    taskModel.setTask_title(cursor.getString(2));
                    taskModel.setTask_due_date(cursor.getString(4));
                    taskModel.setTask_repeat(cursor.getString(5));
                    taskModel.setTask_repeat_days(cursor.getString(6));
                    taskModel.setTask_reminder(cursor.getString(7));
                    taskModel.setTask_is_important(cursor.getString(8));
                    taskModel.setTask_is_my_day(cursor.getString(9));
                    taskModel.setTask_is_completed(cursor.getString(10));
                    taskModel.setTask_created_time(cursor.getString(11));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    //Get all contacts
    public List<TaskModel> getAllUserTasksAlpha(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_NAME + " COLLATE NOCASE";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(3));
                    taskModel.setTask_repeat(cursor.getString(4));
                    taskModel.setTask_repeat_days(cursor.getString(5));
                    taskModel.setTask_reminder(cursor.getString(6));
                    taskModel.setTask_is_important(cursor.getString(7));
                    taskModel.setTask_is_my_day(cursor.getString(8));
                    taskModel.setTask_is_completed(cursor.getString(9));
                    taskModel.setTask_created_time(cursor.getString(10));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }
        return taskItems;
    }

    public List<TaskModel> getAllTasksAlpha() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_NAME + " COLLATE NOCASE";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                taskModel.setTask_title(cursor.getString(1));
                taskModel.setTask_due_date(cursor.getString(2));
                taskModel.setTask_repeat(cursor.getString(3));
                taskModel.setTask_repeat_days(cursor.getString(4));
                taskModel.setTask_reminder(cursor.getString(5));
                taskModel.setTask_is_important(cursor.getString(6));
                taskModel.setTask_is_my_day(cursor.getString(7));
                taskModel.setTask_is_completed(cursor.getString(8));
                taskModel.setTask_created_time(cursor.getString(9));

                //add objects to list
                taskItems.add(taskModel);
            } while (cursor.moveToNext());
        }
        return taskItems;
    }

    public List<TaskModel> getAllImportantAlpha() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_NAME + " COLLATE NOCASE";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                taskModel.setTask_title(cursor.getString(2));
                taskModel.setTask_due_date(cursor.getString(4));
                taskModel.setTask_repeat(cursor.getString(5));
                taskModel.setTask_repeat_days(cursor.getString(6));
                taskModel.setTask_reminder(cursor.getString(7));
                taskModel.setTask_is_important(cursor.getString(8));
                taskModel.setTask_is_my_day(cursor.getString(9));
                taskModel.setTask_is_completed(cursor.getString(10));
                taskModel.setTask_created_time(cursor.getString(11));

                //add objects to list
                taskItems.add(taskModel);
            } while (cursor.moveToNext());
        }
        return taskItems;
    }

    public List<TaskModel> getAllMyDayAlpha() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_NAME + " COLLATE NOCASE";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                TaskModel taskModel = new TaskModel();
                taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                taskModel.setTask_title(cursor.getString(2));
                taskModel.setTask_due_date(cursor.getString(4));
                taskModel.setTask_repeat(cursor.getString(5));
                taskModel.setTask_repeat_days(cursor.getString(6));
                taskModel.setTask_reminder(cursor.getString(7));
                taskModel.setTask_is_important(cursor.getString(8));
                taskModel.setTask_is_my_day(cursor.getString(9));
                taskModel.setTask_is_completed(cursor.getString(10));
                taskModel.setTask_created_time(cursor.getString(11));

                //add objects to list
                taskItems.add(taskModel);
            } while (cursor.moveToNext());
        }
        return taskItems;
    }

    //Get all contacts
    public List<TaskModel> getAllUserTasksImportance(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    if (cursor.getString(7).equals("true")) {
                        TaskModel taskModel = new TaskModel();
                        taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                        taskModel.setTask_title(cursor.getString(1));
                        taskModel.setTask_due_date(cursor.getString(3));
                        taskModel.setTask_repeat(cursor.getString(4));
                        taskModel.setTask_repeat_days(cursor.getString(5));
                        taskModel.setTask_reminder(cursor.getString(6));
                        taskModel.setTask_is_important(cursor.getString(7));
                        taskModel.setTask_is_my_day(cursor.getString(8));
                        taskModel.setTask_is_completed(cursor.getString(9));
                        taskModel.setTask_created_time(cursor.getString(10));

                        //add objects to list
                        taskItems.add(taskModel);
                    }
                }
            } while (cursor.moveToNext());
        }

        //Select all contacts
        selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    if (cursor.getString(7).equals("false")) {
                        TaskModel taskModel = new TaskModel();
                        taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                        taskModel.setTask_title(cursor.getString(1));
                        taskModel.setTask_due_date(cursor.getString(3));
                        taskModel.setTask_repeat(cursor.getString(4));
                        taskModel.setTask_repeat_days(cursor.getString(5));
                        taskModel.setTask_reminder(cursor.getString(6));
                        taskModel.setTask_is_important(cursor.getString(7));
                        taskModel.setTask_is_my_day(cursor.getString(8));
                        taskModel.setTask_is_completed(cursor.getString(9));
                        taskModel.setTask_created_time(cursor.getString(10));

                        //add objects to list
                        taskItems.add(taskModel);
                    }
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> getAllTasksImportance() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(7).equals("true")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(2));
                    taskModel.setTask_repeat(cursor.getString(3));
                    taskModel.setTask_repeat_days(cursor.getString(4));
                    taskModel.setTask_reminder(cursor.getString(5));
                    taskModel.setTask_is_important(cursor.getString(6));
                    taskModel.setTask_is_my_day(cursor.getString(7));
                    taskModel.setTask_is_completed(cursor.getString(8));
                    taskModel.setTask_created_time(cursor.getString(9));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        //Select all contacts
        selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_ID + " DESC";
        cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(7).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(2));
                    taskModel.setTask_repeat(cursor.getString(3));
                    taskModel.setTask_repeat_days(cursor.getString(4));
                    taskModel.setTask_reminder(cursor.getString(5));
                    taskModel.setTask_is_important(cursor.getString(6));
                    taskModel.setTask_is_my_day(cursor.getString(7));
                    taskModel.setTask_is_completed(cursor.getString(8));
                    taskModel.setTask_created_time(cursor.getString(9));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> getAllMyDayImportance() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(8).equals("true")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                    taskModel.setTask_title(cursor.getString(2));
                    taskModel.setTask_due_date(cursor.getString(4));
                    taskModel.setTask_repeat(cursor.getString(5));
                    taskModel.setTask_repeat_days(cursor.getString(6));
                    taskModel.setTask_reminder(cursor.getString(7));
                    taskModel.setTask_is_important(cursor.getString(8));
                    taskModel.setTask_is_my_day(cursor.getString(9));
                    taskModel.setTask_is_completed(cursor.getString(10));
                    taskModel.setTask_created_time(cursor.getString(11));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        //Select all contacts
        selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_ID + " DESC";
        cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(8).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                    taskModel.setTask_title(cursor.getString(2));
                    taskModel.setTask_due_date(cursor.getString(4));
                    taskModel.setTask_repeat(cursor.getString(5));
                    taskModel.setTask_repeat_days(cursor.getString(6));
                    taskModel.setTask_reminder(cursor.getString(7));
                    taskModel.setTask_is_important(cursor.getString(8));
                    taskModel.setTask_is_my_day(cursor.getString(9));
                    taskModel.setTask_is_completed(cursor.getString(10));
                    taskModel.setTask_created_time(cursor.getString(11));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    //Get all contacts
    public List<TaskModel> getAllUserTasksMyDay(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    if (cursor.getString(8).equals("true")) {
                        TaskModel taskModel = new TaskModel();
                        taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                        taskModel.setTask_title(cursor.getString(1));
                        taskModel.setTask_due_date(cursor.getString(3));
                        taskModel.setTask_repeat(cursor.getString(4));
                        taskModel.setTask_repeat_days(cursor.getString(5));
                        taskModel.setTask_reminder(cursor.getString(6));
                        taskModel.setTask_is_important(cursor.getString(7));
                        taskModel.setTask_is_my_day(cursor.getString(8));
                        taskModel.setTask_is_completed(cursor.getString(9));
                        taskModel.setTask_created_time(cursor.getString(10));

                        //add objects to list
                        taskItems.add(taskModel);
                    }
                }
            } while (cursor.moveToNext());
        }

        //Select all contacts
        selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_USER_TASK_ID + " DESC";
        cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    if (cursor.getString(8).equals("false")) {
                        TaskModel taskModel = new TaskModel();
                        taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                        taskModel.setTask_title(cursor.getString(1));
                        taskModel.setTask_due_date(cursor.getString(3));
                        taskModel.setTask_repeat(cursor.getString(4));
                        taskModel.setTask_repeat_days(cursor.getString(5));
                        taskModel.setTask_reminder(cursor.getString(6));
                        taskModel.setTask_is_important(cursor.getString(7));
                        taskModel.setTask_is_my_day(cursor.getString(8));
                        taskModel.setTask_is_completed(cursor.getString(9));
                        taskModel.setTask_created_time(cursor.getString(10));

                        //add objects to list
                        taskItems.add(taskModel);
                    }
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> getAllTasksMyDay() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(8).equals("true")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(2));
                    taskModel.setTask_repeat(cursor.getString(3));
                    taskModel.setTask_repeat_days(cursor.getString(4));
                    taskModel.setTask_reminder(cursor.getString(5));
                    taskModel.setTask_is_important(cursor.getString(6));
                    taskModel.setTask_is_my_day(cursor.getString(7));
                    taskModel.setTask_is_completed(cursor.getString(8));
                    taskModel.setTask_created_time(cursor.getString(9));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        //Select all contacts
        selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY "
                + Util.KEY_TASK_ID + " DESC";
        cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(8).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(0)));
                    taskModel.setTask_title(cursor.getString(1));
                    taskModel.setTask_due_date(cursor.getString(2));
                    taskModel.setTask_repeat(cursor.getString(3));
                    taskModel.setTask_repeat_days(cursor.getString(4));
                    taskModel.setTask_reminder(cursor.getString(5));
                    taskModel.setTask_is_important(cursor.getString(6));
                    taskModel.setTask_is_my_day(cursor.getString(7));
                    taskModel.setTask_is_completed(cursor.getString(8));
                    taskModel.setTask_created_time(cursor.getString(9));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public List<TaskModel> getAllImportantMYDAY() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<TaskModel> taskItems = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(9).equals("true")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                    taskModel.setTask_title(cursor.getString(2));
                    taskModel.setTask_due_date(cursor.getString(4));
                    taskModel.setTask_repeat(cursor.getString(5));
                    taskModel.setTask_repeat_days(cursor.getString(6));
                    taskModel.setTask_reminder(cursor.getString(7));
                    taskModel.setTask_is_important(cursor.getString(8));
                    taskModel.setTask_is_my_day(cursor.getString(9));
                    taskModel.setTask_is_completed(cursor.getString(10));
                    taskModel.setTask_created_time(cursor.getString(11));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        //Select all contacts
        selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_ID + " DESC";
        cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(9).equals("false")) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setTasks_id(Integer.parseInt(cursor.getString(1)));
                    taskModel.setTask_title(cursor.getString(2));
                    taskModel.setTask_due_date(cursor.getString(4));
                    taskModel.setTask_repeat(cursor.getString(5));
                    taskModel.setTask_repeat_days(cursor.getString(6));
                    taskModel.setTask_reminder(cursor.getString(7));
                    taskModel.setTask_is_important(cursor.getString(8));
                    taskModel.setTask_is_my_day(cursor.getString(9));
                    taskModel.setTask_is_completed(cursor.getString(10));
                    taskModel.setTask_created_time(cursor.getString(11));

                    //add objects to list
                    taskItems.add(taskModel);
                }
            } while (cursor.moveToNext());
        }

        return taskItems;
    }

    public int getUserTasksCount(String listName) {
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(listName)) {
                    cnt++;
                }
            } while (cursor.moveToNext());
        }

        return cnt;
    }

    public int getTasksCount() {
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                cnt++;
            } while (cursor.moveToNext());
        }

        return cnt;
    }

    public int getLastTaskId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int lastId = 0;

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.TASKS_TABLE_NAME + " ORDER BY " + Util.KEY_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            lastId = Integer.parseInt(cursor.getString(0));
        }

        return lastId;
    }

    public int getLastUserTaskId() {
        SQLiteDatabase db = this.getReadableDatabase();

        int lastId = 0;

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME + " ORDER BY " + Util.KEY_USER_TASK_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            lastId = Integer.parseInt(cursor.getString(0));
        }

        return lastId;
    }

    public ArrayList<String> getAllUserTaskId(String ListName) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> arrayList = new ArrayList<>();

        //Select all contacts
        String selectAll = "SELECT * FROM " + Util.USER_TASKS_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Loop through our data
        if (cursor.moveToFirst()) {
            do {
                if (!cursor.getString(2).equals(ListName))
                    arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return arrayList;
    }

    //Update contact
    public int updateList(ListModel listModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_LIST_NAME, listModel.getList_name());
        values.put(Util.KEY_LIST_COLOR, listModel.getList_color());
        values.put(Util.KEY_LIST_TOTAL, listModel.getList_total());

        return db.update(Util.LISTS_TABLE_NAME, values, Util.KEY_LIST_NAME + "=?",
                new String[]{String.valueOf(listModel.getList_name())});
    }

    //Update contact
    public int updateRenameList(ListModel listModel, String list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_LIST_NAME, listModel.getList_name());
        values.put(Util.KEY_LIST_COLOR, listModel.getList_color());
        values.put(Util.KEY_LIST_TOTAL, listModel.getList_total());

        return db.update(Util.LISTS_TABLE_NAME, values, Util.KEY_LIST_NAME + "=?",
                new String[]{list});
    }

    //Update UserTasks
    public void updateUserTask(TaskModel taskModel, String userTaskListName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_USER_TASK_NAME, taskModel.getTask_title());
        values.put(Util.KEY_USER_TASK_LIST_NAME, userTaskListName);
        values.put(Util.KEY_USER_TASK_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_USER_TASK_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_USER_TASK_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_USER_TASK_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_USER_TASK_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_USER_TASK_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_USER_TASK_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_USER_TASK_CREATED_TIME, taskModel.getTask_created_time());

        db.update(Util.USER_TASKS_TABLE_NAME, values,Util.KEY_USER_TASK_ID + "=?",
                new String[]{String.valueOf(taskModel.getTasks_id())});
        db.close();
    }

    public void updateTasksThemeColor(String str) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_DLTS_COLUMN_3, str);

        db.update(Util.DEFAULT_LISTS_THEME_COLORS, values,Util.KEY_DLTS_ID + "=?",
                new String[]{"1"});
        db.close();//Closing db connection!
    }

    public void updateImportantThemeColor(String str) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_DLTS_COLUMN_2, str);

        db.update(Util.DEFAULT_LISTS_THEME_COLORS, values,Util.KEY_DLTS_ID + "=?",
                new String[]{"1"});
        db.close();//Closing db connection!
    }

    public void updateMyDayThemeColor(String str) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_DLTS_COLUMN_1, str);

        db.update(Util.DEFAULT_LISTS_THEME_COLORS, values,Util.KEY_DLTS_ID + "=?",
                new String[]{"1"});
        db.close();//Closing db connection!
    }

    public void updateSettings(List<String> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Util.KEY_SOUND, arrayList.get(1));
        values.put(Util.KYE_CONFIRM_DELETE, arrayList.get(2));
        values.put(Util.KEY_ADD_TASK, arrayList.get(3));
        values.put(Util.KEY_STARRED_TASK, arrayList.get(4));
        values.put(Util.KEY_MARK_COMPLETE, arrayList.get(5));
        values.put(Util.KEY_VIBRATE, arrayList.get(6));
        values.put(Util.KEY_PLAN_YOUR_DAY, arrayList.get(7));
        values.put(Util.KEY_REMINDERS, arrayList.get(8));
        values.put(Util.KEY_SWIPE_RIGHT, arrayList.get(9));
        values.put(Util.KEY_SWIPE_LEFT, arrayList.get(10));

        db.update(Util.SETTINGS_TABLE_NAME, values,Util.KEY_SETTINGS_ID + "=?",
                new String[]{"1"});
        db.close();
    }

    //Update UserTasks
    public void updateUserTaskList(String userTaskListName, String list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_USER_TASK_LIST_NAME, list);

        db.update(Util.USER_TASKS_TABLE_NAME, values,Util.KEY_USER_TASK_LIST_NAME + "=?",
                new String[]{userTaskListName});
        db.close();
    }

    public void updateImportantList(String userTaskListName, String list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_IMPORTANT_LIST_NAME, list);

        db.update(Util.IMPORTANT_TABLE_NAME, values,Util.KEY_IMPORTANT_LIST_NAME + "=?",
                new String[]{userTaskListName});
        db.close();
    }

    public void updateMyDayList(String userTaskListName, String list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_MY_DAY_LIST_NAME, list);

        db.update(Util.MY_DAY_TABLE_NAME, values,Util.KEY_MY_DAY_LIST_NAME + "=?",
                new String[]{userTaskListName});
        db.close();
    }

    public void updateImportantTask(TaskModel taskModel, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_IMPORTANT_NAME, taskModel.getTask_title());
        values.put(Util.KEY_IMPORTANT_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_IMPORTANT_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_IMPORTANT_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_IMPORTANT_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_IMPORTANT_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_IMPORTANT_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_IMPORTANT_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_IMPORTANT_CREATED_TIME, taskModel.getTask_created_time());

        db.update(Util.IMPORTANT_TABLE_NAME, values,Util.KEY_IMPORTANT_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateMyDayTask(TaskModel taskModel, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_MY_DAY_NAME, taskModel.getTask_title());
        values.put(Util.KEY_MY_DAY_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_MY_DAY_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_MY_DAY_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_MY_DAY_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_MY_DAY_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_MY_DAY_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_MY_DAY_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_MY_DAY_CREATED_TIME, taskModel.getTask_created_time());

        db.update(Util.MY_DAY_TABLE_NAME, values,Util.KEY_MY_DAY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteImportantTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectAll = "SELECT * FROM " + Util.IMPORTANT_TABLE_NAME + " ORDER BY "
                + Util.KEY_IMPORTANT_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(String.valueOf(id))) {
                    db.delete(Util.IMPORTANT_TABLE_NAME,Util.KEY_IMPORTANT_ID + "=?",
                            new String[]{String.valueOf(id)});
                }
            } while(cursor.moveToNext());
        }
        db.close();
    }

    public void deleteMyDayTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectAll = "SELECT * FROM " + Util.MY_DAY_TABLE_NAME + " ORDER BY "
                + Util.KEY_MY_DAY_ID + " DESC";
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(String.valueOf(id))) {
                    db.delete(Util.MY_DAY_TABLE_NAME,Util.KEY_MY_DAY_ID + "=?",
                            new String[]{String.valueOf(id)});
                }
            } while(cursor.moveToNext());
        }
        db.close();
    }

    //Update UserTasks
    public void updateTask(TaskModel taskModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_TASK_NAME, taskModel.getTask_title());
        values.put(Util.KEY_TASK_DUE_DATE, taskModel.getTask_due_date());
        values.put(Util.KEY_TASK_REPEAT, taskModel.getTask_repeat());
        values.put(Util.KEY_TASK_REPEAT_DAYS, taskModel.getTask_repeat_days());
        values.put(Util.KEY_TASK_REMINDER, taskModel.getTask_reminder());
        values.put(Util.KEY_TASK_IS_IMPORTANT, taskModel.getTask_is_important());
        values.put(Util.KEY_TASK_IS_MY_DAY, taskModel.getTask_is_my_day());
        values.put(Util.KEY_TASK_IS_COMPLETED, taskModel.getTask_is_completed());
        values.put(Util.KEY_TASK_CREATED_TIME, taskModel.getTask_created_time());

        db.update(Util.TASKS_TABLE_NAME, values,Util.KEY_TASK_ID + "=?",
                new String[]{String.valueOf(taskModel.getTasks_id())});
        db.close();
    }

    public void updateUser(UserData userData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_USER_UID, userData.getUserId());
        values.put(Util.KEY_USER_NAME, userData.getUserName());
        values.put(Util.KEY_USER_EMAIL, userData.getUserEmail());

        db.update(Util.USERS_TABLE_NAME, values,Util.KEY_USER_UID + "=?",
                new String[]{userData.getUserId()});
        db.close();
    }

    //Delete single contact
    void deleteList(String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.LISTS_TABLE_NAME, Util.KEY_LIST_NAME + "=?",
                new String[]{String.valueOf(ListName)});
        db.close();
    }

    //Delete single contact
    void deleteUserTask(TaskModel taskModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.USER_TASKS_TABLE_NAME, Util.KEY_USER_TASK_ID + "=?",
                new String[]{String.valueOf(taskModel.getTasks_id())});
        db.close();
    }

    //Delete single contact
    void deleteTask(TaskModel taskModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.TASKS_TABLE_NAME, Util.KEY_TASK_ID + "=?",
                new String[]{String.valueOf(taskModel.getTasks_id())});
        db.close();
    }

    void deleteUserTaskList(String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.USER_TASKS_TABLE_NAME, Util.KEY_USER_TASK_LIST_NAME + "=?",
                new String[]{String.valueOf(ListName)});
        db.close();
    }

    void deleteMyDayList(String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.MY_DAY_TABLE_NAME, Util.KEY_MY_DAY_LIST_NAME + "=?",
                new String[]{String.valueOf(ListName)});
        db.close();
    }

    void deleteImportantList(String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.IMPORTANT_TABLE_NAME, Util.KEY_IMPORTANT_LIST_NAME + "=?",
                new String[]{String.valueOf(ListName)});
        db.close();
    }
}