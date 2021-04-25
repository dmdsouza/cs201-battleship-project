package battleship_game;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/ShowLeaderboard")
	public class ShowLeaderboard extends HttpServlet {
		
		
		private static final long serialVersionUID = 1L;
	    
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Connection conn = null;
					ResultSet rs = null;
					PreparedStatement ps = null;
			  PrintWriter out = response.getWriter();
					
					try {
						Class.forName("com.mysql.cj.jdbc.Driver");}
						catch (ClassNotFoundException e) {
						      System.out.println(e.getMessage());
					    }
					
					
					try{
						conn = DriverManager.getConnection("jdbc:mysql://35.236.121.113:3306/LoginDetails?&user=root&password=root");
					
					ps = conn.prepareStatement("SELECT * from LoginDetails.leaderboard ORDER BY points DESC;");
				
					rs=  ps.executeQuery();
			  
			  List<TopUser> LeaderboardUsers= new ArrayList<>();
			  
			  for (int i = 0; i < 10; i++){
			  
			      if (rs.next()){
			        
			        String name= rs.getString("username");
			        String points= rs.getString("points");
			        LeaderboardUsers.add(new TopUser(name, points));
			        
			      }
			  
			  
			  }
			  
			 Top10Users output= new Top10Users(LeaderboardUsers);
			 Gson jsonOutput = new Gson();  
			 	System.out.println(jsonOutput.toJson(output));
				 out.println(jsonOutput.toJson(output));
			  
			  
				} catch (SQLException sqle) {
					System.out.println ("SQLException: " + sqle.getMessage());
				}
			
			
		}
			
}
