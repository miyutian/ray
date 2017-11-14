#/bin/bash
java -cp bin:lib/* com.tci.shp.TomTom2Mysql CAN kph /ebsr0/data/jackie/output/CAN 127.0.0.1:3306 trafficcast naitou3 TomTom_Raw_16r2_can
java -cp bin:lib/* com.tci.shp.TomTom2Mysql MEX kph /ebsr0/data/jackie/output/MEX 127.0.0.1:3306 trafficcast naitou3 TomTom_Raw_16r2_mex
java -cp bin:lib/* com.tci.shp.TomTom2Mysql USA kph /ebsr0/data/jackie/output/USA 127.0.0.1:3306 trafficcast naitou3 TomTom_Raw_16r2_usa