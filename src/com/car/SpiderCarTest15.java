package com.car;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/***
 * �����١����ش�
 * ץȡhttp://wenda.cheduoshao.com
 * @author dzm
 * ����վ�����ⲹ��,��ѻش�ʱ�䣬��ѻش�
 */
public class SpiderCarTest15 {

	private static String now = "";
	private static String beforeDate = "";
	private static Calendar calendar=Calendar.getInstance();
	private static String year = String.valueOf(calendar.get(Calendar.YEAR));
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	//private static String homeUrl = "http://wenda.cheduoshao.com/hwdList_page1/";
	private static String homeUrl = "http://wenda.cheduoshao.com";
	static {
		Date dBefore = new Date();
		//�õ�����
		Calendar calendar = Calendar.getInstance(); 
		//����Ϊǰһ��
		calendar.add(Calendar.DAY_OF_MONTH, -1);  
		//�õ�ǰһ���ʱ��
		dBefore = calendar.getTime();   
		//����ʱ���ʽ
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		//��ʽ��ǰһ��
		beforeDate = sdf.format(dBefore);    
		
		now = sdf.format(new Date());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://wenda.cheduoshao.com/weiboRead/781_1.html");
		//getCategoryPageInfo();
	}
	
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest15");
	}
	/***
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getCategoryPageInfo(MongoTemplate mongo)
	{
		//initMongo();
		mongoTemplate = mongo;
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL(homeUrl);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//��ȡ��������ҳ��
			int pages = 0;
			String urlInfo = "";
			if(result.indexOf("<div class=\"c_page clearfix\">") > 0 && result.indexOf("��һҳ") > 0)
			{
				int index = result.indexOf("<div class=\"c_page clearfix\">");
				int end = result.indexOf("��һҳ");
				String pageInfo = result.substring(index,end);
				
				String page = pageInfo.substring(0,pageInfo.lastIndexOf("</a>"));
				page = page.substring(page.lastIndexOf("'>")+"'>".length(),page.length());
				pages = Integer.parseInt(page);
				urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a class='c_next' href='")+"<a class='c_next' href='".length(),pageInfo.lastIndexOf("'>"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("page")+"page".length());
				//System.out.println(pages+" "+urlInfo);
			}
			
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://wenda.cheduoshao.com"+urlInfo+page+"/";
				//System.out.println(urls);
				getCategoryHrefInfo(urls);
			}
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
		
		
	}
	/***
	 * ��ȡ���������������href����
	 * @param url
	 */
	public static void getCategoryHrefInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL(url);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//��ҳ����������Ϣ
			String cateInfo = "";
			if(result.indexOf("<div class=\"ask_allquestion\">") > 0 && result.indexOf("<div class=\"c_page clearfix\">") > 0)
			{
				int index = result.indexOf("<div class=\"ask_allquestion\">");
				int end = result.indexOf("<div class=\"c_page clearfix\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("<p class=\"f_r\"><a href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\""));
						href = "http://wenda.cheduoshao.com" + href;
						//System.out.println(i+"  "+href);
						getCarData(href);
					}
					
				}
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
		
	}
	
	public static void getCarData(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL(url);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
		String result = content.toString();
		try
		{
			
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class=\"ask_allquestion\">") > 0 && result.indexOf("<div class=\"commentBox forwardBox\">") > 0)
			{
				int index = result.indexOf("<div class=\"ask_allquestion\">");
				int end = result.indexOf("<div class=\"commentBox forwardBox\">")+"<div class=\"commentBox forwardBox\">".length();
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<p class=\"wbText\">") > 0)
			{
				int index = askerInfo.indexOf("<p class=\"wbText\">")+"<p class=\"wbText\">".length();
				//int end = askerInfo.indexOf("<div class=\"user-box\">");
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</p>"));
				//ȥ��
				title = title.trim();
				title = title.replaceAll("</?[^>]+>", "");
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			//String description = "";
			
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<div class=\"weiboBottom clearfix\">") > 0 && askerInfo.indexOf("<div class=\"wb_CommentBox clearfix\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"weiboBottom clearfix\">");
				int end = askerInfo.indexOf("<div class=\"wb_CommentBox clearfix\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<p class=\"f_l\"><span>")+"<p class=\"f_l\"><span>".length(),time.indexOf("</span>"));
				//ȥ��
				time = time.trim();
				if(time.startsWith("��") && time.endsWith("��"))
				{
					time = time.substring(1,time.length()-1);
				}
				if(time.indexOf("����") > -1)
				{
					time = time.replaceAll("����", now);
				}
				if(time.indexOf("����") > -1)
				{
					time = time.replaceAll("����", beforeDate);
				}
				if(time.indexOf("��") > -1 && time.indexOf("��") > -1)
				{
					time = time.replaceAll("��", "-");
					time = time.replaceAll("��", "");
					time = year+"-"+time;
				}
				System.out.println("����ʱ�䣺"+time);
			}
			CarQuestion cq = new CarQuestion();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try
			{
				cq.setTitle(title);
				//cq.setContent(description);
				cq.setSource(url);
				if(!"".equals(time))
				{
					cq.setAskTime(sdf.parse(time));
				}
				cq.setCreateTime(new Date());
				cq.setKeyword("����");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"��վ����ץȡ����������error"+e);
			}
			
			//�����ش�
			List<String> listStr = new ArrayList<String>();
			if(result.indexOf("<div class=\"commentTab\">") > 0 && result.indexOf("<div class=\"blank10\">") > 0)
			{
				int index = result.indexOf("<div class=\"commentTab\">");
				int end = result.indexOf("<div class=\"blank10\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("</b>");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					
					//�����ش�ش������ʱ��
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span>")+"<span>".length(),askInfo.indexOf("</span>"));
					singleAskTime = singleAskTime.trim();
					if(singleAskTime.startsWith("��") && singleAskTime.endsWith("��"))
					{
						singleAskTime = singleAskTime.substring(1,singleAskTime.length()-1);
					}
					if(singleAskTime.indexOf("����") > -1)
					{
						singleAskTime = singleAskTime.replaceAll("����", now);
					}
					if(singleAskTime.indexOf("����") > -1)
					{
						singleAskTime = singleAskTime.replaceAll("����", beforeDate);
					}
					if(singleAskTime.indexOf("��") > -1 && singleAskTime.indexOf("��") > -1)
					{
						singleAskTime = singleAskTime.replaceAll("��", "-");
						singleAskTime = singleAskTime.replaceAll("��", "");
						singleAskTime = year+"-"+singleAskTime;
					}
					singleAskTime = singleAskTime.trim();
					
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("<span>"));
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//ȥ��
					singleAsk = singleAsk.trim();
					
					listStr.add(singleAsk);
					System.out.println("�����ش�"+singleAskTime+"-----"+singleAsk);
					
					try
					{
						CarAnswer ca = new CarAnswer();
						ca.setQuestionId(cq.getId());
						ca.setContent(singleAsk);
						ca.setSource(url);
						if(!"".equals(singleAskTime))
						{
							ca.setAskTime(sdf.parse(singleAskTime));
						}
						ca.setCreateTime(new Date());
						ca.setBestAnswer("0");
						mongoTemplate.insert(ca);
					}
					catch(Exception e)
					{
						logger.error(url+"��վ����ץȡ������������error"+e);
					}
				}
			}
			//������ж�ҳ�����
			if(askerInfo.indexOf("<div class=\"c_page clearfix\">") > -1 && askerInfo.indexOf("<a class='c_next'") > -1)
			{
				int index = result.indexOf("<div class=\"c_page clearfix\">");
				int end = result.indexOf("<div class=\"commentBox forwardBox\">");
				String pageInfo = result.substring(index,end);
				
				String page = pageInfo.substring(0,pageInfo.lastIndexOf("<a class='c_next'"));
				page = page.substring(0,page.lastIndexOf("</a>"));
				page = page.substring(page.lastIndexOf("'>")+"'>".length(),page.length());
				int pages = Integer.parseInt(page);
				String urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a class='c_next' href='")+"<a class='c_next' href='".length(),pageInfo.lastIndexOf("'>��һҳ"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf(".html"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("_"));
				
				//System.out.println(pages+" "+urlInfo);
				
				for(int i = 1; i <= pages; i++)
				{
					
					String urls = "http://wenda.cheduoshao.com"+urlInfo+"_"+i+".html";
					
					//System.out.println(urls);
					if(i > 1)
					{
						getAnswersHrefInfo(urls,cq.getId());
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
	}
	
	/***
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getAnswersHrefInfo(String url,String id)
	{
		List<String> listStr = new ArrayList<String>();
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL(url);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(result.indexOf("<div class=\"commentTab\">") > 0 && result.indexOf("<div class=\"blank10\">") > 0)
		{
			int index = result.indexOf("<div class=\"commentTab\">");
			int end = result.indexOf("<div class=\"blank10\">");
			
			String otherAsk = result.substring(index,end);
			
			String[] asks = otherAsk.split("</b>");
			for(int i = 0; i < asks.length; i++)
			{
				if(i == 0)
				{
					continue;
				}
				String askInfo = asks[i];
				
				
				//�����ش�ش������ʱ��
				String singleAskTime = askInfo.substring(askInfo.indexOf("<span>")+"<span>".length(),askInfo.indexOf("</span>"));
				singleAskTime = singleAskTime.trim();
				if(singleAskTime.startsWith("��") && singleAskTime.endsWith("��"))
				{
					singleAskTime = singleAskTime.substring(1,singleAskTime.length()-1);
				}
				if(singleAskTime.indexOf("����") > -1)
				{
					singleAskTime = singleAskTime.replaceAll("����", now);
				}
				if(singleAskTime.indexOf("����") > -1)
				{
					singleAskTime = singleAskTime.replaceAll("����", beforeDate);
				}
				if(singleAskTime.indexOf("��") > -1 && singleAskTime.indexOf("��") > -1)
				{
					singleAskTime = singleAskTime.replaceAll("��", "-");
					singleAskTime = singleAskTime.replaceAll("��", "");
					singleAskTime = year+"-"+singleAskTime;
				}
				singleAskTime = singleAskTime.trim();
				
				
				String singleAsk = askInfo.substring(0,askInfo.indexOf("<span>"));
				
				singleAsk = CarFormat.Stringformat(singleAsk);
				singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				singleAsk = singleAsk.trim();
				
				listStr.add(singleAsk);
				System.out.println("�����ش�"+singleAskTime+"-----"+singleAsk);
				
				try
				{
					CarAnswer ca = new CarAnswer();
					ca.setQuestionId(id);
					ca.setContent(singleAsk);
					ca.setSource(url);
					if(!"".equals(singleAskTime))
					{
						ca.setAskTime(sdf.parse(singleAskTime));
					}
					ca.setCreateTime(new Date());
					ca.setBestAnswer("0");
					mongoTemplate.insert(ca);
				}
				catch(Exception e)
				{
					logger.error(url+"��վ����ץȡ������������error"+e);
				}
			}
		}
		
	}
}
