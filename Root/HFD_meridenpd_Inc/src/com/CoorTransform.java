package com;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;

public class CoorTransform {
	private void CoorTransform(){
		
	}
   	public String[] transform(double y, double x) {
		 /* for more details please refer to: 
		    1) https://github.com/OSGeo/proj.4/wiki/GenParms#units 

		    2) https://gis.stackexchange.com/questions/206050/convert-from-epsg27700-to-latitude-and-longitude 

		    3) https://gis.stackexchange.com/questions/199087/projection-from-epsg4326-to-epsg4258 

		    4) https://trac.osgeo.org/proj4j 

		 */
		//String[] EPSGIDList = new String[]{
		//		"EPSG:2234",
		//		"EPSG:2879",
		//		"EPSG:3508"
		//};
		String[] coor = new String[2];
		try {
			
			for (int i = 0; i < 4200; i++){
				
   			CRSFactory crsFactory = new CRSFactory();
   			CoordinateReferenceSystem sourceSRS = crsFactory.createFromName("EPSG:2879");
   			
   			CoordinateReferenceSystem targetSRS = crsFactory.createFromName("EPSG:4326");
   			CoordinateTransform transformation = new BasicCoordinateTransform(sourceSRS, targetSRS);

   			ProjCoordinate result = new ProjCoordinate();
   			ProjCoordinate input = new ProjCoordinate(y, x);
   			transformation.transform(input, result);	    			
   			ProjCoordinate yy = transformation.transform(input, result);	    			
               coor[0] = result.y + "";
               coor[1] = result.x + "";
				}	
			} catch(Exception e) {
			//LOGGER.debug("Transform failed: " + e.getMessage());
		}		
		return coor;
   	}
}