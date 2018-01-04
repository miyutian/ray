package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteTxt {
	public void WriteTxt(String strBufferID,String strBufferID2,String strBufferID3){
		try  
		{      
		  // 鍒涘缓鏂囦欢瀵硅薄  
		String strFilename = "E:\\name1.txt";
		  File fileText = new File(strFilename);  
		  // 鍚戞枃浠跺啓鍏ュ璞″啓鍏ヤ俊鎭� 
		  FileWriter fileWriter = new FileWriter(fileText, true);  

		  // 鍐欐枃浠�
		  fileWriter.write(strBufferID); 
		  fileWriter.write(","); 
		  fileWriter.write(strBufferID2);
		  fileWriter.write(","); 
		  fileWriter.write(strBufferID3 +"\r\n");

		  // 鍏抽棴  
		  fileWriter.close();  
		}  
		catch (IOException e)  
		{  
		  //  
		  e.printStackTrace();  
		} 	
	}	
}
