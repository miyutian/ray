package com;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class ConnectDB {
	public void ConnectDB(){
		
	}
	public Connection dbConn = null;
	/**
	 * Get database connection.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?dontTrackOpenResources=true", 
				"root", "");
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
