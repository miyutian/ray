package com.tci.shp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;

public class TestDbf {

    public static void main(String[] args) throws Exception {
	DbaseFileReader reader = new DbaseFileReader(new FileInputStream(new File("C:\\Users\\jackie\\Desktop\\mexm01___________rd.dbf")).getChannel(), false, Charset.forName("UTF-8"));
	DbaseFileHeader header = reader.getHeader();  
	for (int i = 0; i < header.getNumFields(); i++) {
	    System.out.println(i + ": " + header.getFieldName(i) + ": " + header.getFieldType(i) + ", " + header.getFieldLength(i));
	}
	int count = 0;
	while (reader.hasNext()) {
	    Row row = reader.readRow();
	    System.out.println(count++ + ": " + (Long) row.read(0) + ", " + (String) row.read(1) + ", " + String.valueOf(row.read(2)) + ", " + row.read(2).getClass());
	    if (count > 2275) {
		break;
	    }
	}
	reader.close();
    }
}
