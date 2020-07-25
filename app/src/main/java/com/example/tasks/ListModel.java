package com.example.tasks;

public class ListModel {
    private int list_id;
    private String list_name;
    private String list_color;
    private String list_total;

    public ListModel() { }

    public ListModel(int list_id, String list_name, String list_color, String list_total) {
        this.list_id = list_id;
        this.list_name = list_name;
        this.list_color = list_color;
        this.list_total = list_total;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getList_color() {
        return list_color;
    }

    public void setList_color(String list_color) {
        this.list_color = list_color;
    }

    public String getList_total() {
        return list_total;
    }

    public void setList_total(String list_total) {
        this.list_total = list_total;
    }
}
