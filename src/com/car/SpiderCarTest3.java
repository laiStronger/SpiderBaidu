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
 * ץȡhttp://ask.cheshi.com/
 * @author dzm
 *
 */
public class SpiderCarTest3 {
	
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	//private static String homeUrl = "http://ask.cheshi.com";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		getCarData("http://ask.cheshi.com/detail_628770.html");
		//getHomeHrefInfo();
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
			URL u = new URL("http://ask.cheshi.com");
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
			if(result.indexOf("<div class=\"ak_fillter m\">") > 0 && result.indexOf("<div class=\"n_y l\">") > 0)
			{
				int index = result.indexOf("<div class=\"ak_fillter m\">");
				int end = result.indexOf("<div class=\"n_y l\">");
				homeInfo = result.substring(index,end);
				Map<String,String> hasMap = new HashMap<String,String>();
				String[] hinfo = homeInfo.split("href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						hrefInfo = hrefInfo.substring(0,hrefInfo.indexOf("\">"));
						
						if(hasMap.get(hrefInfo) == null)
						{
							hasMap.put(hrefInfo, hrefInfo);
							
							//System.out.println(hasMap.size()+" "+hrefInfo);
							//��ҳ��������
							getCategoryPageInfo(hrefInfo);
						}
						
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
			int pages = 0;
			if(result.indexOf("<div class=\"pagebox\">") > 0 && result.indexOf("<div class=\"n_rightsidebar l\">") > 0)
			{
				int index = result.indexOf("<div class=\"pagebox\">");
				int end = result.indexOf("<div class=\"n_rightsidebar l\">");
				String pageInfo = result.substring(index,end);
				pageInfo = pageInfo.substring(pageInfo.indexOf("..."),pageInfo.indexOf("��һҳ"));
				String page = pageInfo.substring(pageInfo.indexOf("target='_self'>")+"target='_self'>".length(),pageInfo.indexOf("</a>"));
				pages = Integer.parseInt(page);
				System.out.println(pages);
			}
			//��ȡ�����еķ�ҳ��������url
			String urlInfo = "";
			if(result.indexOf("<div class=\"pagebox\">") > 0 && result.indexOf("<div class=\"n_rightsidebar l\">") > 0)
			{
				int index = result.indexOf("<div class=\"pagebox\">");
				int end = result.indexOf("<div class=\"n_rightsidebar l\">");
				String pageInfo = result.substring(index,end);
				pageInfo = pageInfo.substring(pageInfo.indexOf("<a  href='")+"<a  href='".length(),pageInfo.indexOf("' target='_self'"));
				urlInfo = pageInfo.trim();
				urlInfo= urlInfo.substring(0,urlInfo.lastIndexOf("_"));
				//System.out.println(urlInfo);
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = urlInfo+"_"+page+"/";
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
			if(result.indexOf("<div class=\"ak_list\">") > 0 && result.indexOf("<div class=\"pagebox\">") > 0)
			{
				int index = result.indexOf("<div class=\"ak_list\">");
				int end = result.indexOf("<div class=\"pagebox\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("��ϸ");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						if(hrefInfo.indexOf("<a href=\"") > 0 && hrefInfo.lastIndexOf("\" target=\"_blank\">") > 0 &&  hrefInfo.lastIndexOf(".html") > 0 )
						{
							hrefInfo = hrefInfo.substring(hrefInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),hrefInfo.lastIndexOf("\" target=\"_blank\">"));
							String href =hrefInfo.trim();
							if(href.startsWith("/"))
							{
								href = "http://ask.cheshi.com"+href;
							}
							//System.out.println(i+"  "+href);
							
							getCarData(href);
						}
						
					}
					
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
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		try
		{
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class=\"n_breadcrumb\">") > 0 && result.indexOf("<div class=\"line pencil l\">") > 0)
			{
				int index = result.indexOf("<div class=\"n_breadcrumb\">");
				int end = result.indexOf("<div class=\"line pencil l\">");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("����") > 0 && askerInfo.indexOf("����") > 0)
			{
				int index = askerInfo.indexOf("����")+"����".length();
				int end = askerInfo.indexOf("����");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<h1"),title.indexOf("</h1>"));
				
				title = CarFormat.Stringformat(title);
				title = title.replaceAll("</?[^>]+>", "");
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("���ⲹ�䣺") > 0 && askerInfo.indexOf("<p class=\"shale l\">���ࣺ") > 0)
			{
				int index = askerInfo.indexOf("���ⲹ�䣺")+"���ⲹ�䣺".length();
				int end = askerInfo.indexOf("<p class=\"shale l\">���ࣺ");
				description = askerInfo.substring(index,end);
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				description = description.trim();
				System.out.println("���ⲹ�䣺"+description);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<div class=\"details l\">") > 0  && askerInfo.indexOf("<a href=\"#post\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"details l\">");
				int end = askerInfo.indexOf("<a href=\"#post\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<span>")+"<span>".length(),time.indexOf("</span>"));
				//ȥ��
				time = time.trim();
				time = time.substring(time.indexOf(" "),time.length());
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
			if(result.indexOf("<strong>��Ѵ�</strong>") > 0 && result.indexOf("<div class=\"fore\">") > 0)
			{
				int index = result.indexOf("<strong>��Ѵ�</strong>")+"<strong>��Ѵ�</strong>".length();
				int end = result.indexOf("<div class=\"fore\">");
				bestAsk = result.substring(index,end);
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(result.indexOf("<div class=\"fore\">") > 0 && result.indexOf("<div class=\"line pencil l\">") > 0)
			{
				int index = result.indexOf("<div class=\"fore\">");
				int end = result.indexOf("<div class=\"line pencil l\">");
				asktime = result.substring(index,end);			
				asktime = asktime.substring(asktime.indexOf("<span>")+"<span>".length(),asktime.indexOf("</span>"));
				//ȥ��
				asktime = asktime.trim();
				asktime = asktime.substring(asktime.indexOf(" "),asktime.length());
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
			if(result.indexOf("<div class=\"reply l\">") > 0 && result.indexOf("<div class=\"line pencil l\">") > 0)
			{
				int index = result.indexOf("<div class=\"reply l\">");
				int end = result.indexOf("<div class=\"line pencil l\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<div class=\"reply_no l\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					//�����ش�ش������ʱ��
					String singleAskTime = askInfo.substring(askInfo.indexOf("<div class=\"details l\">")+"<div class=\"details l\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(singleAskTime.indexOf("<span>")+"<span>".length(),singleAskTime.indexOf("</span>"));
					//ȥ��
					singleAskTime = singleAskTime.trim();
					singleAskTime = singleAskTime.substring(singleAskTime.indexOf(" "),singleAskTime.length());
					singleAskTime = singleAskTime.trim();
					
					String singleAsk =askInfo.substring(askInfo.indexOf("<p class=\"fall_nav\">")+"<p class=\"fall_nav\">".length(),askInfo.indexOf("<div class=\"details l\">"));
					
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
