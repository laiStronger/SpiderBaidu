package com.car;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 抓取汽车问答网站
 * @author dzm
 *
 */
public class CarSpiderOther {

	private static Logger logger = LoggerFactory.getLogger(CarSpiderOther.class);
	
	final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	private static MongoTemplate mongoTemplate;
	//private static long startTime = System.currentTimeMillis();   //获取开始时间

	
	/** 总抓取入口
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		
		spiderTotalCategoryHtmlMain();
	}
	
	/**
	 * 使用线程抓取
	 */
	public static void spiderTotalCategoryHtmlMain() throws Exception{

		// 开始的倒数锁 
        final CountDownLatch begin = new CountDownLatch(1);  

        // 结束的倒数锁 
        final CountDownLatch end = new CountDownLatch(10);
        System.out.println("CarSpiderOther start");
      
        try 
        {  
            for(int i = 0; i < 10; i++)
            {
            	new Worker(i+8,begin,end).run(); 
            }
        } 
        catch (Exception e) 
        {  
        	logger.error("error spiderTotalCategoryHtmlMain "+ e);
        }
         
        //try块中begin.await一直处于阻塞状态，等待begin.countDown运行线程在锁存器倒计数1至零
        //begin.countDown();
         
        //等待try块中，end.countDown()，每运行一次，end线程锁存器减一，直至为0，运行end.await()
        //end.await(); 
		System.out.println("CarSpiderOther end");
	}
	
	//使用多线程
	static class Worker extends Thread{  
        int i;  
        CountDownLatch begin;  
        CountDownLatch end;  
        public Worker(int i ,CountDownLatch begin,CountDownLatch end){  
             this.i=i;  
             this.begin=begin;  
             this.end=end;  
        }   
        public void run(){ 
        	System.out.println(i);
        	try
        	{
        		switch (i) {
					case 8:
						 doWork();//工作了  
						 //原本begin锁存器线程数为1，等待begin.countDown()运行之后begin的线程数为0，这样begin.await()直接进入非阻塞状态
						 //begin.await();
						 SpiderCarTest8.getSolveCategoryInfo(mongoTemplate); 
						 System.out.println("Worker SpiderCarTest8.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						 //end总共10个线程数，每运行一次end.countDown()，计数器减一  
						 //end.countDown();
						break;
					case 9:
						doWork();//工作了  
						//begin.await();
						SpiderCarTest9.getCategoryPageInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest9.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 10:
						doWork();//工作了
						//begin.await();
						SpiderCarTest10.getSolveCategoryInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest10.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 11:
						doWork();//工作了  
						//begin.await();
						SpiderCarTest11.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest11.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 12:
						doWork();//工作了  
						//begin.await();
						SpiderCarTest12.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest12.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 13:
						doWork();//工作了  
						//begin.await();
						SpiderCarTest13.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest13.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 14:
						doWork();//工作了
						//begin.await();
						SpiderCarTest14.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest14.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 15:
						doWork();//工作了  
						//begin.await();
						SpiderCarTest15.getCategoryPageInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest15.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 16:
						doWork();//工作了  
						SpiderCarTest16.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest16.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					case 17:
						doWork();//工作了  
						//begin.await();
						SpiderCarTest17.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest17.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//工人完成工作，计数器减一  
						break;
					default:
						break;
					}
        	}
        	catch(Exception e)
        	{
        		logger.error(""+e);
        	}
    		System.out.println("Worker "+i+" do work end at "+sdf.format(new Date()));  
  
        }  
          
        private void doWork(){  
            try {  
                Thread.sleep(500);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
    } 
}
