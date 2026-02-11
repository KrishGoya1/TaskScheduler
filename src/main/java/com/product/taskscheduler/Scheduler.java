package com.product.taskscheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Scheduler {

    public void generateSchedule(List<Project> allProjects) {
        // Sort projects by revenue in descending order
        allProjects.sort(Comparator.comparingDouble(Project::getRevenue).reversed());

        Project[] weeklySchedule = new Project[5]; // Mon=0, Tue=1, Wed=2, Thu=3, Fri=4
        double totalRevenue = 0;
        int projectsScheduledCount = 0;

        for (Project project : allProjects) {
            if (projectsScheduledCount >= 5) break; 

            // Find the latest possible slot before the deadline
            // Deadline represents max days from start (1-based index effectively).
            // E.g. Deadline 3 means can be done on Day 1, 2, or 3 (index 0, 1, 2).
            // We cap at 5 because we only have 5 days.
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
