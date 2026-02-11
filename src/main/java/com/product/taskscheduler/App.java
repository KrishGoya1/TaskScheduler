package com.product.taskscheduler;

import java.util.List;
import java.util.Scanner;

public class App {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProjectDAO projectDAO = new ProjectDAO();
    private static final Scheduler scheduler = new Scheduler();

    public static void main(String[] args) {
        // Initialize DB
        DatabaseManager.initializeDatabase();
        
        System.out.println("Welcome to ProManage Task Scheduler");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Add Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Generate Weekly Schedule");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    addProject();
                    break;
                case "2":
                    viewProjects();
                    break;
                case "3":
                    generateSchedule();
                    break;
                case "4":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addProject() {
        System.out.print("Enter Project Title: ");
        String title = scanner.nextLine();
        
        int deadline = 0;
        while (true) {
            try {
                System.out.print("Enter Deadline (days, e.g. 3): ");
                deadline = Integer.parseInt(scanner.nextLine());
                if (deadline > 0) break;
                System.out.println("Deadline must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
        
        double revenue = 0;
        while (true) {
            try {
                System.out.print("Enter Revenue: ");
                revenue = Double.parseDouble(scanner.nextLine());
                if (revenue > 0) break;
                System.out.println("Revenue must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }

        Project project = new Project(title, deadline, revenue);
        projectDAO.addProject(project);
    }

    private static void viewProjects() {
        List<Project> projects = projectDAO.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
        } else {
            System.out.println("\n--- All Projects ---");
            for (Project p : projects) {
                System.out.println(p);
            }
        }
    }

    private static void generateSchedule() {
        List<Project> projects = projectDAO.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects available to schedule.");
            return;
        }
        scheduler.generateSchedule(projects);
    }
}
