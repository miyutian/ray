Map processing steps:
1. Unzip SHP files:
	nohup ./unzipshp.sh &
	
2. Remove ax folders:
	Delete files of ax folder like: ax.7z.001, nam2016_06-shpd-mn-usa-ax.7z.001
	
3. Reomve USA GU state folder(USA Only):
	Folder name like: nam201606shpdmnusaugu7z001
	
4. Generate street tables(links, alias, tmc, zone):
	nohup ./tomtom2mysql.sh &
			
5. (MEX only) Some links in water area and not in any state, county.
	a. Find them out: select * from TomTom_Raw_16r3_mex.Raw_TTLinks_MEX where country!='MEX' or country is null
		16r3 total 19 links: 14840000883642,14840001496932,14840006973885,14840000353438,14840002216982,14840002392668,14840002888250,14840001538073,14840001547938,14840001656861,14840001705064,14840001943936,14840001943937,14840001943938,14840001960528,14840001960529,14840002036058,14840002480257,14840002891646
	b. Assign nearest county, state to them:
		update Raw_TTLinks_MEX set state='TB',state_code='27',county='CÁRDENAS',county_cod='002' where link_id=14840000883642;
		update Raw_TTLinks_MEX set state='TB',state_code='27',county='CÁRDENAS',county_cod='002' where link_id=14840001496932;
		update Raw_TTLinks_MEX set state='BS',state_code='03',county='COMONDÚ',county_cod='001' where link_id=14840006973885;
		update Raw_TTLinks_MEX set state='QR',state_code='23',county='ISLA MUJERES',county_cod='003' where link_id=14840000353438;
		update Raw_TTLinks_MEX set state='SI',state_code='25',county='AHOME',county_cod='001' where link_id=14840002216982;
		update Raw_TTLinks_MEX set state='SI',state_code='25',county='AHOME',county_cod='001' where link_id=14840002392668;
		update Raw_TTLinks_MEX set state='SI',state_code='25',county='AHOME',county_cod='001' where link_id=14840002888250;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001538073;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001547938;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001656861;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001705064;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001943936;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001943937;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='GUAYMAS',county_cod='029' where link_id=14840001943938;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='PUERTO PEÑASCO',county_cod='048' where link_id=14840001960528;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='PUERTO PEÑASCO',county_cod='048' where link_id=14840001960529;
		update Raw_TTLinks_MEX set state='SO',state_code='26',county='PUERTO PEÑASCO',county_cod='048' where link_id=14840002036058;
		update Raw_TTLinks_MEX set state='TM',state_code='28',county='MATAMOROS',county_cod='022' where link_id=14840002480257;
		update Raw_TTLinks_MEX set state='TM',state_code='28',county='MATAMOROS',county_cod='022' where link_id=14840002891646;


6. Generate maneuver table:
	nohup ./maneuver.sh &

7. Remove duplicate records:
	-----------CAN----------------	
	1). Raw_TTLinks_CAN:
	    insert into FINAL_16R3_TomTom_Master_CAN_20161104.16R3_Raw_TTLinks_CAN select LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,LREF_ADDRE,LNREF_ADDR,RREF_ADDRE,RNREF_ADDR,F_NODE_ID,T_NODE_ID,FUNC_CLASS,SPEEDCAT,TO_LANES,FROM_LANES,LANE_CAT,LINKDIR,FRONTAGE,BRIDGE,TUNNEL,RAMP,TOLLWAY,FERRY_TYPE,HWYDIR,REVERSIBLE,EXPR_LANE,CARPOOLRD,CONTRACC,STATE,COUNTY,COUNTY_ABB,STATE_CODE,COUNTY_COD,LINK_GEOM,LANES,CITY,DISTANCE from TomTom_Raw_16r3_can.Raw_TTLinks_CAN group by link_id
	    
	2). Raw_TTAlias_CAN:
	    insert into FINAL_16R3_TomTom_Master_CAN_20161104.16R3_Raw_TTAlias_CAN select * from TomTom_Raw_16r3_can.Raw_TTAlias_CAN group by LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,DIR_ONSIGN
	    
	3). Raw_TTTmc_CAN:
		insert into FINAL_16R3_TomTom_Master_CAN_20161104.16R3_Raw_TTTMC_CAN select * from TomTom_Raw_16r3_can.Raw_TTTmc_CAN group by LINK_ID,TMC,TMCPATHID
		
	4). Raw_TTZone_CAN:
		insert into FINAL_16R3_TomTom_Additional_20161104.16r3_ttlinks_zone select * from TomTom_Raw_16r3_can.Raw_TTZone_CAN group by LINK_ID
		
	5). Raw_Maneuver_CAN:
		insert into FINAL_16R3_TomTom_Additional_20161104.16r3_ttmaneuver select * from TomTom_Raw_16r3_can.Raw_Maneuver_CAN group by ID,FEATTYP,BIFTYP,PROMANTYP,JNCTID,SEQNR,TRPELID,TRPELTYP
	
	-----------USA----------------
	1). Raw_TTLinks_USA:
	    insert into FINAL_16R3_TomTom_Master_USA_20161104.16R3_Raw_TTLinks_USA select LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,LREF_ADDRE,LNREF_ADDR,RREF_ADDRE,RNREF_ADDR,F_NODE_ID,T_NODE_ID,FUNC_CLASS,SPEEDCAT,TO_LANES,FROM_LANES,LANE_CAT,LINKDIR,FRONTAGE,BRIDGE,TUNNEL,RAMP,TOLLWAY,FERRY_TYPE,HWYDIR,REVERSIBLE,EXPR_LANE,CARPOOLRD,CONTRACC,STATE,COUNTY,COUNTY_ABB,STATE_CODE,COUNTY_COD,LINK_GEOM,LANES,CITY,DISTANCE from TomTom_Raw_16r3_usa.Raw_TTLinks_USA group by link_id
	
	2). Raw_TTAlias_USA:
	    insert into FINAL_16R3_TomTom_Master_USA_20161104.16R3_Raw_TTAlias_USA select * from TomTom_Raw_16r3_usa.Raw_TTAlias_USA group by LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,DIR_ONSIGN
	    	
	3). Raw_TTTmc_USA:
		insert into FINAL_16R3_TomTom_Master_USA_20161104.16R3_Raw_TTTMC_USA select * from TomTom_Raw_16r3_usa.Raw_TTTmc_USA group by LINK_ID,TMC,TMCPATHID
	
	4). Raw_TTZone_USA:
		insert into FINAL_16R3_TomTom_Additional_20161104.16r3_ttlinks_zone select * from TomTom_Raw_16r3_usa.Raw_TTZone_USA group by LINK_ID
		
	5). Raw_Maneuver_USA:
		insert into FINAL_16R3_TomTom_Additional_20161104.16r3_ttmaneuver select * from TomTom_Raw_16r3_usa.Raw_Maneuver_USA group by ID,FEATTYP,BIFTYP,PROMANTYP,JNCTID,SEQNR,TRPELID,TRPELTYP
		
	-----------MEX----------------
	1). Raw_TTLinks_MEX:
	    insert into FINAL_16R3_TomTom_Master_MEX_20161226.16R3_Raw_TTLinks_MEX select LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,LREF_ADDRE,LNREF_ADDR,RREF_ADDRE,RNREF_ADDR,F_NODE_ID,T_NODE_ID,FUNC_CLASS,SPEEDCAT,TO_LANES,FROM_LANES,LANE_CAT,LINKDIR,FRONTAGE,BRIDGE,TUNNEL,RAMP,TOLLWAY,FERRY_TYPE,HWYDIR,REVERSIBLE,EXPR_LANE,CARPOOLRD,CONTRACC,STATE,COUNTY,COUNTY_ABB,STATE_CODE,COUNTY_COD,LINK_GEOM,LANES,CITY,DISTANCE from TomTom_Raw_16r3_mex.Raw_TTLinks_MEX group by link_id
	    
	2). Raw_TTAlias_MEX:
	    insert into FINAL_16R3_TomTom_Master_MEX_20161226.16R3_Raw_TTAlias_MEX select * from TomTom_Raw_16r3_mex.Raw_TTAlias_MEX group by LINK_ID,FNAME_PREF,NAMEPREFIX,FNAME_BASE,FNAME_SUFF,FNAME_TYPE,DIR_ONSIGN
	    
	3). Raw_TTTmc_MEX:
		insert into FINAL_16R3_TomTom_Master_MEX_20161226.16R3_Raw_TTTMC_MEX select * from TomTom_Raw_16r3_mex.Raw_TTTmc_MEX group by LINK_ID,TMC,TMCPATHID
		
	4). Raw_TTZone_MEX:
		insert into FINAL_16R3_TomTom_Additional_20161104.16r3_ttlinks_zone select * from TomTom_Raw_16r3_mex.Raw_TTZone_MEX group by LINK_ID

	5). Raw_Maneuver_MEX:
		insert into FINAL_16R3_TomTom_Additional_20161104.16r3_ttmaneuver select * from TomTom_Raw_16r3_mex.Raw_Maneuver_MEX group by ID,FEATTYP,BIFTYP,PROMANTYP,JNCTID,SEQNR,TRPELID,TRPELTYP
		
	
----Others-------------------------------------------------
1. Process Exit and Route Number:
    CAN:
	insert into FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_ExitNum_CAN select distinct a.link_id,a.fname_base from TomTom_Raw_16r2_can.Raw_ExitNum_CAN a inner join incident_db.16r2_ttlinks_can b on a.link_id=b.link_id and b.ramp='Y'
	delete a from FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_ExitNum_CAN a inner join 16r2_tt_can.16r2_TTLinks_CAN_original b on a.link_id=b.link_id and ifnull(b.fname_pref,'')='' and ifnull(b.nameprefix,'')='' and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')='' and ifnull(b.fname_suff,'')=''
	delete a from FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_ExitNum_CAN a inner join 16r2_tt_can.16r2_alias_can b on a.link_id=b.link_id and ifnull(b.fname_pref,'')='' and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')='' and ifnull(b.fname_suff,'')=''
	
	insert into FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_RouteNum_CAN select distinct link_id,fname_pref,nameprefix,fname_base,fname_suff,fname_type from TomTom_Raw_16r2_can.Raw_RouteNum_CAN
	delete a from FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_RouteNum_CAN a inner join 16r2_tt_can.16r2_TTLinks_CAN_original b on a.link_id=b.link_id and ifnull(b.fname_pref,'')=ifnull(a.fname_pref,'') and ifnull(b.nameprefix,'')=ifnull(a.nameprefix,'') and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')=ifnull(a.fname_type,'') and ifnull(b.fname_suff,'')=ifnull(a.fname_suff,'')
	delete a from FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_RouteNum_CAN a inner join 16r2_tt_can.16r2_alias_can b on a.link_id=b.link_id and ifnull(b.fname_pref,'')=ifnull(a.fname_pref,'') and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')=ifnull(a.fname_type,'') and ifnull(b.fname_suff,'')=ifnull(a.fname_suff,'')
	
	delete a from FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_ExitNum_CAN a inner join FINAL_16R2_TomTom_Master_CAN_20161104.16r2_Raw_RouteNum_CAN b on a.link_id=b.link_id and ifnull(b.fname_pref,'')='' and ifnull(b.nameprefix,'')='' and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')='' and ifnull(b.fname_suff,'')=''
	
	
    MEX:
	insert into FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_ExitNum_MEX select distinct a.link_id,a.fname_base from TomTom_Raw_16r2_mex.Raw_ExitNum_MEX a inner join incident_db.16r2_ttlinks_mex b on a.link_id=b.link_id and b.ramp='Y'
	delete a from FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_ExitNum_MEX a inner join 16r2_tt_mex.16r2_TTLinks_MEX_original b on a.link_id=b.link_id and ifnull(b.fname_pref,'')='' and ifnull(b.nameprefix,'')='' and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')='' and ifnull(b.fname_suff,'')=''
	delete a from FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_ExitNum_MEX a inner join 16r2_tt_mex.16r2_alias_mex b on a.link_id=b.link_id and ifnull(b.fname_pref,'')='' and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')='' and ifnull(b.fname_suff,'')=''
	
	insert into FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_RouteNum_MEX select distinct link_id,fname_pref,nameprefix,fname_base,fname_suff,fname_type from TomTom_Raw_16r2_mex.Raw_RouteNum_MEX
	delete a from FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_RouteNum_MEX a inner join 16r2_tt_mex.16r2_TTLinks_MEX_original b on a.link_id=b.link_id and ifnull(b.fname_pref,'')=ifnull(a.fname_pref,'') and ifnull(b.nameprefix,'')=ifnull(a.nameprefix,'') and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')=ifnull(a.fname_type,'') and ifnull(b.fname_suff,'')=ifnull(a.fname_suff,'')
	delete a from FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_RouteNum_MEX a inner join 16r2_tt_mex.16r2_alias_mex b on a.link_id=b.link_id and ifnull(b.fname_pref,'')=ifnull(a.fname_pref,'') and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')=ifnull(a.fname_type,'') and ifnull(b.fname_suff,'')=ifnull(a.fname_suff,'')
	
	delete a from FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_ExitNum_MEX a inner join FINAL_16R2_TomTom_Master_MEX_20161226.16r2_Raw_RouteNum_MEX b on a.link_id=b.link_id and ifnull(b.fname_pref,'')='' and ifnull(b.nameprefix,'')='' and ifnull(b.fname_base,'')=ifnull(a.fname_base,'') and ifnull(b.fname_type,'')='' and ifnull(b.fname_suff,'')=''
	
	