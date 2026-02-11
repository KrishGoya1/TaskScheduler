package com.product.taskscheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public void addProject(Project project) {
        String sql = "INSERT INTO projects (title, deadline, revenue) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, project.getTitle());
            pstmt.setInt(2, project.getDeadline());
            pstmt.setDouble(3, project.getRevenue());
            
            pstmt.executeUpdate();
            System.out.println("success: " + project.getTitle());
            
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(new Project(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("deadline"),
                        rs.getDouble("revenue")
                ));
            }
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
        }
        return projects;
    }
}
