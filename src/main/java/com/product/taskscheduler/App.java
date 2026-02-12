package com.product.taskscheduler;

import java.util.List;
import java.util.Scanner;

public class App {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProjectDAO projectDAO = new ProjectDAO();
    private static final Scheduler scheduler = new Scheduler();

    public static void main(String[] args) {
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

        Project newProject = new Project(title, deadline, revenue);
        
        // --- Admission Control / Stability Check ---
        List<Project> existingProjects = projectDAO.getAllProjects();
        
        // 1. Calculate current schedule (to know who is promised)
        Scheduler.ScheduleResult currentResult = scheduler.calculateSchedule(existingProjects);
        List<Project> previouslyScheduled = new java.util.ArrayList<>();
        for (Project p : currentResult.getWeeklySchedule()) {
            if (p != null) previouslyScheduled.add(p);
        }
        
        // 2. Calculate new schedule with candidate project
        existingProjects.add(newProject);
        Scheduler.ScheduleResult newResult = scheduler.calculateSchedule(existingProjects);
        List<Project> newlyScheduled = new java.util.ArrayList<>();
        for (Project p : newResult.getWeeklySchedule()) {
            if (p != null) newlyScheduled.add(p);
        }
        
        // 3. Check conditions
        // Condition A: New project must be in the schedule (otherwise it's not profitable enough/no slot)
        boolean isNewScheduled = newlyScheduled.contains(newProject);
        
        // Condition B: All previously scheduled projects must STILL be scheduled (Stability Check)
        // We use ID check or reference check. Since 'existingProjects' contains the same instances
        // as returned by DAO, reference check works for the old ones.
        boolean stable = newlyScheduled.containsAll(previouslyScheduled);
        
        if (isNewScheduled && stable) {
            projectDAO.addProject(newProject);
            System.out.println("✅ Project Accepted and Scheduled.");
        } else {
            System.out.println("❌ Project Rejected.");
            if (!isNewScheduled) {
                System.out.println("Reason: Not profitable enough or no deadline-compatible slot available.");
            } else {
                System.out.println("Reason: Accepting this project would displace an already scheduled project.");
            }
        }
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
        
        Scheduler.ScheduleResult result = scheduler.calculateSchedule(projects);
        Project[] weeklySchedule = result.getWeeklySchedule();
        
        System.out.println("\n=== OPTIMAL WEEKLY SCHEDULE ===");
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        boolean anyScheduled = false;
        
        for (int i = 0; i < 5; i++) {
            if (weeklySchedule[i] != null) {
                System.out.printf("%-10s: %s (Deadline: %d, Revenue: %.2f)%n", 
                        days[i], weeklySchedule[i].getTitle(), weeklySchedule[i].getDeadline(), weeklySchedule[i].getRevenue());
                anyScheduled = true;
            } else {
                System.out.printf("%-10s: [Free Slot]%n", days[i]);
            }
        }

        if (!anyScheduled) {
            System.out.println("No projects could be scheduled.");
        }
        
        System.out.printf("\nTotal Expected Revenue: %.2f%n", result.getTotalRevenue());
        
        List<Project> rejected = result.getRejectedProjects();
        if (!rejected.isEmpty()) {
            System.out.println("\n--- Rejected/Unscheduled Projects ---");
            for (Project p : rejected) {
                 System.out.printf("Title: %s | Revenue: %.2f | Deadline: %d%n", p.getTitle(), p.getRevenue(), p.getDeadline());
            }
        }
        System.out.println("===============================");
    }
}
