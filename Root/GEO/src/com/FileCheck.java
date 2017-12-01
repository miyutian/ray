package com;

import java.awt.geom.Path2D;
import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.geotools.data.ows.GetCapabilitiesRequest;

public class FileCheck {
	public static void main(String[] args) throws Exception{
		FileCheck FC = new FileCheck();
		String path = FC.GetPath();
		FC.FolderCheck(path);
	}
	public void FolderCheck(String path){
		File folder = new File(path);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
		//check files in each folder
			String StatePath = path+'\\'+files[i].getName();
			System.out.println(StatePath);
			File StateFolders = new File(StatePath);
			File[] gzfiles = folder.listFiles();
			for (int j = 0; j < gzfiles.length; j++){
				System.out.println(gzfiles[j].getName());
			}
		}
	}
	public String GetPath()throws Exception{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			   String str = "";
			   String str1 = "";
			   fis = new FileInputStream("D:\\path.txt");// FileInputStream
			   // 从文件系统中的某个文件中获取字节
			    isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			    br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
			    
			}catch (IOException e) {
			    e.printStackTrace();
			   }
		return br.readLine();
		
	}
}
