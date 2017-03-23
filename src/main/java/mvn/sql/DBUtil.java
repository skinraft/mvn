package mvn.sql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.LogManager;

import com.alibaba.druid.pool.DruidDataSource;

public class DBUtil {
	// 119.29.187.217
	private static String username = "root";
	private static String password = "123456";
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/smart_cabinet?useSSL=false&characterEncoding=UTF-8&useUnicode=true";

	/***
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public synchronized static Connection getConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName(driver).newInstance();
		return (Connection) DriverManager.getConnection(url, username, password);
	}

	/**
	 * 阿里获取数据库连接池里面的连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public synchronized static Connection getConnectionAli()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		@SuppressWarnings("resource")
		DruidDataSource druidDataSource = new DruidDataSource();
		try {
			InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("druid.properties");
			Properties props = new Properties();
			props.load(in);
			druidDataSource.setUrl(props.getProperty("jdbc.url"));
			druidDataSource.setDriverClassName(props.getProperty("jdbc.driverClassName"));
			druidDataSource.setUsername(props.getProperty("jdbc.username"));
			druidDataSource.setPassword(props.getProperty("jdbc.password"));
			druidDataSource.setValidationQuery(props.getProperty("jdbc.validationQuery"));
			druidDataSource.setInitialSize(Integer.parseInt(props.getProperty("jdbc.initialSize")));
			druidDataSource.setMaxActive(Integer.parseInt(props.getProperty("jdbc.maxActive")));
			druidDataSource.setMinIdle(Integer.parseInt(props.getProperty("jdbc.minIdle")));
			druidDataSource.setMaxWait(Long.parseLong(props.getProperty("jdbc.maxWait")));
			druidDataSource.setTestOnBorrow(Boolean.parseBoolean(props.getProperty("jdbc.testOnBorrow")));
			druidDataSource.setTestOnReturn(Boolean.parseBoolean(props.getProperty("jdbc.testOnReturn")));
			druidDataSource.setTestWhileIdle(Boolean.parseBoolean(props.getProperty("jdbc.testWhileIdle")));
			druidDataSource.setTimeBetweenEvictionRunsMillis(
					Long.parseLong(props.getProperty("jdbc.timeBetweenEvictionRunsMillis")));
			druidDataSource.setMinEvictableIdleTimeMillis(
					Long.parseLong(props.getProperty("jdbc.minEvictableIdleTimeMillis")));
			druidDataSource.setRemoveAbandoned(Boolean.parseBoolean(props.getProperty("jdbc.removeAbandoned")));
			druidDataSource
					.setRemoveAbandonedTimeout(Integer.parseInt(props.getProperty("jdbc.removeAbandonedTimeout")));
			druidDataSource.setLogAbandoned(Boolean.parseBoolean(props.getProperty("jdbc.logAbandoned")));
		} catch (Exception e) {
			e.printStackTrace();
			LogManager.getLogger(DBUtil.class).error(e.getMessage());
		}
		return druidDataSource.getConnection();
	}

}
