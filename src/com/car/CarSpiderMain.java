package com.car;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 抓取汽车问答网站，按照排序抓取
 * @author dzm
 *
 */
public class CarSpiderMain {

	final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	private static MongoTemplate mongoTemplate;
	
	/** 总抓取入口
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		Test();
	}
	
	public static void Test() throws Exception
	{
        System.out.println("CarSpiderMain start");
        for(int i = 1; i <= 7 ; i++)
        {
        	switch (i) {
	     		case 1:
	     			System.out.println("Worker SpiderCarTest1.getHomeHrefInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest1.getHomeHrefInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest1.getHomeHrefInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		case 2:
	     			System.out.println("Worker SpiderCarTest2.getCategoryPageInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest2.getCategoryPageInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest2.getCategoryPageInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		case 3:
	     			System.out.println("Worker SpiderCarTest3.getHomeHrefInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest3.getHomeHrefInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest3.getHomeHrefInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		case 4:
	     			System.out.println("Worker SpiderCarTest4.getHomeHrefInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest4.getHomeHrefInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest4.getHomeHrefInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		case 5:
	     			System.out.println("Worker SpiderCarTest5.getHomeHrefInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest5.getHomeHrefInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest5.getHomeHrefInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		case 6:
	     			System.out.println("Worker SpiderCarTest6.getSolveCategoryInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest6.getSolveCategoryInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest6.getSolveCategoryInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		case 7:
	     			System.out.println("Worker SpiderCarTest7.getHomeHrefInfo() start"+" do work complete at "+sdf.format(new Date()));  
	     			SpiderCarTest7.getHomeHrefInfo(mongoTemplate);
	     			System.out.println("Worker SpiderCarTest7.getHomeHrefInfo() end"+" do work complete at "+sdf.format(new Date()));  
	     			break;
	     		default:
	     			break;
     		}
        }
        
        System.out.println("CarSpiderMain end");
	}
	
}
