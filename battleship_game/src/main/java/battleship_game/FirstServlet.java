//package battleship_game;
//
//import java.io.IOException;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//
//import com.google.appengine.api.utils.SystemProperty;
//import com.google.appengine.*;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import javax.servlet.http.*;`
//
//@WebServlet("FirstServlet")
//public class FirstServlet extends HttpServlet {
//  public void doGet(HttpServletRequest req, HttpServletResponse res)
//      throws IOException {
//    res.setContentType("text/plain");
//
//    String url = null;
//    try {
//      if (SystemProperty.Environment.value() ==
//          SystemProperty.Environment.Value.Production) {
//        // Load the class that provides the new "jdbc:google:mysql://" prefix.
//        Class.forName("com.mysql.jdbc.GoogleDriver");
//        url = System.getProperty("cloudsql.url");
//      } else {
//        Class.forName("com.mysql.jdbc.Driver");
//        url = System.getProperty("cloudsql.url.dev");
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//      return;
//    }
//
//    try {
//      Connection conn = DriverManager.getConnection(url);
//
//      try {
//        Statement st = conn.createStatement();
//        ResultSet rs = st.executeQuery("SHOW DATABASES");
//        while (rs.next()) {
//          res.getWriter().println(rs.getString(1));
//        }
//        res.getWriter().println("-- done --");
//      } finally {
//        conn.close();
//      }
//    } catch (SQLException e) {
//      res.getWriter().println("SQLException: " + e.getMessage());
//    }
//
//  }
//}