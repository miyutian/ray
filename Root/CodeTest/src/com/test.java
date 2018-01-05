package com;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class test {

	Timestamp ts = new Timestamp(System.currentTimeMillis());   
    String tsStr = "2011-05-09 11:49:45";   
    try {   
        ts = Timestamp.valueOf(tsStr);   
        System.out.println(ts);   
    } catch (Exception e) {   
        e.printStackTrace();   
    }  
	}

}
