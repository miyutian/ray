package com;

import java.util.ArrayList;
import java.util.Date;

public class run {
	ArrayList<String> ShpList = new ArrayList<String>();
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new run().run();
	}
	public void run() throws Exception{
		long startTime = System.currentTimeMillis();
		System.out.println("-----Start UnZip GZ Files-------at " + new Date());
		UnZip UZ = new UnZip();
		ShpList = new UnZip().UnZipgz();
		System.out.println("-----End UnZip GZ Files-------at " + new Date());
		System.out.println("-----Start Save RD file to DB-------at " + new Date());
		new RD2DB().tttmc(ShpList);
		System.out.println("-----End Save RD file to DB-------at " + new Date());
		System.out.println("-----Start Save NW file to DB-------at " + new Date());
		new NW_GC2DB().nwtodb(ShpList);
		System.out.println("-----End Save NW file to DB-------at " + new Date());
		System.out.println("-----Start Save GC file to DB-------at " + new Date());
		new NW_GC2DB().gctodb(ShpList);
		System.out.println("-----End Save GC file to DB-------at " + new Date());
		System.out.println("-----Start Save RN file to DB-------at " + new Date());
		new RN2DB().rn2db(ShpList);
		System.out.println("-----End Save RN file to DB-------at " + new Date());
		System.out.println("-----Start Save SI file to DB-------at " + new Date());
		new SI2DB().sitodb(ShpList);
		System.out.println("-----End Save SI file to DB-------at " + new Date());
		System.out.println("-----Start Save SP file to DB-------at " + new Date());
		new SI2DB().sptodb(ShpList);
		System.out.println("-----End Save SP file to DB-------at " + new Date());
		System.out.println("-----Start Save SR file to DB-------at " + new Date());
		new SR2DB().srtable(ShpList);
		System.out.println("-----End Save SR file to DB-------at " + new Date());
		System.out.println("-----Start Save MN file to DB-------at " + new Date());
		new MN_MP2DB().mntable(ShpList);
		System.out.println("-----End Save MN file to DB-------at " + new Date());
		System.out.println("-----Start Save MP file to DB-------at " + new Date());
		new MN_MP2DB().mptable(ShpList);
		System.out.println("-----End Save MP file to DB-------at " + new Date());
		long time = System.currentTimeMillis()- startTime;
		System.out.println("Used "+time/1000+" S");
		System.out.println("End the Program");
	}

}
