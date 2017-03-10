package com.car;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
/***
 * ���ֶ��ֳ������������ʴ�
 * ץȡhttp://www.862sc.com/ask_list.html?status=&c1=&c2=&kw=
 * @author dzm
 *
 */
public class SpiderCarTest10 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://www.862sc.com/ask_list.html";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://www.862sc.com/ask/3258.html");
		//getSolveCategoryInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest10");
	}
	
	/***
	 * ��ȡ�ѽ���ķ���
	 * @param url
	 */
	public static void getSolveCategoryInfo(MongoTemplate mongo)
	{
		//initMongo();
		mongoTemplate = mongo;
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
			
			String result = content.toString();
			
			//��ҳ����������Ϣ
			String homeInfo = "";
			if(result.indexOf("<UL class=tabitem>") > 0 && result.indexOf("\">ȫ������") > 0)
			{
				int index = result.indexOf("<UL class=tabitem>");
				int end = result.indexOf("\">ȫ������");
				homeInfo = result.substring(index,end);
				String href = homeInfo.substring(homeInfo.lastIndexOf("href=\"")+"href=\"".length(),homeInfo.length());
				href = "http://www.862sc.com/ask_list.html"+href;		
				//System.out.println(href);
				//��ҳ��������
				getCategoryPageInfo(href);
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
		
	}
	/***
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getCategoryPageInfo(String url)
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
			
			String result = content.toString();
			
			//��ȡ��������ҳ��
			int pages = 0;
			if(result.indexOf("��һҳ") > 0 && result.indexOf("\">βҳ") > 0)
			{
				int index = result.indexOf("��һҳ");
				int end = result.indexOf("\">βҳ");
				String page = result.substring(index,end);
				page = page.substring(page.indexOf("href=\"")+"href=\"".length(),page.length());
				page = page.substring(page.lastIndexOf("=")+"=".length(),page.length());
				page = page.trim();
				pages = Integer.parseInt(page);
				//System.out.println(pages);
				
			}
			//��ȡ�����еķ�ҳ��������url
			String urlInfo = "";
			if(result.indexOf("��һҳ") > 0 && result.indexOf("\">βҳ") > 0)
			{
				int index = result.indexOf("��һҳ");
				int end = result.indexOf("\">βҳ");
				String pageInfo = result.substring(index,end);
				pageInfo = pageInfo.substring(pageInfo.indexOf("href=\"")+"href=\"".length(),pageInfo.length());
				urlInfo= pageInfo.substring(0,pageInfo.lastIndexOf("="));
			
				//System.out.println(urlInfo);
				
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://www.862sc.com/"+urlInfo+"="+page;
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
			InputStreamReader theHTML = new InputStreamReader(in, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//��ҳ����������Ϣ
			String cateInfo = "";
			if(result.indexOf("<DIV class=box_body>") > 0 && result.indexOf("<div class=\"pages\"") > 0)
			{
				int index = result.indexOf("<DIV class=box_body>");
				int end = result.indexOf("<div class=\"pages\"");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\""));
						
						href = "http://www.862sc.com/"+href;
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
		} catch (Exception e) {
			System.err.println(e);
		}
		String result = content.toString();
		try
		{
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class=\"m\">") > 0 && result.indexOf("<DIV class=other_answer>") > 0)
			{
				int index = result.indexOf("<div class=\"m\">");
				int end = result.indexOf("<DIV class=other_answer>")+"<DIV class=other_answer>".length();
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<h1 class=\"ask_title\">") > 0 && askerInfo.indexOf("</h1>") > 0)
			{
				int index = askerInfo.indexOf("<h1 class=\"ask_title\">")+"<h1 class=\"ask_title\">".length();
				int end = askerInfo.indexOf("</h1>");
				title = askerInfo.substring(index,end);			
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("<div id=\"question\" class=\"ask_content px14\">") > 0 && askerInfo.indexOf("</div>") > 0)
			{
				int index = askerInfo.indexOf("<div id=\"question\" class=\"ask_content px14\">");
				description = askerInfo.substring(index,askerInfo.length());
				description = description.substring(0,description.indexOf("</div>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				description = description.trim();
				System.out.println("���ⲹ�䣺"+description);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<span class=\"f_gray\">") > 0 && askerInfo.indexOf("</span>") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"f_gray\">")+"<span class=\"f_gray\">".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</span>"));
				time = time.substring(time.indexOf(" "),time.length());
				//ȥ��
				time = time.trim();
				System.out.println("����ʱ�䣺"+time);
			}
			CarQuestion cq = new CarQuestion();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
			if(askerInfo.indexOf("<div class=\"best_answer_show\">") > 0 && askerInfo.indexOf("</div>") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"best_answer_show\">")+"<div class=\"best_answer_show\">".length();
				bestAsk = askerInfo.substring(index,askerInfo.length());
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("</div>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(askerInfo.indexOf("<div class=\"best_answer_show\">") > 0 && askerInfo.indexOf("</span>") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"best_answer_show\">");
				asktime = askerInfo.substring(index,askerInfo.length());
				asktime = asktime.substring(asktime.indexOf("<span class=\"px11\">")+"<span class=\"px11\">".length(),asktime.length());
				asktime = asktime.substring(0,asktime.indexOf("</span>"));
				//ȥ��
				asktime = asktime.trim();
				System.out.println("��ѻش�"+asktime+"-----"+bestAsk);
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
			//�����ش�
			if(askerInfo.indexOf("<DIV class=pos>") > 0 && askerInfo.indexOf("<DIV class=other_answer>") > 0)
			{
				int index = askerInfo.indexOf("<DIV class=pos>");
				int end = askerInfo.indexOf("<DIV class=other_answer>");
				
				String otherAsk = askerInfo.substring(index,end);
				
				String[] asks = otherAsk.split("<div  class=\"ask_content px14\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span class=\"px11\">")+"<span class=\"px11\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("</span>"));
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</div>"));
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//ȥ��
					singleAsk = singleAsk.trim();
					
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
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
	}
}
