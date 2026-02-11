package com.product.taskscheduler;

public class Project {
    private int id;
    private String title;
    private int deadline;
    private double revenue;

    public Project(int id, String title, int deadline, double revenue) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.revenue = revenue;
    }

    public Project(String title, int deadline, double revenue) {
        this.title = title;
        this.deadline = deadline;
        this.revenue = revenue;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDeadline() { return deadline; }
    public double getRevenue() { return revenue; }

    @Override
    public String toString() {
        return String.format("ID: %d | Title: %s | Deadline: %d days | Revenue: %.2f", id, title, deadline, revenue);
    }
}
