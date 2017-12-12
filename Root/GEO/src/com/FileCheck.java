package com;

import java.awt.List;
import java.awt.geom.Path2D;
import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.geotools.data.ows.GetCapabilitiesRequest;
import org.geotools.data.shapefile.files.ShpFiles;

public class FileCheck {
	public static void main(String[] args) throws Exception {
		FileCheck FC = new FileCheck();
		FC.FolderCheck();
	}
	public ArrayList<String> FolderCheck() throws Exception{
		int FolderCount = 0;
		int FolderCountCompare = 0;
		FileCheck FC = new FileCheck();
		String path = FC.GetPath();
		File folder = new File(path);
		File[] files = folder.listFiles();
		ArrayList<String> GzPathlist = new ArrayList(); 
		for (int i = 0; i < files.length; i++) {
		//check files in each folder
			String StatePath = path+'\\'+files[i].getName();
			//System.out.println("StatePath---------"+StatePath);
			File StateFolders = new File(StatePath);
			File[] gzfiles = StateFolders.listFiles();
			for (int j = 0; j < gzfiles.length; j++){
				//System.out.println(gzfiles[j].getName());
				String ShpFiles = gzfiles[j].getName();
				//System.out.println("ShpFiles======>"+ShpFiles);
				String ShpPath = StatePath+"\\\\"+ ShpFiles;
				//System.out.println(ShpPath);
				//save gz path to a list
				if ((ShpFiles.indexOf("___________nw.dbf.gz") != -1 || ShpFiles.indexOf("___________nw.prj.gz") != -1 
		        		|| ShpFiles.indexOf("___________nw.shp.gz") != -1 || ShpFiles.indexOf("___________nw.shx.gz") != -1 
		        		|| ShpFiles.indexOf("___________gc.dbf.gz") != -1 || ShpFiles.indexOf("___________gc.shx.gz") != -1 
		        		|| ShpFiles.indexOf("___________gc.shp.gz") != -1 || ShpFiles.indexOf("___________gc.prj.gz") != -1 
		        		|| ShpFiles.indexOf("___________a8.dbf.gz") != -1 || ShpFiles.indexOf("___________si.dbf.gz") != -1
		        		|| ShpFiles.indexOf("___________a8.prj.gz") != -1 || ShpFiles.indexOf("___________a8.shp.gz") != -1
		        		|| ShpFiles.indexOf("___________sp.dbf.gz") != -1 || ShpFiles.indexOf("___________rn.dbf.gz") != -1
		        		|| ShpFiles.indexOf("___________a8.shx.gz") != -1 || ShpFiles.indexOf("___________rd.dbf.gz") != -1
		        		|| ShpFiles.indexOf("___________mn.dbf.gz") != -1 || ShpFiles.indexOf("___________mp.dbf.gz") != -1)
		        		|| ShpFiles.indexOf("___________mn.dbf.gz") != -1) {
		        	    System.out.println("	      " + ShpFiles+"    new");
		        	    GzPathlist.add(ShpPath);
		        	}
			}
			FolderCount ++;
			FolderCountCompare = GzPathlist.size();
			//System.out.println("FolderCountCompare"+FolderCountCompare);
			if (FolderCount*19 != FolderCountCompare){
				System.err.println("Can't find some needed file in：："+ StatePath);
			}
		}return GzPathlist;
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
