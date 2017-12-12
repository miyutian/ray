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

public class SR2DB {
	private void SR2DB(){
	
	}
	public static void main(String args[]) throws Exception{
		//SR2DB sr = new SR2DB();
		//sr.srtable();
		
	}
	public void srtable(ArrayList<String> srList) throws Exception{
		for(int i = 0; i < srList.size();i++){
			if(srList.get(i).indexOf("___________sr.dbf")!=-1){
				Connection dbConn = new ConnectDB().connect();
		        String insertSQL = "insert into srtable values(?,?,?,?,?,?,?)";
		        PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
			    InputStream fis = null;  
			    try {  
			    	String File = srList.get(i);
			    	
			    	int batchCount = 0;
			        // 读取文件的输入流  
			        // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息  
			        DbaseFileReader reader = new DbaseFileReader(new FileInputStream(File).getChannel(), false, Charset.forName("UTF-8")); 
			        // 调用DBFReader对实例方法得到path文件中字段的个数   
			        // 一条条取出path文件中记录  
			        while (reader.hasNext()) { 
			        	Row row = reader.readRow();
			        		//System.out.print(row.read(0)+"===>");  
			                //System.out.println(row.read(5));  
			                pstm.setLong(1, (Long) row.read(0));
			                pstm.setInt(2, (Integer) row.read(1));
			                pstm.setInt(3, (Integer) row.read(2));
			                pstm.setString(4, (String) row.read(3));
			                pstm.setInt(5, (Integer) row.read(4));
			    			pstm.setString(6, (row.read(5).toString()));
			    			pstm.setInt(7, (Integer) row.read(6));
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
}
