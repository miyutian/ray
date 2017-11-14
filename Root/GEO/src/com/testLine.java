package com;
import java.io.File;  
import java.io.IOException;
import java.net.MalformedURLException;
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
      
	public static void main(String[] args) {  
        ShapefileDataStore shpDataStore = null;  
        try{  
            shpDataStore = new ShapefileDataStore(new File("E:\\nw.shp").toURI().toURL());  
            //shpDataStore.setStringCharset(Charset.forName("GBK"));  
            String typeName = shpDataStore.getTypeNames()[0]; 
            //System.out.println("typeName===>"+typeName);
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;   
            featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>)shpDataStore.getFeatureSource(typeName);
            
            //FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
            //存储地理对象
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures(); 
            //System.out.println(result.size());  
            FeatureIterator<SimpleFeature> itertor = result.features();  
            while(itertor.hasNext()){  
                SimpleFeature feature = itertor.next();   
                
                Collection<Property> p = feature.getProperties(); 
                Iterator<Property> it = p.iterator(); 
                while(it.hasNext()) {  
                    Property pro = it.next();  
                    if (!(pro.getValue() instanceof Point)) {  
                        //System.out.println(pro.getName() + " = " + pro.getValue()); 
                    	//System.out.println(pro.getName() + " = " + pro.getAttribute("ID")); 
                    }  
                }
                System.out.println(feature.getDefaultGeometryProperty().getValue());
                System.out.println(feature.getAttribute("ID"));
            }
            itertor.close();  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch(IOException e) { e.printStackTrace(); }  
    }  
     
}  
