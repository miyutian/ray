package com;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;

public class MN_MP2DB {
	private void MN_MP2DB(){
		
	}
	public void mntable()throws Exception{
		Connection dbcon = new ConnectDB().connect();
		String insertSQL = "insert into mntable values(?,?,?,?,?)";
		PreparedStatement pstm = dbcon.prepareStatement(insertSQL);
		int batchCount = 0;
		String file = "E:\\mn.dbf";
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(1));
			pstm.setInt(3, (Integer) row.read(2));
			pstm.setString(4, row.read(3).toString());
			pstm.setLong(5, (Long) row.read(4));
			pstm.addBatch();
		        batchCount++;
		        if (batchCount == 4000) {
		            pstm.executeBatch();
		            pstm.clearBatch();
		            batchCount = 0;
		        }
		}
		pstm.executeBatch();	
		pstm.close();
		reader.close();
	    
	
	}
	public void mptable()throws Exception{
		Connection dbcon = new ConnectDB().connect();
		String insertSQL = "insert into mptable values(?,?,?,?)";
		PreparedStatement pstm = dbcon.prepareStatement(insertSQL);
		int batchCount = 0;
		String file = "E:\\mp.dbf";
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(file).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			pstm.setLong(1, (Long) row.read(0));
			pstm.setInt(2, (Integer) row.read(1));
			pstm.setLong(3, (Long) row.read(2));
			pstm.setInt(4, (Integer) row.read(3));
			pstm.addBatch();
		        batchCount++;
		        if (batchCount == 4000) {
		            pstm.executeBatch();
		            pstm.clearBatch();
		            batchCount = 0;
		        }
		}
		pstm.executeBatch();	
		pstm.close();
		reader.close();
	}

	
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		MN_MP2DB mnmp = new MN_MP2DB();
		mnmp.mntable();
		mnmp.mptable();
	}

}
