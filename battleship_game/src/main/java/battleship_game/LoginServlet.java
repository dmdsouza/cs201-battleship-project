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

@WebServlet("/Login")
	public class LoginServlet extends HttpServlet {
		
		
		private static final long serialVersionUID = 1L;
	    
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			PrintWriter out = response.getWriter();
			try {
      Connection conn = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
      
      response.setContentType("text/html");
      String Username = request.getParameter("username");
	  String Password = request.getParameter("password");
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");}
				catch (ClassNotFoundException e) {
				      System.out.println(e.getMessage());
				      out.println("jdbc driver error");
			    }
			
			
			try{
				conn = DriverManager.getConnection("jdbc:mysql://35.236.121.113:3306/LoginDetails?&user=root&password=root");
			
			ps = conn.prepareStatement("SELECT * from LoginDetails.loginInfo WHERE username=? AND password= ?;");
			ps.setString(1, Username);
			ps.setString(2, Password);
			rs=  ps.executeQuery();
      
      if (rs.next()) {
				out.println("exists");
			}
			
			else {
				out.println("notFound");
				
			}
      
      
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
			out.println("sql error");
		}
    } catch (Exception sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
			out.println("error");
		}
			
			out.flush();	
		}
			
}