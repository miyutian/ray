package com;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class ConnectDB {
	public Connection dbConn = null;
	/**
	 * Get database connection.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/geo?useUnicode=true&characterEncoding=UTF-8", 
				"root", "");
		return dbConn;
	} // end of connect
	/**
	 * Disconnect with db.
	 */
	public void disconnect() {
		if (dbConn != null) {
			try {
				dbConn.close();
			} catch (SQLException e) {
				dbConn = null;
			}
		}
	} // end of disconnect
}
