package com.product.taskscheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scheduler {

    public static class ScheduleResult {
        private final Project[] weeklySchedule; // Index 0-4 for Mon-Fri
        private final List<Project> rejectedProjects;
        private final double totalRevenue;

        public ScheduleResult(Project[] weeklySchedule, List<Project> rejectedProjects, double totalRevenue) {
            this.weeklySchedule = weeklySchedule;
            this.rejectedProjects = rejectedProjects;
            this.totalRevenue = totalRevenue;
        }

        public Project[] getWeeklySchedule() {
            return weeklySchedule;
        }

        public List<Project> getRejectedProjects() {
            return rejectedProjects;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }
    }

    public ScheduleResult calculateSchedule(List<Project> allProjects) {
        // Clone list to avoid modifying the original list
        List<Project> sortedProjects = new ArrayList<>(allProjects);
        
        // Sort projects by revenue in descending order
        sortedProjects.sort(Comparator.comparingDouble(Project::getRevenue).reversed());

        Project[] weeklySchedule = new Project[5]; // Mon=0, Tue=1, Wed=2, Thu=3, Fri=4
        double totalRevenue = 0;
        List<Project> rejectedProjects = new ArrayList<>();
        
        // Track which projects are scheduled
        List<Project> scheduledList = new ArrayList<>();

        for (Project project : sortedProjects) {
            boolean scheduled = false;
            
            // Find the latest possible slot before the deadline
            // Deadline 1 = Day 1 (index 0). Max allowed index is 4 (Day 5).
            int maxDayIndex = Math.min(project.getDeadline(), 5) - 1;

            if (maxDayIndex >= 0) {
                for (int i = maxDayIndex; i >= 0; i--) {
                    if (weeklySchedule[i] == null) {
                        weeklySchedule[i] = project;
                        totalRevenue += project.getRevenue();
                        scheduledList.add(project);
                        scheduled = true;
                        break;
                    }
                }
            }

            if (!scheduled) {
                rejectedProjects.add(project);
            }
        }

        return new ScheduleResult(weeklySchedule, rejectedProjects, totalRevenue);
    }
}
