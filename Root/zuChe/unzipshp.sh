#/bin/bash
java -cp bin:lib/* com.tci.shp.UnzipShp /ebsr1/Transfer/TomTom_Map/North_America_2016_06/CAN /ebsr0/data/jackie/output/CAN
java -cp bin:lib/* com.tci.shp.UnzipShp /ebsr1/Transfer/TomTom_Map/North_America_2016_06/MEX /ebsr0/data/jackie/output/MEX
java -cp bin:lib/* com.tci.shp.UnzipShp /ebsr1/Transfer/TomTom_Map/North_America_2016_06/USA /ebsr0/data/jackie/output/USA

