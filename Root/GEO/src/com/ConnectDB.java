package com;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import com.mysql.jdbc.Driver;
public class ConnectDB {
	public Connection dbConn = null;
	public void ConnectDB(){
		
	}
	
	/**
	 * Get database connection.
	 * @return 
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/geo?dontTrackOpenResources=true", 
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
