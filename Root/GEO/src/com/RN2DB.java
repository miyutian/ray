package com;
import java.io.FileInputStream;  
import java.io.InputStream;  
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.mysql.jdbc.Driver;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader.Row;


public class RN2DB {
    private String[] fnameTypes = {"ABBEY","ACRES","ALLEY","ALT","ALY","ANX","ARC","ARCH","AVE","BAY","BCH","BEACH","BEND","BG","BLF","BLFS","BLVD","BND","BR","BRG","BRK","BRKS","BTM","BUS","BYP","BYPASS","BYU","BYWAY","CAMPUS","CAPE","CDS","CHASE","CIR","CIRCT","CIRS","CLB","CLF","CLFS","CLOSE","CMN","CMNS","COMMON","CONC","CONN","COR","CORS","COVE","CP","CPE","CRES","CRK","CRNRS","CROSS","CRSE","CRST","CRT","CSWY","CT","CTR","CTS","CURV","CV","CVS","CYN","DALE","DELL","DIVERS","DL","DM","DOWNS","DR","DRS","DV","END","EST","ESTATE","ESTS","EXPY","EXT","EXTEN","EXTS","FALL","FARM","FIELD","FLD","FLDS","FLS","FLT","FLTS","FOREST","FRD","FRG","FRK","FRKS","FRONT","FRST","FRY","FT","FWY","GATE","GDN","GDNS","GLADE","GLEN","GLN","GREEN","GRN","GRNDS","GRNS","GROVE","GRV","GRVS","GTWY","HARBR","HBR","HEATH","HGHLDS","HILL","HL","HLS","HOLLOW","HOLW","HTS","HVN","HWY","INLET","INLT","IS","ISLAND","ISLE","ISS","JCT","KEY","KNL","KNLS","KNOLL","KY","KYS","LAND","LANDNG","LANE","LCK","LCKS","LDG","LF","LGT","LGTS","LINE","LINK","LK","LKOUT","LKS","LMTS","LN","LNDG","LOOP","MALL","MANOR","MAZE","MDW","MDWS","MEADOW","MEWS","ML","MLS","MNR","MNRS","MOUNT","MSN","MT","MTN","MTWY","NCK","OPAS","ORCH","OVAL","PARADE","PARK","PASS","PATH","PIKE","PINES","PK","PKWY","PKY","PL","PLAT","PLAZA","PLN","PLNS","PLZ","PNE","PNES","PORT","PR","PROM","PRT","PSGE","PT","PTS","PTWAY","PVT","QUAY","RADL","RAMP","RD","RDG","RDGS","RDS","RG","RIDGE","RISE","RIV","RNCH","ROAD","ROW","RPD","RPDS","RST","RTE","RUN","SHL","SHLS","SHR","SHRS","SKWY","SMT","SPG","SPGS","SPUR","SQ","SQS","ST","STA","STRA","STRM","STS","SUBDIV","TER","TERR","THICK","TLINE","TPKE","TRAIL","TRAK","TRCE","TRFY","TRL","TRLR","TRNABT","TRWY","TUNL","UN","UPAS","VALE","VIA","VIEW","VILLAS","VILLGE","VIS","VISTA","VL","VLG","VLGS","VLY","VW","VWS","WALK","WALL","WAY","WAYS","WHARF","WL","WLS","WOOD","WYND","XING","XRD","XRDS"};
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		RN2DB routenum = new RN2DB();
		routenum.rn2db();
	}
	private void RN2DB(){
	
	}
	public void rn2db()throws Exception{
		String File = "E:\\rn.dbf";
		Connection dbcon = new ConnectDB().connect();
		String insertSQL = "insert into rntable values(?,?,?,?,?,?)";
		PreparedStatement pstm = dbcon.prepareStatement(insertSQL);
		int batchCount = 0;
		
		DbaseFileReader reader = new DbaseFileReader(new FileInputStream(File).getChannel(), false, Charset.forName("UTF-8"));
		while (reader.hasNext()) {
			Row row = reader.readRow();
			int feat_type = (Integer) row.read(1);
			if (feat_type != 4110) {
			    continue;
			}
			
			long link_id = (Long) row.read(0);
			String routeNum = StrFormat.convertNullStringToEnptyAndUppercase((String) row.read(3)).trim();
			String routeName = StrFormat.convertNullStringToEnptyAndUppercase((String) row.read(4)).trim();
			String fname_pref = StrFormat.convertNullStringToEnptyAndUppercase((String) row.read(17)).trim();
			String fname_suff = StrFormat.convertNullStringToEnptyAndUppercase((String) row.read(16)).trim();
			
			if (!routeNum.equals("")) {
				System.out.println("!routeNum.equals('')");
			    String fname_base = routeNum;
			    String fname_type = "";
			    if (!fname_pref.equals("") && fname_base.startsWith(fname_pref + " ")) {
				fname_base = fname_base.substring(fname_pref.length() + 1);
			    }
			    
			    if (!fname_suff.equals("") && fname_base.endsWith(" " + fname_suff)) {
				fname_base = fname_base.substring(0, fname_base.length() - fname_suff.length() - 1);
			    }
			    
			    for (int i = 0; i < fnameTypes.length; i++) {
				if (fname_base.endsWith(" " + fnameTypes[i])) {
				    fname_base = fname_base.substring(0, fname_base.length() - fnameTypes[i].length() - 1);
				    fname_type = fnameTypes[i];
				    break;
				}
			    }
			    
			    pstm.setLong(1, link_id);
			    pstm.setString(2, fname_pref);
			    pstm.setString(3, "");
			    pstm.setString(4, fname_base);
			    pstm.setString(5, fname_suff);
			    pstm.setString(6, fname_type);
			    pstm.addBatch();
			    batchCount++;
			}
			
			if (!routeName.equals("") && !routeName.equals(routeNum)) {
			    String fname_base = routeName;
			    String fname_type = "";
			    if (!fname_pref.equals("") && fname_base.startsWith(fname_pref + " ")) {
				fname_base = fname_base.substring(fname_pref.length() + 1);
			    }
			    
			    if (!fname_suff.equals("") && fname_base.endsWith(" " + fname_suff)) {
				fname_base = fname_base.substring(0, fname_base.length() - fname_suff.length() - 1);
			    }
			    
			    for (int i = 0; i < fnameTypes.length; i++) {
				if (fname_base.endsWith(" " + fnameTypes[i])) {
				    fname_base = fname_base.substring(0, fname_base.length() - fnameTypes[i].length() - 1);
				    fname_type = fnameTypes[i];
				    break;
				}
			    }
			    
			    pstm.setLong(1, link_id);
			    pstm.setString(2, fname_pref);
			    pstm.setString(3, "");
			    pstm.setString(4, fname_base);
			    pstm.setString(5, fname_suff);
			    pstm.setString(6, fname_type);
			    pstm.addBatch();
			    batchCount++;
			}		
		        
		        if (batchCount >= 4000) {
		            pstm.executeBatch();
		            pstm.clearBatch();
		            batchCount = 0;
		        }
		}
		pstm.executeBatch();	
		pstm.close();
		reader.close();
	    }

}
