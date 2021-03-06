package battleship_game;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/updateLeaderboard")
	public class updateLeaderboard extends HttpServlet {
		
		
		private static final long serialVersionUID = 1L;
	    
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Connection conn = null;
					ResultSet rs = null;
					PreparedStatement ps = null;
			  PrintWriter out = response.getWriter();
			  String Username = request.getParameter("username");
					
					try {
						Class.forName("com.mysql.cj.jdbc.Driver");}
						catch (ClassNotFoundException e) {
						      System.out.println(e.getMessage());
					    }
					
					
					try{
						conn = DriverManager.getConnection("jdbc:mysql://35.236.121.113:3306/LoginDetails?&user=root&password=root");
					
					ps = conn.prepareStatement("UPDATE LoginDetails.leaderboard SET points= points+1 WHERE username=?;");
					ps.setString(1, Username);
					ps.executeUpdate();
			  out.println("success");
			  
				} catch (SQLException sqle) {
					System.out.println ("SQLException: " + sqle.getMessage());
				}
			
			
		}
			
}