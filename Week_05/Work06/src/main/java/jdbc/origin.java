package jdbc;

import java.sql.*;

public class origin {

    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql:///test", "root", "root");
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select username,password from account");
        while (rs.next()) {
            System.out.println("用户名:" + rs.getString("username"));
            System.out.println("密码:" + rs.getString("username"));
        }
        rs.close();
        statement.close();
        connection.close();
    }
}