import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.net.URL;
public class CopyOfparase {
	public void sanle(IN_dot_ArchiveSpeed a22){
		try {
			//URL url = new URL("http://www.baidu.com");
            //InputStream in =url.openStream();
            //InputStreamReader isr = new InputStreamReader(in);
            //BufferedReader bufr = new BufferedReader(isr);
            File file = new File("F:\\metro_config.xml");
            SAXReader reader=new SAXReader();
            //读取xml文件到Document中
            Document doc=reader.read(file);
            //获取xml文件的根节点
            Element rootElement=doc.getRootElement();
            Element firstWorldElement = rootElement.element("corridor");
            //System.out.println("first World Attr: "
              //      + firstWorldElement.attribute(0).getName() + "="
                //    + firstWorldElement.attributeValue("corridor"));
            for (Iterator iter = rootElement.elementIterator(); iter.hasNext();)
            {
                Element e = (Element) iter.next();
                //System.out.println("second World Attr: "+
                //e.attribute(0).getName()+"="
               // 		+e.attributeValue("corridor"));
                //+e.attribute(0).getText()+";;"+e.attribute(1).getName()+"="+e.attribute(1).getText());
                for (Iterator iter2 = e.elementIterator(); iter2.hasNext();)
                {
                	Element e2 = (Element) iter2.next();
                	//打印道路名称
                	//System.out.println(e2.attribute(2).getName());
                	if("station_id".equals(e2.attribute(2).getName())){
                		//调用Dotsensor方法，将循环的值转存为对象
                		
                		Dotsensor dotsendor1 = new Dotsensor();
                		dotsendor1.setState("MN");
                		dotsendor1.setMarket("MIN");
                		dotsendor1.setHwyidx(0);
                		dotsendor1.setCrossidx(0);
                		dotsendor1.setLaneidx(0);
                		dotsendor1.setDotsensorid(e2.attribute(2).getText());
                		dotsendor1.setDistrict("");
                		dotsendor1.setTcisensorid(0);
                		dotsendor1.setHwy(e.attribute(0).getText());
                		dotsendor1.setHwydir(e.attribute(1).getText());
                		dotsendor1.setCross_from("");
                		dotsendor1.setData_source_slat(Float.parseFloat(e2.attribute(5).getText()));
                		dotsendor1.setData_source_slong(Float.parseFloat(e2.attribute(4).getText()));
                		dotsendor1.setCross_to("");
                		dotsendor1.setData_source_elat(0);
                		dotsendor1.setData_source_elong(0);
                		dotsendor1.setReader_id("MN_dotStation_RTSpeed");
                		a22.dotsensorList.add(dotsendor1);
                		//打印信息
                		//System.out.println("dotsensorid="+e2.attribute(2).getText()+";hwy="
                         //   +e.attribute(0).getText()+";hwydir="+e.attribute(1).getText()
                         //   +";lat="+e2.attribute(5).getText()+";lon="+e2.attribute(4).getText()
                          //  );
                	}
                	if("station_id".equals(e2.attribute(1).getName())){
                		Dotsensor dotsendor1 = new Dotsensor();
                		if("lon".equals(e2.attribute(2).getName())){
                			dotsendor1.setState("MN");
                    		dotsendor1.setMarket("MIN");
                    		dotsendor1.setHwyidx(0);
                    		dotsendor1.setCrossidx(0);
                    		dotsendor1.setLaneidx(0);
                    		dotsendor1.setDotsensorid(e2.attribute(1).getText());
                    		dotsendor1.setDistrict("");
                    		dotsendor1.setTcisensorid(0);
                    		dotsendor1.setHwy(e.attribute(0).getText());
                    		dotsendor1.setHwydir(e.attribute(1).getText());
                    		dotsendor1.setCross_from("");
                    		dotsendor1.setData_source_slat(Float.parseFloat(e2.attribute(3).getText()));
                    		dotsendor1.setData_source_slong(Float.parseFloat(e2.attribute(2).getText()));
                    		dotsendor1.setCross_to("");
                    		dotsendor1.setData_source_elat(0);
                    		dotsendor1.setData_source_elong(0);
                    		dotsendor1.setReader_id("MN_dotStation_RTSpeed");
                    		a22.dotsensorList.add(dotsendor1);
                		}else{
                		dotsendor1.setState("MN");
                		dotsendor1.setMarket("MIN");
                		dotsendor1.setHwyidx(0);
                		dotsendor1.setCrossidx(0);
                		dotsendor1.setLaneidx(0);
                		dotsendor1.setDotsensorid(e2.attribute(1).getText());
                		dotsendor1.setDistrict("");
                		dotsendor1.setTcisensorid(0);
                		dotsendor1.setHwy(e.attribute(0).getText());
                		dotsendor1.setHwydir(e.attribute(1).getText());
                		dotsendor1.setCross_from("");
                		dotsendor1.setData_source_slat(Float.parseFloat(e2.attribute(4).getText()));
                		dotsendor1.setData_source_slong(Float.parseFloat(e2.attribute(3).getText()));
                		dotsendor1.setCross_to("");
                		dotsendor1.setData_source_elat(0);
                		dotsendor1.setData_source_elong(0);
                		dotsendor1.setReader_id("MN_dotStation_RTSpeed");
                		a22.dotsensorList.add(dotsendor1);
                		//System.out.println("dotsensorid="+e2.attribute(1).getText()+";hwy="
                        //    +e.attribute(0).getText()+";hwydir="+e.attribute(1).getText()
                        //    +";lat="+e2.attribute(4).getText()+";lon="+e2.attribute(3).getText()
                         //   );
                		}  
                	}
                	if("station_id".equals(e2.attribute(3).getName())){
                		Dotsensor dotsendor1 = new Dotsensor();
                		dotsendor1.setState("MN");
                		dotsendor1.setMarket("MIN");
                		dotsendor1.setHwyidx(0);
                		dotsendor1.setCrossidx(0);
                		dotsendor1.setLaneidx(0);
                		dotsendor1.setDotsensorid(e2.attribute(3).getText());
                		dotsendor1.setDistrict("");
                		dotsendor1.setTcisensorid(0);
                		dotsendor1.setHwy(e.attribute(0).getText());
                		dotsendor1.setHwydir(e.attribute(1).getText());
                		dotsendor1.setCross_from("");
                		dotsendor1.setData_source_slat(Float.parseFloat(e2.attribute(6).getText()));
                		dotsendor1.setData_source_slong(Float.parseFloat(e2.attribute(5).getText()));
                		dotsendor1.setCross_to("");
                		dotsendor1.setData_source_elat(0);
                		dotsendor1.setData_source_elong(0);
                		dotsendor1.setReader_id("MN_dotStation_RTSpeed");
                		a22.dotsensorList.add(dotsendor1);
                		System.out.println("dotsensorid="+e2.attribute(3).getText()+";hwy="
                            +e.attribute(0).getText()+";hwydir="+e.attribute(1).getText()
                            +";lat="+e2.attribute(6).getText()+";lon="+e2.attribute(5).getText()
                            );
                           
                	}

                }

            }
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
		
	}
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        CopyOfparase a11 = new CopyOfparase();
        a11.sanle(null);
        
    }

}