package com.tci.shp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class CheckFiles {
    
    private String fileSeparator = System.getProperty("file.separator");
    
    public static void main(String[] args) {
	try {
	    if (args.length != 1) {
		throw new Exception("Invalid Parmaeters");
	    }

	    String inputDir = args[0];
	    long startTime = System.currentTimeMillis();
	    System.out.println("-----Start-------" + new Date());

	    SevenZip.initSevenZipFromPlatformJAR();
	    
	    new CheckFiles().run(inputDir);

	    long used = (System.currentTimeMillis() - startTime) / 1000;
	    System.out.println("    Use " + used / 60 + " mins.");
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(0);
	}
    }

    public void run(String inputDir) throws Exception {
	File folder = new File(inputDir);
	File[] files = folder.listFiles();
	for (int i = 0; i < files.length; i++) {
	    if (files[i].isFile() && files[i].getName().endsWith("7z.001")) {
		
		
		
		RandomAccessFile randomAccessFile = null;
		ISevenZipInArchive inArchive = null;
	        try {
	            randomAccessFile = new RandomAccessFile(files[i], "r");
	            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
	            System.out.println(files[i].getName() + ", total files num: " + inArchive.getNumberOfItems());
	            
	            int count = 0;
	            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
	            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
	        	//System.out.println("    " + item.getPath());
	        	if (item.isFolder()) {
	        	    continue;
	        	}
	        	String path = item.getPath();        	
	        	//System.out.println("	" + path);  
	        	if (path.lastIndexOf(fileSeparator) != -1 && (path.indexOf("___________nw.dbf.gz") != -1 || path.indexOf("___________nw.prj.gz") != -1 
	        		|| path.indexOf("___________nw.shp.gz") != -1 || path.indexOf("___________nw.shx.gz") != -1 
	        		|| path.indexOf("___________gc.dbf.gz") != -1 || path.indexOf("___________a8.dbf.gz") != -1
	        		|| path.indexOf("___________a8.prj.gz") != -1 || path.indexOf("___________a8.shp.gz") != -1
	        		|| path.indexOf("___________a8.shx.gz") != -1 || path.indexOf("___________rd.dbf.gz") != -1
	        		|| path.indexOf("___________mn.dbf.gz") != -1 || path.indexOf("___________mp.dbf.gz") != -1)) {
	        	    //System.out.println("	" + path);
	        	    count++;
	        	}
	            }
	            System.out.println("	Extract count: " + count);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (inArchive != null) {
	                try {
	                    inArchive.close();
	                } catch (SevenZipException e) {
	                    System.err.println("Error closing archive: " + e);
	                }
	            }
	            if (randomAccessFile != null) {
	                try {
	                    randomAccessFile.close();
	                } catch (IOException e) {
	                    System.err.println("Error closing file: " + e);
	                }
	            }
	        }
		
	    }	   
	}
    }
}
