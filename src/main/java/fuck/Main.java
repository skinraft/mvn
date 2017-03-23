package fuck;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import mvn.sql.DBUtil;

public class Main {
	private static final Logger LOG = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LOG.debug("卧槽，卧槽");
		LOG.error("哈哈，");
		LOG.info("控制台信息");
		try {
			Connection c=DBUtil.getConnectionAli();
			LOG.debug("c="+(c==null?"null":"cunzai"));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
