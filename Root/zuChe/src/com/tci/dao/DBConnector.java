package com.tci.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
    private static DBConnector instance = new DBConnector();

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DB_DRIVER = "jdbc:mysql";

    private String dbHost = "192.168.200.15";

    private short dbPort = 3306;

    private String user = "trafficcast";

    private String pwd = "naitou";
    
    private String schema = "test";

    private DBConnector() {
    }

    public static DBConnector getInstance() {
	return instance;
    }
   
    public Connection connectToDB() throws Exception {
	String path = DB_DRIVER + "://" + dbHost + ":" + dbPort + "/" + schema + "?dontTrackOpenResources=true";
	Class.forName(JDBC_DRIVER).newInstance();
	return DriverManager.getConnection(path, user, pwd);
    }

    public String getDbHost() {
	return dbHost;
    }

    public void setDbHost(String dbHost) {
	this.dbHost = dbHost;
    }

    public short getDbPort() {
	return dbPort;
    }

    public void setDbPort(short dbPort) {
	this.dbPort = dbPort;
    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public String getPwd() {
	return pwd;
    }

    public void setPwd(String pwd) {
	this.pwd = pwd;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}