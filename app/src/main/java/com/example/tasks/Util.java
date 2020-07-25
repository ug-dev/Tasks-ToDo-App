package com.example.tasks;

import java.util.ArrayList;
import java.util.List;

public class Util {
    //Checking user first time logged in or not...
    public static boolean EXIT_FLAG = false;
    public static boolean USER_LOGGED = false;
    public static boolean IS_TASKS = false;
    public static boolean IS_IMPORTANT = false;
    public static boolean IS_MY_DAY = false;
    public static int LIST_POS;
    public static int IMPORTANT_LIST_POSITION;
    public static boolean IS_IMPORTANT_DELETED;
    public static boolean IS_MY_DAY_DELETED;
    public static List<String> SEARCH_LIST_NAMES = new ArrayList<>();

    //Database related items
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasks_db";
    public static final String LISTS_TABLE_NAME = "lists";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String USER_TASKS_TABLE_NAME = "user_tasks";
    public static final String USERS_TABLE_NAME = "users";
    public static final String IMPORTANT_TABLE_NAME = "important_table";
    public static final String MY_DAY_TABLE_NAME = "my_day_table";
    public static final String DEFAULT_LISTS_THEME_COLORS = "default_list_theme_colors";
    public static final String SETTINGS_TABLE_NAME = "settings_table";

    //Lists table columns names
    public static final String KEY_LIST_ID = "list_id";
    public static final String KEY_LIST_NAME = "list_name";
    public static final String KEY_LIST_COLOR = "list_color";
    public static final String KEY_LIST_TOTAL = "list_total";

    //Tasks table column names
    public static final String KEY_TASK_ID = "task_id";
    public static final String KEY_TASK_NAME = "task_name";
    public static final String KEY_TASK_DUE_DATE = "task_due_date";
    public static final String KEY_TASK_REPEAT = "task_repeat";
    public static final String KEY_TASK_REPEAT_DAYS = "repeat_days";
    public static final String KEY_TASK_REMINDER = "task_reminder";
    public static final String KEY_TASK_IS_IMPORTANT = "task_is_important";
    public static final String KEY_TASK_IS_MY_DAY = "task_is_my_day";
    public static final String KEY_TASK_IS_COMPLETED = "task_is_completed";
    public static final String KEY_TASK_CREATED_TIME = "task_created_time";

    //User Tasks table column names
    public static final String KEY_USER_TASK_ID = "user_task_id";
    public static final String KEY_USER_TASK_NAME = "user_task_name";
    public static final String KEY_USER_TASK_LIST_NAME = "user_task_list_name";
    public static final String KEY_USER_TASK_DUE_DATE = "user_task_due_date";
    public static final String KEY_USER_TASK_REPEAT = "user_task_repeat";
    public static final String KEY_USER_TASK_REPEAT_DAYS = "user_task_repeat_days";
    public static final String KEY_USER_TASK_REMINDER = "user_task_reminder";
    public static final String KEY_USER_TASK_IS_IMPORTANT = "user_task_is_important";
    public static final String KEY_USER_TASK_IS_MY_DAY = "user_task_is_my_day";
    public static final String KEY_USER_TASK_IS_COMPLETED = "user_task_is_completed";
    public static final String KEY_USER_TASK_CREATED_TIME = "user_task_created_time";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_UID = "user_uid";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";

    public static final String KEY_IMPORTANT_ID = "important_id";
    public static final String KEY_IMPORTANT_TASK_ID = "important_task_id";
    public static final String KEY_IMPORTANT_NAME = "important_name";
    public static final String KEY_IMPORTANT_LIST_NAME = "important_list_name";
    public static final String KEY_IMPORTANT_DUE_DATE = "important_due_date";
    public static final String KEY_IMPORTANT_REPEAT = "important_repeat";
    public static final String KEY_IMPORTANT_REPEAT_DAYS = "important_repeat_days";
    public static final String KEY_IMPORTANT_REMINDER = "important_reminder";
    public static final String KEY_IMPORTANT_IS_IMPORTANT = "important_is_important";
    public static final String KEY_IMPORTANT_IS_MY_DAY = "important_is_my_day";
    public static final String KEY_IMPORTANT_IS_COMPLETED = "important_is_completed";
    public static final String KEY_IMPORTANT_CREATED_TIME = "important_created_time";

    public static final String KEY_MY_DAY_ID = "my_day_id";
    public static final String KEY_MY_DAY_TASK_ID = "my_day_task_id";
    public static final String KEY_MY_DAY_NAME = "my_day_name";
    public static final String KEY_MY_DAY_LIST_NAME = "my_day_list_name";
    public static final String KEY_MY_DAY_DUE_DATE = "my_day_due_date";
    public static final String KEY_MY_DAY_REPEAT = "my_day_repeat";
    public static final String KEY_MY_DAY_REPEAT_DAYS = "my_day_repeat_days";
    public static final String KEY_MY_DAY_REMINDER = "my_day_reminder";
    public static final String KEY_MY_DAY_IS_IMPORTANT = "my_day_is_important";
    public static final String KEY_MY_DAY_IS_MY_DAY = "my_day_is_my_day";
    public static final String KEY_MY_DAY_IS_COMPLETED = "my_day_is_completed";
    public static final String KEY_MY_DAY_CREATED_TIME = "my_day_created_time";

    public static final String KEY_DLTS_ID = "dlts_id";
    public static final String KEY_DLTS_COLUMN_1 = "dlts_column_1";
    public static final String KEY_DLTS_COLUMN_2 = "dlts_column_2";
    public static final String KEY_DLTS_COLUMN_3 = "dlts_column_3";

    public static final String KEY_SETTINGS_ID = "settings_id";
    public static final String KEY_SOUND = "sound";
    public static final String KYE_CONFIRM_DELETE = "confirm_delete";
    public static final String KEY_ADD_TASK = "add_task";
    public static final String KEY_STARRED_TASK = "starred_task";
    public static final String KEY_MARK_COMPLETE = "mark_complete";
    public static final String KEY_VIBRATE = "vibrate";
    public static final String KEY_PLAN_YOUR_DAY = "plan_your_day";
    public static final String KEY_REMINDERS = "reminders";
    public static final String KEY_SWIPE_RIGHT = "swipe_right";
    public static final String KEY_SWIPE_LEFT = "swipe_left";
}
