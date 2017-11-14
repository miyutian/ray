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



  
public class test {  
      
	public static void main(String[] args) {  
        ShapefileDataStore shpDataStore = null;  
        try{  
            shpDataStore = new ShapefileDataStore(new File("E:\\Industrial standard Docments\\TomTom\\c11\\chnc11___________cf.shp").toURI().toURL());  
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
                //System.out.println(feature.getName() + " = " + feature.getValue());
                Collection<Property> p = feature.getProperties();
                //System.out.println("p");
                Iterator<Property> it = p.iterator();  
                while(it.hasNext()) {  
                    Property pro = it.next();  
                    if (pro.getValue() instanceof Point) { 
                        //System.out.println("PointX = " + ((Point)(pro.getValue())).getX());  
                        //System.out.println("PointY = " + ((Point)(pro.getValue())).getY());  
                    } else {  
                    	if ("ID1".equals(pro.getName().toString())){
                    		//System.out.println(pro.getName() + " = " + pro.getValue());
                    	}
                    	
                    }  
                }  
            }  
            itertor.close();  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch(IOException e) { e.printStackTrace(); }  
    }  
     
}  
