package com.tci.shp;

import java.util.Date;

import com.tci.dao.DBConnector;



public class TomTom2Mysql {
    public static void main(String[] args) {
   	try {	    
   	    if (args.length != 7) {
   		throw new Exception("Invalid input parameters!");
   	    }
   	    
   	    String country = args[0];
   	    String type = args[1];
   	    String folderPath = args[2];
   	    String[] dbString = args[3].split(":");
   	    String host = dbString[0];
   	    String port = dbString[1];
   	    String userName = args[4];
   	    String password = args[5];
   	    String schema = args[6];
   	    
   	    // init DB config
   	    DBConnector.getInstance().setDbHost(host);
   	    DBConnector.getInstance().setDbPort(Short.parseShort(port));
   	    DBConnector.getInstance().setUser(userName);
   	    DBConnector.getInstance().setPwd(password);
   	    DBConnector.getInstance().setSchema(schema);
   	    
   	    long startTime = System.currentTimeMillis();
   	    if (type.equalsIgnoreCase("street")) {
   		System.out.println("-----Start to process " + country + " street tables-----" + new Date());
   		new Street2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("maneuver")) {
   		System.out.println("-----Start to process " + country + " maneuver tables-----" + new Date());
   		new Maneuver2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("origFRC")) {
   		System.out.println("-----Start to process " + country + " origFRC tables-----" + new Date());
   		new OrigFRC2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("origFOW")) {
   		System.out.println("-----Start to process " + country + " origFOW tables-----" + new Date());
   		new OrigFOW2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("exitNum")) {
   		System.out.println("-----Start to process " + country + " exitNum tables-----" + new Date());
   		new ExitNum2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("routeNum")) {
   		System.out.println("-----Start to process " + country + " routeNum tables-----" + new Date());
   		new RouteNum2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("kph")) {
   		System.out.println("-----Start to process " + country + " kph tables-----" + new Date());
   		new KPH2Mysql(country, folderPath).run();
   	    } else if (type.equalsIgnoreCase("sr")) {
   		System.out.println("-----Start to process " + country + " sr tables-----" + new Date());
   		new SR2Mysql(country, folderPath).run();
   	    }
   	    
   	    long used = (System.currentTimeMillis() - startTime) / 1000;
   	    System.out.println("    Use " + used / 60 + " mins.");
   	    System.out.println();
   	} catch (Exception e) {
   	    e.printStackTrace();
   	    //System.err.println("Invalid input parameters!");
   	    //System.err.println("Input parameters sample: nt2mysql USA street D:\\14r2\\D1CN14200ND1000AABEH 192.168.111.15:3306 trafficcast naitou test");
   	    System.exit(0);
   	}
       }
}
