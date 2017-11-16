package com;
import java.io.File;  
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Iterator;   
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;  
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;  
import org.opengis.feature.simple.SimpleFeatureType;

//import com.tci.shp.String;

import com.vividsolutions.jts.geom.Point;



  
public class testLine {  
	private Connection dbConn = null;
	public static void main(String[] args) throws Exception {  
		testLine std = new testLine();
		std.shptodb();
    }  
	public void shptodb() throws Exception{
		 ShapefileDataStore shpDataStore = null;  
	        int batchCount = 0;
	        try{  
	            shpDataStore = new ShapefileDataStore(new File("E:\\nw.shp").toURI().toURL());  
	            //shpDataStore.setStringCharset(Charset.forName("GBK"));  
	            String typeName = shpDataStore.getTypeNames()[0]; 
	            //System.out.println("typeName===>"+typeName);
	            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;   
	            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>)shpDataStore.getFeatureSource(typeName);
	            //FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
	            
	            String insertSQL = "insert into 15r3_raw_ntlinks_usa(LINK_ID,F_NODE_ID,T_NODE_ID,LINK_GEOM) values(?,?,?,GeomFromText(?))";
	            dbConn = new ConnectDB().connect();
	            PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
	            //存储地理对象
	            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
	            //System.out.println(result.size());  
	            FeatureIterator<SimpleFeature> itertor = result.features();  
	            while(itertor.hasNext()){  
	                SimpleFeature feature = itertor.next();   
	                int paraCount = 1;
	                Collection<Property> p = feature.getProperties(); 
	                Iterator<Property> it = p.iterator(); 
	                while(it.hasNext()) {  
	                	
	                    Property pro = it.next();  
	                    if (!(pro.getValue() instanceof Point)) {  
	                        //System.out.println(pro.getName() + " = " + pro.getValue()); 
	                    	//System.out.println(pro.getName() + " = " + pro.getAttribute("ID")); 
	                    }  
	                }
	                String geometry = feature.getDefaultGeometryProperty().getValue().toString();
	                System.out.println(feature.getAttribute("ID"));
	                pstm.setLong(paraCount++, (Long) feature.getAttribute("ID"));
	                pstm.setLong(paraCount++, (Long) feature.getAttribute("F_JNCTID"));
	                pstm.setLong(paraCount++, (Long) feature.getAttribute("T_JNCTID"));

	    			geometry = geometry.replaceFirst("MULTILINESTRING \\(",	"linestring");
	    			geometry = geometry.substring(0, geometry.length() - 1);
	    			pstm.setString(paraCount++, geometry.toString());
	    			pstm.addBatch();
	    			batchCount++;
	    			batchCount++;
	    			if (batchCount == 4000) {
	    			    pstm.executeBatch();
	    			    pstm.clearBatch();
	    			    batchCount = 0;
	    			}
	    		    }
	    		    pstm.executeBatch();
	            itertor.close();  
	        } catch (MalformedURLException e) {  
	            e.printStackTrace();  
	        } catch(IOException e) { e.printStackTrace(); }  
	}
     
}  
