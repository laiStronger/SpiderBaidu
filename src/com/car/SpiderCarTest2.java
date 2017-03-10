package com.car;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/***
 * �����������������ʴ�
 * ץȡhttp://qa.auto.sina.com.cn
 * @author dzm
 *
 */
public class SpiderCarTest2 {
	
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest2.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://qa.auto.sina.com.cn/question/answered";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCategoryPageInfo();
		
		//getCarData("http://qa.auto.sina.com.cn/question/info/2263831/");
	}

	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
	}
	
	
	/***
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getCategoryPageInfo(MongoTemplate template)
	{
		//initMongo();
		mongoTemplate = template;
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL(homeUrl);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//��ȡ��������ҳ��
		int pages = 0;
		if(result.indexOf("<div class=\"L\"><span>") > 0 && result.indexOf("</span>������</div>") > 0)
		{
			int index = result.indexOf("<div class=\"L\"><span>")+"<div class=\"L\"><span>".length();
			int end = result.indexOf("</span>������</div>");
			String page = result.substring(index,end);
			
			pages = Integer.parseInt(page.trim());
			if(pages%10 > 0)
			{
				pages = pages/10+1;
			}
			else
			{
				pages = pages/10;
			}
			System.out.println(pages);
		}
		//��ȡ�����еķ�ҳ��������url
		String urlInfo = "";
		if(result.indexOf("��һҳ") > 0 && result.indexOf("��һҳ") > 0)
		{
			int index = result.indexOf("��һҳ");
			int end = result.indexOf("��һҳ");
			String pageInfo = result.substring(index,end);
			String[] hinfo = pageInfo.split("<li><a href=\"");
			if(hinfo.length > 0)
			{
				String hrefInfo = hinfo[1];
				String[] hrefs = hrefInfo.split("/\">");
				urlInfo = hrefs[0];
				urlInfo= urlInfo.substring(0,urlInfo.lastIndexOf("/"));
				//System.out.println(urlInfo);
			}
		}
		for(int page = 1; page <= pages; page++)
		{
			String urls = "http://qa.auto.sina.com.cn"+urlInfo+"/"+page+"/";
			//System.out.println(urls);
			getCategoryHrefInfo(urls);
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
			InputStreamReader theHTML = new InputStreamReader(in, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//��ҳ����������Ϣ
		String cateInfo = "";
		if(result.indexOf("<ul class=\"c dd\">") > 0 && result.indexOf("<div class=\"page\">") > 0)
		{
			int index = result.indexOf("<ul class=\"c dd\">");
			int end = result.indexOf("<div class=\"page\">");
			cateInfo = result.substring(index,end);
			
			String[] hinfo = cateInfo.split("<!--</div>-->");
			if(hinfo.length > 0)
			{
				for(int i = 0; i < hinfo.length; i++)
				{
					if(i == hinfo.length-1)
					{
						continue;
					}
					String hrefInfo = hinfo[i];
					String href = hrefInfo.substring(hrefInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),hrefInfo.lastIndexOf("\" title=\""));
					
					if(href.startsWith("/"))
					{
						href = "http://qa.auto.sina.com.cn"+href;
					}
				
					
					getCarData(href);
				}
				
			}
		} 
	}
	
	public static void getCarData(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL(url);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		try
		{
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class=\"bread_nav\">") > -1 && result.indexOf("<div class=\"ll\">") > 0)
			{
				int index = result.indexOf("<div class=\"bread_nav\">")-"<div class=\"bread_nav\">".length();
				int end = result.indexOf("<div class=\"ll\">");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<div class=\"ff\">") > -1 && askerInfo.indexOf("<div class=\"bread_nav\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"bread_nav\">");
				int end = askerInfo.indexOf("<div class=\"ff\">");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<i>")+"<i>".length(),title.length());
				title = title.substring(0,title.indexOf("</i>"));
				//ȥ��
				title = title.trim();
				
				title = CarFormat.Stringformat(title);
				title = title.replaceAll("</?[^>]+>", "");
				
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("<div class=\"ff\">") > -1 && askerInfo.indexOf("<div class=\"gg\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ff\">");
				int end = askerInfo.indexOf("<div class=\"gg\">");
				description = askerInfo.substring(index,end);
				
				//ȥ��
				description = description.trim();
				if(description.indexOf("<p>") > -1 && description.indexOf("</p>") > -1)
				{
					description = description.substring(description.indexOf("<p>"),description.lastIndexOf("</p>"));
				}
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				System.out.println("���ⲹ�䣺"+description);
			}
			
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<p>������<i style=\"color:#ED5900;\">") > 0 && askerInfo.indexOf("����") > 0)
			{
				int index = askerInfo.indexOf("<p>������<i style=\"color:#ED5900;\">");
				int end = askerInfo.indexOf("����");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<span>")+"<span>".length(),time.length());
				//ȥ��
				time = time.trim();
				System.out.println("����ʱ�䣺"+time);
			}
			CarQuestion cq = new CarQuestion();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try
			{
				cq.setTitle(title);
				cq.setContent(description);
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
			//��Ѵ�
			String bestAsk = "";
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(askerInfo.indexOf("��Ѵ�") > 0 && askerInfo.indexOf("<dl class=\"n\"></dl>") > 0)
			{
				int index = askerInfo.indexOf("��Ѵ�");
				int end = askerInfo.indexOf("<dl class=\"n\"></dl>");			
				
				bestAsk = askerInfo.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("��Ѵ�"),bestAsk.indexOf("<div class=\"L jj_1\">"));
				bestAsk = bestAsk.substring(bestAsk.indexOf("<p>"),bestAsk.indexOf("</p>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();

				asktime = askerInfo.substring(index,end+"<dl class=\"n\"></dl>".length());
				asktime = asktime.substring(asktime.indexOf("<div style=\"width:84px\"><span>")+"<div style=\"width:84px\"><span>".length(),asktime.indexOf("<dl class=\"n\"></dl>"));
				asktime = asktime.substring(0,asktime.indexOf("</span>"));
				asktime = asktime.trim();
				System.out.println("��ѻش�:"+asktime+" "+bestAsk);
				try
				{
					CarAnswer ca = new CarAnswer();
					ca.setQuestionId(cq.getId());
					ca.setContent(bestAsk);
					ca.setSource(url);
					if(!"".equals(asktime))
					{
						ca.setAskTime(sdf.parse(asktime));
					}
					ca.setCreateTime(new Date());
					ca.setBestAnswer("1");
					mongoTemplate.insert(ca);
				}
				catch(Exception e)
				{
					logger.error(url+"��վ����ץȡ��������Ѵ�error"+e);
				}
			}
			
			Map<String, String> hasMap = new HashMap<String,String>();
			//�����ش�
			if(result.indexOf("<div style=\"border-top:1px solid #e0e0e0;\" class=\"bl2 mt\">") > 0 && result.indexOf("<div class=\"ll\">") > 0)
			{
				int index = result.indexOf("<div style=\"border-top:1px solid #e0e0e0;\" class=\"bl2 mt\">");
				int end = result.indexOf("<div class=\"ll\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<div class=\"jj_2_2\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("<div style=\"width:84px;\"><span>")+"<div style=\"width:84px;\"><span>".length(),askInfo.indexOf("</span></div>"));
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<div class=\"jj_2_2\">")+"<div class=\"jj_2_2\">".length(),askInfo.indexOf("<div class=\"L jj_1\">"));
					singleAsk = singleAsk.substring(singleAsk.indexOf("<p>")+"<p>".length(),singleAsk.indexOf("</p>"));
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//ȥ��
					singleAsk = singleAsk.trim();
					if(hasMap.get(singleAsk) == null)
					{
						hasMap.put(singleAsk, singleAskTime);
						
						System.out.println("�����ش�"+hasMap.size()+"---"+singleAskTime+"-----"+singleAsk);
						
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
			}
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
		
	}
}
