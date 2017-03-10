package com.car;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * ץȡ�����ʴ���վ
 * @author dzm
 *
 */
public class CarSpiderOther {

	private static Logger logger = LoggerFactory.getLogger(CarSpiderOther.class);
	
	final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	private static MongoTemplate mongoTemplate;
	//private static long startTime = System.currentTimeMillis();   //��ȡ��ʼʱ��

	
	/** ��ץȡ���
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
	 * ʹ���߳�ץȡ
	 */
	public static void spiderTotalCategoryHtmlMain() throws Exception{

		// ��ʼ�ĵ����� 
        final CountDownLatch begin = new CountDownLatch(1);  

        // �����ĵ����� 
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
         
        //try����begin.awaitһֱ��������״̬���ȴ�begin.countDown�����߳���������������1����
        //begin.countDown();
         
        //�ȴ�try���У�end.countDown()��ÿ����һ�Σ�end�߳���������һ��ֱ��Ϊ0������end.await()
        //end.await(); 
		System.out.println("CarSpiderOther end");
	}
	
	//ʹ�ö��߳�
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
						 doWork();//������  
						 //ԭ��begin�������߳���Ϊ1���ȴ�begin.countDown()����֮��begin���߳���Ϊ0������begin.await()ֱ�ӽ��������״̬
						 //begin.await();
						 SpiderCarTest8.getSolveCategoryInfo(mongoTemplate); 
						 System.out.println("Worker SpiderCarTest8.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						 //end�ܹ�10���߳�����ÿ����һ��end.countDown()����������һ  
						 //end.countDown();
						break;
					case 9:
						doWork();//������  
						//begin.await();
						SpiderCarTest9.getCategoryPageInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest9.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 10:
						doWork();//������
						//begin.await();
						SpiderCarTest10.getSolveCategoryInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest10.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 11:
						doWork();//������  
						//begin.await();
						SpiderCarTest11.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest11.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 12:
						doWork();//������  
						//begin.await();
						SpiderCarTest12.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest12.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 13:
						doWork();//������  
						//begin.await();
						SpiderCarTest13.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest13.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 14:
						doWork();//������
						//begin.await();
						SpiderCarTest14.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest14.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 15:
						doWork();//������  
						//begin.await();
						SpiderCarTest15.getCategoryPageInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest15.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 16:
						doWork();//������  
						SpiderCarTest16.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest16.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
						break;
					case 17:
						doWork();//������  
						//begin.await();
						SpiderCarTest17.getHomeHrefInfo(mongoTemplate);
						System.out.println("Worker SpiderCarTest17.getSolveCategoryInfo()"+" do work complete at "+sdf.format(new Date()));  
						//end.countDown();//������ɹ�������������һ  
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
