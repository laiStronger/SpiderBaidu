package com.car;

import java.io.BufferedInputStream;
import java.io.IOException;
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
 * �����������������ʴ�
 * ץȡhttp://k.pcauto.com.cn/
 * @author dzm
 *
 */
public class SpiderCarTest4 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest2.class);
	private static MongoTemplate mongoTemplate;
	//private static String homeUrl = "http://k.pcauto.com.cn";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getHomeHrefInfo();
		//getCarData("http://k.pcauto.com.cn/question/3828751.html");
	}

	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
	}
	
	/***
	 * ��ȡ��ҳ���з����href����
	 */
	public static void getHomeHrefInfo(MongoTemplate template)
	{
		//initMongo();
		mongoTemplate = template;
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL("http://k.pcauto.com.cn");
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
			if(result.indexOf("<div class=\"tbB\">") > 0 && result.indexOf("<div class=\"boxwtzs\">") > 0)
			{
				int index = result.indexOf("<div class=\"tbB\">");
				int end = result.indexOf("<div class=\"boxwtzs\">");
				homeInfo = result.substring(index,end);
				
				String[] hinfo = homeInfo.split("<dt><a href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\">"));
						href = "http://k.pcauto.com.cn"+href;
						//System.out.println(href);
						getSolveCategoryInfo(href);
						
					}
					
				}
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url");
		}
		
	}
	/***
	 * ��ȡ�ѽ���ķ���
	 * @param url
	 */
	public static void getSolveCategoryInfo(String url)
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
			String homeInfo = "";
			if(result.indexOf("<span class=\"subMark pagination\">") > 0 && result.indexOf("\">�ѽ��") > 0)
			{
				int index = result.indexOf("<span class=\"subMark pagination\">");
				int end = result.indexOf("\">�ѽ��");
				homeInfo = result.substring(index,end);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
						
				//System.out.println(href);
				//��ҳ��������
				getCategoryPageInfo(href);
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url"+url);
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
			if(result.indexOf("<strong class=\"orange\">") > 0 && result.indexOf("</strong> ������</p>") > 0)
			{
				int index = result.indexOf("<strong class=\"orange\">")+"<strong class=\"orange\">".length();
				int end = result.indexOf("</strong> ������</p>");
				String page = result.substring(index,end);
				page = page.trim();
				pages = Integer.parseInt(page);
				if(pages%25 > 0)
				{
					pages = pages/25+1;
				}
				else
				{
					pages = pages/25;
				}
				//System.out.println(pages);
				
			}
			//��ȡ�����еķ�ҳ��������url
			String urlInfo = "";
			if(result.indexOf("<div class=\"pcauto_page\">") > 0 && result.indexOf("<p class=\"pNum\">") > 0)
			{
				int index = result.indexOf("<div class=\"pcauto_page\">");
				int end = result.indexOf("<p class=\"pNum\">");
				String pageInfo = result.substring(index,end);
				
				urlInfo= pageInfo.substring(pageInfo.indexOf("href='")+"href='".length(),pageInfo.indexOf(".html'>"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("/"));
				System.out.println(urlInfo);
				
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://k.pcauto.com.cn"+urlInfo+"/p"+page+".html";
				//System.out.println(urls);
				getCategoryHrefInfo(urls);
			}
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url"+url);
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
			if(result.indexOf("����ʱ��") > 0 && result.indexOf("<div class=\"pcauto_page\">") > 0)
			{
				int index = result.indexOf("����ʱ��");
				int end = result.indexOf("<div class=\"pcauto_page\">");
				cateInfo = result.substring(index,end);
				String[] hinfo = {};
				if(cateInfo.contains("<i class=\"iTitle\"><a id=ytest href=\""))
				{
					hinfo = cateInfo.split("<i class=\"iTitle\"><a id=ytest href=\"");
				}
				else if(cateInfo.contains("<i class=\"iTitle\"><a  href=\""))
				{
					hinfo = cateInfo.split("<i class=\"iTitle\"><a  href=\"");
				}
				
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\">"));
						
						
						//System.out.println(i+"  "+href);
						
						getCarData(href);
					}
					
				}
				else
				{
					System.out.println(hinfo.length);
				}
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url"+url);
		}
		
	}
	
	public static void getCarData(String url)
	{
		System.out.println("��������:"+url);
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
			if(result.indexOf("<div class=\"modOut modPblm\" id=\"question_content\">") > 0 && result.indexOf("<div class=\"layB\">") > 0)
			{
				int index = result.indexOf("<div class=\"modOut modPblm\" id=\"question_content\">");
				int end = result.indexOf("<div class=\"layB\">");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<i class=\"icon_js\"></i>") > 0 && askerInfo.indexOf("<span class=\"sxs orange\">����") > 0)
			{
				int index = askerInfo.indexOf("<i class=\"icon_js\"></i>")+"<i class=\"icon_js\"></i>".length();
				int end = askerInfo.indexOf("<span class=\"sxs orange\">����");
				title = askerInfo.substring(index,end);			
				title = title.substring(0,title.indexOf("</h1>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			//String description = "";
			
			//�ɼ���������������������������ظ������������������ɡ�
			/*if(title.equals(description))
			{
				
			}*/
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("����ʱ�䣺") > 0 && askerInfo.indexOf("<span class=\"sData\">") > 0)
			{
				int index = askerInfo.indexOf("����ʱ�䣺")+"����ʱ�䣺".length();
				int end = askerInfo.indexOf("<span class=\"sData\">");
				time = askerInfo.substring(index,end);
				time = time.substring(0,time.indexOf("</span>"));
			
				//ȥ��
				time = time.trim();
				System.out.println("����ʱ�䣺"+time);
			}
			CarQuestion cq = new CarQuestion();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
			if(result.indexOf("<strong>��Ѵ𰸣�</strong>") > 0 && result.indexOf("<div class=\"exInfo\">") > 0)
			{
				int index = result.indexOf("<strong>��Ѵ𰸣�</strong>");
				int end = result.indexOf("<div class=\"exInfo\">");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("<div class=\"dInfo gray\">"));
				bestAsk = bestAsk.substring(bestAsk.lastIndexOf("<p>"),bestAsk.lastIndexOf("</p>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(result.indexOf("<strong>��Ѵ𰸣�</strong>") > 0 && result.indexOf("<div class=\"exInfo\">") > 0)
			{
				int index = result.indexOf("<strong>��Ѵ𰸣�</strong>");
				int end = result.indexOf("<div class=\"exInfo\">");
				asktime = result.substring(index,end);
				asktime = asktime.substring(0,asktime.indexOf("<div class=\"dInfo gray\">"));
				asktime = asktime.substring(asktime.lastIndexOf("<span class=\"sTime\">")+"<span class=\"sTime\">".length(),asktime.indexOf("</span>"));
				
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
			if(result.indexOf("<strong>�����ش�</strong>") > 0 && result.indexOf("<div class=\"layB\">") > 0)
			{
				int index = result.indexOf("<strong>�����ش�</strong>");
				int end = result.indexOf("<div class=\"layB\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<i class=\"blue\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("</i>")+"</i>".length(),askInfo.indexOf("</div>"));
					singleAskTime = singleAskTime.trim();
					
					
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<p>")+"<p>".length(),askInfo.indexOf("</p>"));
					singleAskTime = singleAskTime.trim();
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
