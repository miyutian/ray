package com;
import java.io.FileInputStream;  
import java.io.InputStream;  
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.mysql.jdbc.Driver;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;

public class test {  
	
	public static void sitodb() throws Exception{  
		Connection dbConn = new ConnectDB().connect();
        String insertSQL = "insert into sitable values(?,?)";
        PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
	    InputStream fis = null;  
	    try {  
	    	String File = "E:\\si.dbf";
	    	
	    	int batchCount = 0;
	        // 读取文件的输入流  
	        // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息  
	        DbaseFileReader reader = new DbaseFileReader(new FileInputStream(File).getChannel(), false, Charset.forName("UTF-8")); 
	        // 调用DBFReader对实例方法得到path文件中字段的个数   
	        // 一条条取出path文件中记录  
	        while (reader.hasNext()) { 
	        	Row row = reader.readRow();
	        	if ("4E".equals(row.read(3).toString())) {
	        		System.out.print(row.read(0)+"===>");  
	                System.out.println(row.read(5));  
	                pstm.setLong(1, (Long) row.read(0));
	    			pstm.setString(2, (String) row.read(5));
	    			pstm.addBatch();
	    			batchCount++;
	    			if (batchCount == 4000) {
	    			    pstm.executeBatch();
	    			    pstm.clearBatch();
	    			    batchCount = 0;
	    			}
	        	} pstm.executeBatch();
	            }
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } finally {  
	        try { 
	        	pstm.close();
    		    dbConn.close();
	            fis.close();  
	        } catch (Exception e) {  
	        }  
	    }  
	}  
	public static void sptodb() throws Exception{  
		Connection dbConn = new ConnectDB().connect();
        String insertSQL = "insert into sptable values(?,?)";
        PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
	    InputStream fis = null;  
	    try {  
	    	String File = "E:\\sp.dbf";
	    	
	    	int batchCount = 0;
	        // 读取文件的输入流  
	        // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息  
	        DbaseFileReader reader = new DbaseFileReader(new FileInputStream(File).getChannel(), false, Charset.forName("UTF-8")); 
	        while (reader.hasNext()) { 
	        	Row row = reader.readRow();
	        		System.out.print(row.read(0)+"===>");  
	                System.out.println(row.read(2));  
	                pstm.setLong(1, (Long) row.read(0));
	    			pstm.setLong(2, (Long) row.read(2));
	    			pstm.addBatch();
	    			batchCount++;
	    			if (batchCount == 4000) {
	    			    pstm.executeBatch();
	    			    pstm.clearBatch();
	    			    batchCount = 0;
	    			}
	        	} pstm.executeBatch();
	            
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } finally {  
	        try { 
	        	pstm.close();
    		    dbConn.close();
	            fis.close();  
	        } catch (Exception e) {  
	        }  
	    }  
	}  
	 
		public static void main(String[] args) throws Exception {  
	      test exit = new test();
	 	  //exit.sitodb();
	 	  exit.sptodb();
	    }  
}