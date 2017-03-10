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
 * ������������������������ҳ
 * ץȡhttp://qcar.ieche.com/
 * @author dzm
 *	����վ����Ѵ𰸣���ѻش�ʱ��
 */
public class SpiderCarTest16 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://qcar.ieche.com/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://qcar.ieche.com/article.asp?id=382808");
		//getHomeHrefInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest16");
	}
	/***
	 * ��ȡ��ҳ���з����href����
	 */
	public static void getHomeHrefInfo(MongoTemplate mongo)
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
			if(result.indexOf("<div class=\"main-lside\">") > 0 && result.indexOf("<div class=\"main-rside\">") > 0)
			{
				int index = result.indexOf("<div class=\"main-lside\">");
				int end = result.indexOf("<div class=\"main-rside\">");
				homeInfo = result.substring(index,end);
				
				String[] hinfo = homeInfo.split("<li><a href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=_blank"));
						href = "http://qcar.ieche.com/"+href;
						//System.out.println(i+"  "+href);
						//��ҳ��������
						getCategoryPageInfo(href);
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

			//����ֻ��һҳ�������
			if(result.indexOf("</span>/1</b>") > -1)
			{
				//System.out.println(url);
				getCategoryHrefInfo(url);
			}
			else
			{
				//��ȡ��������ҳ��
				int pages = 0;
				if(result.indexOf("<div class=\"page_nav\">") > 0 && result.indexOf(" title=\"βҳ\">") > 0)
				{
					int index = result.indexOf("<div class=\"page_nav\">");
					int end = result.indexOf(" title=\"βҳ\">");
					String pageInfo = result.substring(index,end);
					if(!pageInfo.endsWith("<font color=#999999"))
					{
						pageInfo = pageInfo.substring(pageInfo.lastIndexOf("=")+"=".length(),pageInfo.length());
						pages = Integer.parseInt(pageInfo);
						//System.out.println(pages);
					}
				}
				//��ȡ�����еķ�ҳ��������url
				String urlInfo = "";
				if(result.indexOf("<div class=\"page_nav\">") > 0 && result.indexOf(" title=\"βҳ\">") > 0)
				{
					int index = result.indexOf("<div class=\"page_nav\">");
					int end = result.indexOf(" title=\"βҳ\">");
					String pageInfo = result.substring(index,end);
					urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=")+"<a href=".length(),pageInfo.length());
					urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("=")+"=".length());
					//System.out.println(urlInfo);
				}
				String local = url.substring(0,url.indexOf("?"));
				for(int page = 1; page <= pages; page++)
				{
					String urls = local+urlInfo+page;
					//System.out.println(urls);
					getCategoryHrefInfo(urls);
				}
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
			if(result.indexOf("<div id=\"content\">") > 0 && result.indexOf("<div class=\"page_nav\">") > 0)
			{
				int index = result.indexOf("<div id=\"content\">");
				int end = result.indexOf("<div class=\"page_nav\">");
				cateInfo = result.substring(index,end);
				String[] hinfo = {};
				//���ɷ���
				if(cateInfo.indexOf("<li class=\"ppTitle\"><a href=\"") > -1)
				{
					hinfo = cateInfo.split("<li class=\"ppTitle\"><a href=\"");
				}
				else if(cateInfo.indexOf("<h3><a href=\"") > -1)
				{
					hinfo = cateInfo.split("<h3><a href=\"");
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
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\">"));
						if(href.startsWith("/"))
						{
							href = "http://qcar.ieche.com"+href;
						}
						else
						{
							href = "http://qcar.ieche.com/"+href;
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
		System.out.println("url���ӣ�"+url);
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
			if(result.indexOf("<div id=\"content\">") > 0 && result.indexOf("<div class=\"commentin\">") > 0)
			{
				int index = result.indexOf("<div id=\"content\">");
				int end = result.indexOf("<div class=\"commentin\">");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<h1 id=\"title\">") > 0)
			{
				int index = askerInfo.indexOf("<h1 id=\"title\">")+"<h1 id=\"title\">".length();
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</h1>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("<div class=\"review_con\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"review_con\">");
				description = askerInfo.substring(index,askerInfo.length());
				description = description.substring(description.indexOf("<p>"),description.indexOf("</p>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				description = description.trim();
				System.out.println("���ⲹ�䣺"+description);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<li>ʱ�䣺<em>") > 0)
			{
				int index = askerInfo.indexOf("<li>ʱ�䣺<em>")+"<li>ʱ�䣺<em>".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</em></li>"));
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
			
			//�����ش�
			if(result.indexOf("<ul id=\"postContentList\">") > 0 && result.indexOf("�ظ�����") > 0)
			{
				int index = result.indexOf("<ul id=\"postContentList\">");
				int end = result.indexOf("�ظ�����");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<dd>");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("<small>�ش��ߣ�"),askInfo.length());
					singleAskTime = singleAskTime.substring(singleAskTime.indexOf("| ")+"| ".length(),singleAskTime.indexOf("</small>"));
					//ȥ��
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</dd>"));
					
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
