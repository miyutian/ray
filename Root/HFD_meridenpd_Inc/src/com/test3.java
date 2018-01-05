package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class test3 {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		new test3().test3();
	}
	public void test3() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Connection dbConn = new ConnectDB().connect();
		String incidentIDSQL = "SELECT incidentID from hfd_meridenpd_inc_info_v2";
		PreparedStatement pstmIncidentID = dbConn.prepareStatement(incidentIDSQL);
		ResultSet retsult = pstmIncidentID.executeQuery();
		ArrayList<String> IdList = new ArrayList<String>();
		IdList.add("2018000528");
		IdList.add("2018000653");
		IdList.add("2018000584");
		while(retsult.next()){
			System.out.println("save DB sucessfully!"+retsult.getString(1));
			//if(IdList.contains(String.valueOf(retsult.getString(1)))){
			if(IdList.contains(retsult.getString(1))){
				System.out.println("con");
			}
		}
	}
}
