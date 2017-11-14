#/bin/bash
java -cp bin:lib/* com.tci.shp.TomTom2Mysql CAN origFRC /ebsr0/data/jackie/output/CAN 127.0.0.1:3306 trafficcast naitou3 TomTom_Raw_16r3_can
java -cp bin:lib/* com.tci.shp.TomTom2Mysql MEX origFRC /ebsr0/data/jackie/output/MEX 127.0.0.1:3306 trafficcast naitou3 TomTom_Raw_16r3_mex
java -cp bin:lib/* com.tci.shp.TomTom2Mysql USA origFRC /ebsr0/data/jackie/output/USA 127.0.0.1:3306 trafficcast naitou3 TomTom_Raw_16r3_usa
