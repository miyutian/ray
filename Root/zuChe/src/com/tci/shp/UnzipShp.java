package com.tci.shp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;


public class UnzipShp {
    
    private String fileSeparator = System.getProperty("file.separator");
    
    /*
     * Param 1: input dir
     * Param 2: output dir
     * 
     */
    public static void main(String[] args) {
	try {	    
	    if (args.length != 2) {
		throw new Exception("Invalid Parmaeters");
	    }
	    
	    String inputDir = args[0];
	    String outputDir = args[1];
	    	    
	    long startTime = System.currentTimeMillis();
	    
	    System.out.println("-----Start to Unzip shp files-------" + new Date());
	    
	    new File(outputDir).mkdir();
	    
	    SevenZip.initSevenZipFromPlatformJAR();
	    
	    UnzipShp unzipTar = new UnzipShp();
	    unzipTar.run(inputDir, outputDir);
	    
	    
	    long used = (System.currentTimeMillis() - startTime) / 1000;
	    System.out.println("    Use " + used / 60 + " mins.");
	    System.out.println();
	} catch (Exception e) {
	    e.printStackTrace();
	    //System.err.println("Invalid input parameters!");
	    //System.err.println("Input parameters sample: unziptar d:\\input d::\\output");
	    System.exit(0);
	}
    }
    
    public void run(String inputDir, String outputDir) throws Exception {
	// unzip NT tar files
	File folder = new File(inputDir);
	File[] files = folder.listFiles();
	for (int i = 0; i < files.length; i++) {
	    if (files[i].isFile() && files[i].getName().endsWith("7z.001")) {
		System.out.println("Start to unzip " + files[i].getName());
		String subFolderName = outputDir + fileSeparator + files[i].getName().replaceAll("\\_", "").replaceAll("\\-", "").replaceAll("\\.", "");
		if (!new File(subFolderName).exists()) {
		    new File(subFolderName).mkdir();
		}
		
		RandomAccessFile randomAccessFile = null;
		ISevenZipInArchive inArchive = null;
	        try {
	            randomAccessFile = new RandomAccessFile(files[i], "r");
	            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));

	            //System.out.println("Count of items in archive: " + inArchive.getNumberOfItems());
	            
	            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
	            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
	        	//System.out.println("    " + item.getPath());
	        	if (item.isFolder()) {
	        	    continue;
	        	}
	        	final String path = item.getPath();	        	
	        	final String folderName = subFolderName;
	        	final int[] hash = new int[] { 0 };
	        	/*if (path.lastIndexOf(fileSeparator) != -1 && (path.indexOf("___________nw.dbf.gz") != -1 || path.indexOf("___________nw.prj.gz") != -1 
	        		|| path.indexOf("___________nw.shp.gz") != -1 || path.indexOf("___________nw.shx.gz") != -1 
	        		|| path.indexOf("___________gc.dbf.gz") != -1 || path.indexOf("___________a8.dbf.gz") != -1
	        		|| path.indexOf("___________a8.prj.gz") != -1 || path.indexOf("___________a8.shp.gz") != -1
	        		|| path.indexOf("___________a8.shx.gz") != -1 || path.indexOf("___________rd.dbf.gz") != -1
	        		|| path.indexOf("___________mn.dbf.gz") != -1 || path.indexOf("___________mp.dbf.gz") != -1)) {*/
	        	/*if (path.lastIndexOf(fileSeparator) != -1 && (path.indexOf("___________si.dbf.gz") != -1 || path.indexOf("___________sp.dbf.gz") != -1 
	        		|| path.indexOf("___________rn.dbf.gz") != -1 )) {*/
	        	if (path.lastIndexOf(fileSeparator) != -1 && (path.indexOf("___________sr.dbf.gz") != -1)) {
	        	    
	        	    
	        	    ExtractOperationResult result;

	                    final long[] sizeArray = new long[1];
	                    result = item.extractSlow(new ISequentialOutStream() {
	                        public int write(byte[] data) throws SevenZipException {
	                            FileOutputStream fos;
	                            try {	                        	    
        	                            File file = new File(folderName + fileSeparator + path.substring(path.lastIndexOf(fileSeparator)));
        	                            fos = new FileOutputStream(file, true);
        	                            fos.write(data);
        	                            fos.flush();
        	                            fos.close();        	                            
	                            } catch (Exception e) {
	                        	e.printStackTrace();
	                            }

	                            hash[0] ^= Arrays.hashCode(data); // Consume data
	                            sizeArray[0] += data.length;
	                            return data.length; // Return amount of consumed data
	                        }
	                    });

	                    if (result == ExtractOperationResult.OK) {
	                        //System.out.println(String.format("%9X | %10s | %s", hash[0], sizeArray[0], item.getPath()));
	                    } else {
	                        System.err.println("    Error: extracting item: " + result);
	                    }
	                    unzipGZ(folderName, path.substring(path.lastIndexOf(fileSeparator)));
	        	}	        	
	            }

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
    
    private void unzipGZ(String outputDir, String gzName) throws Exception {
	File gzFile = new File(outputDir + fileSeparator + gzName);
	GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(gzFile)));

	FileOutputStream fos = new FileOutputStream(outputDir + fileSeparator + gzName.substring(0, gzName.indexOf(".gz")));
	BufferedOutputStream dest = new BufferedOutputStream(fos);
	int count = 0;
	byte data[] = new byte[204800];
	while ((count = gis.read(data)) != -1) {
	    dest.write(data, 0, count);
	}
	dest.flush();
	dest.close();
	fos.close();

	gis.close();
	gzFile.delete();
    }
}
