package com;


import java.io.IOException;

import org.apache.http.ParseException;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;

/**
 * 使用Jsoup解析url
 * @tag：url ：http://sex.guokr.com/
 * Created by monster on 2015/12/11.
 */
public class CodeTest {
    public static void main(String[] args) throws ParseException, IOException{
    	String[] coor= new CodeTest().transform(756835.31,985114.69);
    	
    	
    }
    	/**
    	 * @param y
    	 * @param x
    	 * @return
    	 */
    	/**
    	 * @param y
    	 * @param x
    	 * @return
    	 */
    	private String[] transform(double y, double x) {
    		 /* for more details please refer to: 
    		    1) https://github.com/OSGeo/proj.4/wiki/GenParms#units 

    		    2) https://gis.stackexchange.com/questions/206050/convert-from-epsg27700-to-latitude-and-longitude 

    		    3) https://gis.stackexchange.com/questions/199087/projection-from-epsg4326-to-epsg4258 

    		    4) https://trac.osgeo.org/proj4j 

    		 */
    		String[] EPSGIDList = new String[]{
    				"EPSG:4481",
    				"EPSG:4482",
    				"EPSG:4483",
    				"EPSG:4484",
    				"EPSG:4486",
    				"EPSG:4488"
    		};
    		try {
    			for (int i = 0; i < 4200; i++){
	    			CRSFactory crsFactory = new CRSFactory();
	    			CoordinateReferenceSystem sourceSRS = crsFactory.createFromName(EPSGIDList[i]);
	    			CoordinateReferenceSystem targetSRS = crsFactory.createFromName("EPSG:4326");
	    			CoordinateTransform transformation = new BasicCoordinateTransform(sourceSRS, targetSRS);
	    			ProjCoordinate result = new ProjCoordinate();
	    			ProjCoordinate input = new ProjCoordinate(y, x);
	    			transformation.transform(input, result);
	    			ProjCoordinate yy = transformation.transform(input, result);
	    			
	    			//System.out.println("result       "+yy);
	    			if (result == null){
	    				continue;
	    			}

	                String[] coor = new String[2];
	                coor[0] = result.y + "";
	                coor[1] = result.x + "";
	                if(Math.ceil(result.x/1) == -72.0){
		                System.out.println(i+"===========>"+EPSGIDList[i]+"   "+"X::"+result.x+"   "+"Y::"+result.y);
	                }else{
	                	System.out.println(i+"===========>"+EPSGIDList[i]+"   "+"pass");
	                }
	                new WriteTxt().WriteTxt(coor[0],coor[1]);
    			}
                
    		} catch(Exception e) {
    			//LOGGER.debug("Transform failed: " + e.getMessage());
    		}			
    		return null;
    	

    }
}