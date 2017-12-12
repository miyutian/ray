package com;
import java.io.FileInputStream;  
import java.io.InputStream;  
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.mysql.jdbc.Driver;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;

public class SI2DB {  
	
	public void sitodb(ArrayList<String> siList) throws Exception{  
		for(int i = 0; i < siList.size();i++){
			if(siList.get(i).indexOf("___________si.dbf")!=-1){
				Connection dbConn = new ConnectDB().connect();
		        String insertSQL = "insert into sitable values(?,?)";
		        PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
			    InputStream fis = null;  
			    try {  
			    	String File = siList.get(i);
			    	
			    	int batchCount = 0;
			        // 读取文件的输入流  
			        // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息  
			        DbaseFileReader reader = new DbaseFileReader(new FileInputStream(File).getChannel(), false, Charset.forName("UTF-8")); 
			        // 调用DBFReader对实例方法得到path文件中字段的个数   
			        // 一条条取出path文件中记录  
			        while (reader.hasNext()) { 
			        	Row row = reader.readRow();
			        	if ("4E".equals(row.read(3).toString())) {
			        		//System.out.print(row.read(0)+"===>");  
			                //System.out.println(row.read(5));  
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
	    }  
	}  
	public void sptodb(ArrayList<String> spList) throws Exception{  
		for(int i = 0; i < spList.size();i++){
			if(spList.get(i).indexOf("___________sp.dbf")!=-1){
				Connection dbConn = new ConnectDB().connect();
		        String insertSQL = "insert into sptable values(?,?)";
		        PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
			    InputStream fis = null;  
			    try {  
			    	String File = spList.get(i);
			    	
			    	int batchCount = 0;
			        // 读取文件的输入流  
			        // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息  
			        DbaseFileReader reader = new DbaseFileReader(new FileInputStream(File).getChannel(), false, Charset.forName("UTF-8")); 
			        while (reader.hasNext()) { 
			        	Row row = reader.readRow();
			        		//System.out.print(row.read(0)+"===>");  
			                //System.out.println(row.read(2));  
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
	    }  
	}  
	 
		public static void main(String[] args) throws Exception {  
	      //SI2DB exit = new SI2DB();
	 	  //exit.sitodb();
	 	  //exit.sptodb();
	    }  
}