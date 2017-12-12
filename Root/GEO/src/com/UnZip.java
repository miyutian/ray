package com;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

public class UnZip {

	public static void main(String[] args) throws Exception{

		UnZip uz = new UnZip();
		//String aaa = "E:\\Industrial standard Docments\\TomTom\\chnc11___________2r.dbf.gz";
		uz.UnZipgz();
	}
	public ArrayList<String> UnZipgz() throws Exception{
		FileCheck FC = new FileCheck();
		ArrayList<String> PathList = FC.FolderCheck();
		ArrayList<String> ShpPathList = new ArrayList();
		try{
			for(int i = 0 ; i<PathList.size(); i++){
				String ShpPath = PathList.get(i);
				String ouputfile = "";
				//建立gzip压缩文件输入流 
				FileInputStream file = new FileInputStream(ShpPath);
				//建立gzip解压工作流
				GZIPInputStream gzin = new GZIPInputStream(file);
				//建立解压文件输出流
				ouputfile = ShpPath.substring(0,ShpPath.lastIndexOf(".gz"));
				ShpPathList.add(ouputfile);
				FileOutputStream fout = new FileOutputStream(ouputfile);
				int num;
				byte[] buf=new byte[1024];
				while ((num = gzin.read(buf,0,buf.length)) != -1){
					fout.write(buf,0,num);
				}
			gzin.close();   
			fout.close();   
			gzin.close();
			}

		}catch (Exception ex){  
			System.err.println(ex.toString());  
			}
		return ShpPathList;
	}

}
