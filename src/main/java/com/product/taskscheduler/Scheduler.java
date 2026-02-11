package com.product.taskscheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Scheduler {

    public void generateSchedule(List<Project> allProjects) {
        allProjects.sort(Comparator.comparingDouble(Project::getRevenue).reversed());

        Project[] weeklySchedule = new Project[5];
        double totalRevenue = 0;
        int projectsScheduledCount = 0;

        for (Project project : allProjects) {
            if (projectsScheduledCount >= 5) break; 
            int maxDayIndex = Math.min(project.getDeadline(), 5) - 1;

            for (int i = maxDayIndex; i >= 0; i--) {
                if (weeklySchedule[i] == null) {
                    weeklySchedule[i] = project;
                    totalRevenue += project.getRevenue();
                    projectsScheduledCount++;
                    break; 
                }
            }
        }

        System.out.println("\n=== OPTIMAL WEEKLY SCHEDULE ===");
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        boolean anyScheduled = false;
        
        for (int i = 0; i < 5; i++) {
            if (weeklySchedule[i] != null) {
                System.out.printf("%-10s: %s (Revenue: %.2f)%n", days[i], weeklySchedule[i].getTitle(), weeklySchedule[i].getRevenue());
                anyScheduled = true;
            } else {
                System.out.printf("%-10s: [Free Slot]%n", days[i]);
            }
        }

        if (!anyScheduled) {
            System.out.println("No projects could be scheduled.");
        }
        
        System.out.printf("\nTotal Expected Revenue: %.2f%n", totalRevenue);
        System.out.println("===============================");
    }
}
