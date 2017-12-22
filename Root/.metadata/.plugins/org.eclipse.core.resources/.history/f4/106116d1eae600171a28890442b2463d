package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteTxt {
	public void WriteTxt(String strBufferID){
		try  
		{      
		  // 创建文件对象  
		String strFilename = "E:\\name.txt";
		  File fileText = new File(strFilename);  
		  // 向文件写入对象写入信息  
		  FileWriter fileWriter = new FileWriter(fileText, true);  

		  // 写文件
		  fileWriter.write(strBufferID);  

		  // 关闭  
		  fileWriter.close();  
		}  
		catch (IOException e)  
		{  
		  //  
		  e.printStackTrace();  
		} 	
	}	
}
