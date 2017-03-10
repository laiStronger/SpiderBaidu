package com.car;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
/***
 * ��������
 * ץȡhttp://wenwen.sogou.com/cate/?cid=87228416
 * @author dzm
 * ����վ��������µķ��࣬�������Σ�����ֱ�ӵ�����ֱ࣬�����ѽ����
 */
public class SpiderCarTest6 {
	
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://wenwen.sogou.com/cate/?cid=87228416";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://wenwen.sogou.com/z/q624056402.htm?ch=wtk.title");
		//getSolveCategoryInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
	}
	/***
	 * ��ȡ�ѽ���ķ���
	 * @param url
	 */
	public static void getSolveCategoryInfo(MongoTemplate template)
	{
		//initMongo();
		mongoTemplate = template;
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
			
			//��ҳ����������Ϣ
			String homeInfo = "";
			if(result.indexOf("�ѽ��</a></li>") > 0)
			{
				int index = result.indexOf("\">�ѽ��</a></li>");
				homeInfo = result.substring(0,index);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				href = "http://wenwen.sogou.com"+href;		
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//��ȡ��������ҳ��
			int pages = 50;
			/*if(result.indexOf("<input type=\"text\" class=\"page-item-jump-text\"") > 0 && result.indexOf("<a id=\"jumpbtn\"") > 0)
			{
				int index = result.indexOf("<input type=\"text\" class=\"page-item-jump-text\"");
				int end = result.indexOf("<a id=\"jumpbtn\"");
				String pageInfo = result.substring(index,end);
				if(pageInfo.indexOf("/��") > 0)
				{
					String[] pageInfos = pageInfo.split("/��");
					String pageinfo = pageInfos[1];
					String[] ps = pageinfo.split("ҳ");
					String page = ps[0];
					pages = Integer.parseInt(page);
					System.out.println(pages);
				}
			}*/
			//��ȡ�����еķ�ҳ��������url
			String urlInfo = "";
			if(result.indexOf("<div class=\"pagination\">") > 0 && result.indexOf("\" class=\"page_turn\">��һҳ") > 0)
			{
				int index = result.indexOf("<div class=\"pagination\">");
				int end = result.indexOf("\" class=\"page_turn\">��һҳ");
				String pageInfo = result.substring(index,end);
				urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),pageInfo.length());
					
				urlInfo= urlInfo.substring(0,urlInfo.lastIndexOf("=")+"=".length());
				
			}
			for(int page = 0; page < pages; page++)
			{
				String urls = "http://wenwen.sogou.com"+urlInfo+page;
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
			if(result.indexOf("<div class=\"lib-list\">") > 0 && result.indexOf("<div class=\"pagination\">") > 0)
			{
				int index = result.indexOf("<div class=\"lib-list\">");
				int end = result.indexOf("<div class=\"pagination\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("style=\"display:none;\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == hinfo.length-1)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						
						String href = hrefInfo.substring(hrefInfo.lastIndexOf("href=\"")+"href=\"".length(),hrefInfo.length());
						href = href.substring(0,href.indexOf("\">"));
						if(href.startsWith("/"))
						{
							href = "http://wenwen.sogou.com"+href;
						}
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
		System.out.println("URL���ӣ�"+url);
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
			if(result.indexOf("<div class=\"question-main\">") > 0 && result.indexOf("<div id=\"otherQuestions_list\"") > 0)
			{
				int index = result.indexOf("<div class=\"question-main\">");
				int end = result.indexOf("<div id=\"otherQuestions_list\"")+"<div id=\"otherQuestions_list\"".length();
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<h3 id=\"questionTitle\">") > 0)
			{
				int index = askerInfo.indexOf("<h3 id=\"questionTitle\">")+"<h3 id=\"questionTitle\">".length();
				
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</h3>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<div class=\"question-info\">") > 0 && result.indexOf("<div class=\"question-tit\"") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"question-info\">")+"<div class=\"question-info\">".length();
				int end = askerInfo.indexOf("<div class=\"question-tit\"");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<span class=\"time\">")+"<span class=\"time\">".length(),time.length());
				time = time.substring(0,time.indexOf("</span>"));
				//ȥ��
				time = time.trim();
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
			//��Ѵ�
			String bestAsk = "";
			if(result.indexOf("<div class=\"ico-pending\">") > 0 && result.indexOf("<div class=\"answer-wrap\">") > 0)
			{
				int index = result.indexOf("<div class=\"ico-pending\">");
				int end = result.indexOf("<div class=\"answer-wrap\">");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("<div class=\"answer-con\">")+"<div class=\"answer-con\">".length(),bestAsk.length());
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("</div>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(result.indexOf("<div class=\"ico-pending\">") > 0 && result.indexOf("<div class=\"answer-wrap\">") > 0)
			{
				int index = result.indexOf("<div class=\"ico-pending\">");
				int end = result.indexOf("<div class=\"answer-wrap\">");
				asktime = result.substring(index,end);
				asktime = asktime.substring(asktime.indexOf("<span class=\"time\">")+"<span class=\"time\">".length(),asktime.length());
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
			List<String> listStr = new ArrayList<String>();
			if(askerInfo.indexOf("<div class=\"answer-wrap\">") > 0 && askerInfo.indexOf("<div id=\"otherQuestions_list\"") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"answer-wrap\">");
				int end = askerInfo.indexOf("<div id=\"otherQuestions_list\"");
				
				String otherAsk = askerInfo.substring(index,end);
				
				String[] asks = otherAsk.split("<span class=\"time\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					//�����ش�ش������ʱ��
					String singleAskTime = askInfo.substring(0,askInfo.indexOf("</span>"));;
					//ȥ��
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<div class=\"answer-con\">")+"<div class=\"answer-con\">".length(),askInfo.length());
					singleAsk = singleAsk.substring(0,singleAsk.indexOf("</div>"));
					if(singleAsk.indexOf("<a target=\"_blank\"") > -1)
					{
						singleAsk = singleAsk.substring(0,singleAsk.indexOf("<a target=\"_blank\""));
					}
					
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
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
		
	}
}
