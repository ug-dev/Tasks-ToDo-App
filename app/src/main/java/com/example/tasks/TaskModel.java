package com.example.tasks;

public class TaskModel {
    private int tasks_id;
    private String task_title;
    private String task_due_date;
    private String task_repeat;

    public String getTask_repeat_days() {
        return task_repeat_days;
    }

    public void setTask_repeat_days(String task_repeat_days) {
        this.task_repeat_days = task_repeat_days;
    }

    private String task_repeat_days;
    private String task_reminder;
    private String task_is_important;
    private String task_is_my_day;
    private String task_is_completed;
    private String task_created_time;

    public TaskModel() {}

    public TaskModel(int tasks_id,
                     String task_title,
                     String task_due_date,
                     String task_repeat,
                     String task_reminder,
                     String task_is_important,
                     String task_is_my_day,
                     String task_is_completed,
                     String task_created_time) {

        this.tasks_id = tasks_id;
        this.task_title = task_title;
        this.task_due_date = task_due_date;
        this.task_repeat = task_repeat;
        this.task_reminder = task_reminder;
        this.task_is_important = task_is_important;
        this.task_is_my_day = task_is_my_day;
        this.task_is_completed = task_is_completed;
        this.task_created_time = task_created_time;
    }

    public int getTasks_id() {
        return tasks_id;
    }

    public void setTasks_id(int tasks_id) {
        this.tasks_id = tasks_id;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_due_date() {
        return task_due_date;
    }

    public void setTask_due_date(String task_due_date) {
        this.task_due_date = task_due_date;
    }

    public String getTask_repeat() {
        return task_repeat;
    }

    public void setTask_repeat(String task_repeat) {
        this.task_repeat = task_repeat;
    }

    public String getTask_reminder() {
        return task_reminder;
    }

    public void setTask_reminder(String task_reminder) {
        this.task_reminder = task_reminder;
    }

    public String getTask_is_important() {
        return task_is_important;
    }

    public void setTask_is_important(String task_is_important) {
        this.task_is_important = task_is_important;
    }

    public String getTask_is_my_day() {
        return task_is_my_day;
    }

    public void setTask_is_my_day(String task_is_my_day) {
        this.task_is_my_day = task_is_my_day;
    }

    public String getTask_is_completed() {
        return task_is_completed;
    }

    public void setTask_is_completed(String task_is_completed) {
        this.task_is_completed = task_is_completed;
    }

    public String getTask_created_time() {
        return task_created_time;
    }

    public void setTask_created_time(String task_created_time) {
        this.task_created_time = task_created_time;
    }
}
