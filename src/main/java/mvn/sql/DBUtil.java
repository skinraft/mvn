package mvn.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBUtil {
	//119.29.187.217
	private static String username = "root";
	private static String password = "123456";
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/smart_cabinet?useSSL=false&characterEncoding=UTF-8&useUnicode=true";

	public synchronized static Connection getConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName(driver).newInstance();
		return (Connection) DriverManager.getConnection(url, username, password);
	}

}
