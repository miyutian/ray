package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import com.mysql.jdbc.Driver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.JsonParase.UserBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.collections.functors.ForClosure;
public class JsonParase {
	String city = "HFD";
	String state = "CT";
	int Type = 1;
	
	public ArrayList<String[]> InfoParase(String InfoString) throws Exception{
		JsonParser parse =new JsonParser();  //创建json解析器
		Pattern pattern = Pattern.compile("[0-9]*");
        ArrayList<String[]> InfoList = new ArrayList<String[]>();
        try {
            JsonObject json=(JsonObject) parse.parse(InfoString);  //创建jsonObject对象
            System.out.println("recordsCount:"+json.get("records").getAsInt());  //将json数据转为为int型的数据
             
            //JsonObject result=json.get("rows").getAsJsonObject();
            //JsonObject rows=result.get("id").getAsJsonObject();
            //System.out.println("temperature:"+rows.get("id").getAsString());
            
            JsonObject jsonObject = new JsonParser().parse(InfoString).getAsJsonObject();
            //JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = jsonObject.getAsJsonArray("rows");

            Gson gson = new Gson();
            ArrayList<UserBean> userBeanList = new ArrayList<UserBean>();
            //加强for循环遍历JsonArray
            for (JsonElement user : jsonArray) {
                //使用GSON，直接转成Bean对象
                UserBean userBean = gson.fromJson(user, UserBean.class);
                userBeanList.add(userBean);
                String main_st = userBean.marker_details_xml;
                
                main_st = main_st.substring(main_st.lastIndexOf("value=")+7,main_st.indexOf("></details>")-2);
                //System.out.println("main_st:::"+main_st);
                if(main_st.indexOf(" ")!=-1){
                    Matcher isNum = pattern.matcher(main_st.substring(0,main_st.indexOf(" ")));
                
                String InfoListString[] = new String[7];
                if (main_st.indexOf("/")!=-1){
                	if(main_st.indexOf(",")!=-1){
	                	InfoListString = new String[]{
	                    		userBean.id,userBean.starttime,userBean.closetime,userBean.nature,
	                    		main_st.substring(0,main_st.lastIndexOf("/")),main_st.substring(main_st.lastIndexOf("/")+1,main_st.indexOf(",")),main_st
	                	};
                	}else{
                		InfoListString = new String[]{
	                    		userBean.id,userBean.starttime,userBean.closetime,userBean.nature,
	                    		main_st.substring(0,main_st.lastIndexOf("/")),main_st.substring(main_st.lastIndexOf("/")+1),main_st
                		};
	                	};
                }else if (isNum.matches()){
                	InfoListString = new String[]{
                    		userBean.id,userBean.starttime,userBean.closetime,userBean.nature,
                    		main_st.substring(main_st.indexOf(" ")+1,main_st.indexOf(",")),"BLOCK "+main_st.substring(0,main_st.indexOf(" ")),main_st
                    };
                }else if(main_st.indexOf(",")!=-1){
                	InfoListString = new String[]{
                    		userBean.id,userBean.starttime,userBean.closetime,userBean.nature,
                    		main_st.substring(0,main_st.indexOf(",")),null,main_st
                    };
                	System.err.println("Find some recored have no cross from!!!");
                }
                else{
                	InfoListString = new String[]{
                    		userBean.id,userBean.starttime,userBean.closetime,userBean.nature,
                    		main_st,null,main_st
                    };
                }
                InfoList.add(InfoListString);
                }
            }    
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }return InfoList;
	
	 }
	public ArrayList<String[]> LatLonParase (String LatLonString){
		
		JSONObject Markers = JSONObject.fromObject(LatLonString);  
        //System.out.println("------1----->"+Markers.toString());  
        JSONArray latArray = Markers.getJSONArray("Markers");  
        Iterator<JSONArray> lat = latArray.iterator(); 
        ArrayList<String[]> LatLonList = new ArrayList<String[]>();

        while (lat.hasNext()) {  
        	String LatLonListString[] = new String[3];
            JSONObject temp = JSONObject.fromObject(lat.next());  

            //System.out.println("------2----->"+temp.toString());  
            JSONArray value = temp.getJSONArray("Details");  
            Iterator<JSONArray> ianswers = value.iterator(); 
            int i= 1;
            while (ianswers.hasNext()) {  
                JSONObject tanswers = JSONObject.fromObject(ianswers.next());  
                //System.out.println("------3----->"); 
                if (i == 2){
                    LatLonListString[0] = temp.getString("lat"); 
                    LatLonListString[1] = temp.getString("lng");
                    LatLonListString[2] = tanswers.getString("value");
                    //System.out.println(tanswers.getString("value"));
                }i++; 
            }LatLonList.add(LatLonListString);
        }  return LatLonList;
	}
	public ArrayList<String[]> JoinJson(ArrayList<String[]>InfoList,ArrayList<String[]>LatLonList){
		ArrayList<String[]> HFDInfo = new ArrayList<String[]>();
		for(int InfoListi = 0;InfoListi < InfoList.size();InfoListi++){
			String[] HFDInfoStrings = new String[6];
			for (int LatLonListi = 0;LatLonListi<LatLonList.size();LatLonListi++){
				if (String.valueOf(LatLonList.get(LatLonListi)[2]).equals(String.valueOf(InfoList.get(InfoListi)[6]))){
					HFDInfoStrings = new String[]{
							InfoList.get(InfoListi)[0],InfoList.get(InfoListi)[1],InfoList.get(InfoListi)[2],InfoList.get(InfoListi)[3],
							InfoList.get(InfoListi)[4],InfoList.get(InfoListi)[5],LatLonList.get(LatLonListi)[0],LatLonList.get(LatLonListi)[1]
					};
					HFDInfo.add(HFDInfoStrings);
					break;
				}
			}
		}return HFDInfo;
	}
	public void InsertDB(ArrayList<String[]> HFDInfo) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Connection dbConn = new ConnectDB().connect();
        String insertSQL = "insert into HFD_meridenpd_Inc_Info(ID,Start_time,end_time,description,main_st,cross_from,Slat,Slon) values(?,?,?,?,?,?,?,?)";
        PreparedStatement pstm = dbConn.prepareStatement(insertSQL);
        int batchCount = 0;
		for (int i = 0;i < HFDInfo.size();i++){
			pstm.setInt(1, Integer.valueOf(HFDInfo.get(i)[0]));
			pstm.setString(2, (String)(HFDInfo.get(i)[1]));
			pstm.setString(3, (String)(HFDInfo.get(i)[2]));
			pstm.setString(4, (String)(HFDInfo.get(i)[3]));
			pstm.setString(5, (String)(HFDInfo.get(i)[4]));
			pstm.setString(6, (String)(HFDInfo.get(i)[5]));
			pstm.setString(7, (String)(HFDInfo.get(i)[6]));
			pstm.setString(8, (String)(HFDInfo.get(i)[7]));
			System.out.println(insertSQL);
    			pstm.addBatch();
    			batchCount++;
    			if (batchCount == 4000) {
    			    pstm.executeBatch();
    			    pstm.clearBatch();
    			    batchCount = 0;
        	} pstm.executeBatch();
        	System.out.println("save DB sucessfully!");
		}
	}
	public class UserBean {
	    //变量名跟JSON数据的字段名需要一致
	    private String starttime ;
	    private String id;
	    private String nature;
	    private String marker_details_xml;
	    private String closetime;
	    private String value;
	    private String lat;
	    private String lng;
	    
	}
}
