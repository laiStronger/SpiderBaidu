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
 * �����й������ʴ�
 * ץȡhttp://www.qi-che.com/ask/ 
 * @author dzm
 *
 */
public class SpiderCarTest8 {

	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	//private static String homeUrl = "http://www.qi-che.com/ask/";
	//private static String url = "http://www.qi-che.com/ask/search.php";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://www.qi-che.com/ask/question.php?qid=152");
		//getSolveCategoryInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest8");
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
			URL u = new URL("http://www.qi-che.com/ask/search.php?act=all");
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
			if(result.indexOf("<div class=\"h3_menu\">") > 0 && result.indexOf("\">�ѽ��") > 0)
			{
				int index = result.indexOf("<div class=\"h3_menu\">");
				int end = result.indexOf("\">�ѽ��");
				homeInfo = result.substring(index,end);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				href = "http://www.qi-che.com/ask/"+href;		
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
			if(result.indexOf("<div class=\"pages\">") > 0 && result.indexOf("��һҳ") > 0)
			{
				int index = result.indexOf("<div class=\"pages\">")+"<div class=\"pages\">".length();
				int end = result.indexOf("��һҳ");
				String page = result.substring(index,end);
				page = page.substring(0,page.lastIndexOf("</a>"));
				page = page.substring(page.lastIndexOf("\">")+"\">".length(),page.length());
				page = page.trim();
				pages = Integer.parseInt(page);
				//System.out.println(pages);
				
			}
			//��ȡ�����еķ�ҳ��������url
			String urlInfo = "";
			if(result.indexOf("<div class=\"pages\">") > 0 && result.indexOf("��һҳ") > 0)
			{
				int index = result.indexOf("<div class=\"pages\">");
				int end = result.indexOf("��һҳ");
				String pageInfo = result.substring(index,end);
				
				urlInfo= pageInfo.substring(pageInfo.indexOf("<a href=\"")+"<a href=\"".length(),pageInfo.length());
				
				urlInfo = urlInfo.substring(0,urlInfo.indexOf("\">"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("="));
				//System.out.println(urlInfo);
				
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://www.qi-che.com/ask/"+urlInfo+"="+page;
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
			if(result.indexOf("<div class=\"mainlist_box\">") > 0 && result.indexOf("<div class=\"pages\">") > 0)
			{
				int index = result.indexOf("<div class=\"mainlist_box\">");
				int end = result.indexOf("<div class=\"pages\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("<a href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" title="));
						
						href = "http://www.qi-che.com/ask/"+href;
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
			if(result.indexOf("<div class=\"ur_here\">") > 0 && result.indexOf("<div style=\"display: none;\" class=\"messagesubmit\" id=\"jianyi_content\">") > 0)
			{
				int index = result.indexOf("<div class=\"ur_here\">");
				int end = result.indexOf("<div style=\"display: none;\" class=\"messagesubmit\" id=\"jianyi_content\">");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<b>") > 0 && askerInfo.indexOf("</b>") > 0)
			{
				int index = askerInfo.indexOf("<b>")+"<b>".length();
				int end = askerInfo.indexOf("</b>");
				title = askerInfo.substring(index,end);			
				
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("<div class=\"ask_content\">") > 0 && askerInfo.indexOf("<div class=\"ask_rela\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask_content\">");
				description = askerInfo.substring(index,askerInfo.length());
				description = description.substring(description.indexOf("<pre>"),description.indexOf("</pre>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				description = description.trim();
				System.out.println("���ⲹ�䣺"+description);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<div class=\"ask_added\">") > 0 && askerInfo.indexOf("<div class=\"ask_rela\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask_added\">");
				int end = askerInfo.indexOf("<div class=\"ask_rela\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<em>")+"<em>".length(),time.indexOf("</em>"));
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
			if(result.indexOf("id=\"answrBestDiv\"") > 0 && result.indexOf("id=\"answerListDiv\"") > 0)
			{
				int index = result.indexOf("id=\"answrBestDiv\"");
				int end = result.indexOf("id=\"answerListDiv\"");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("<pre>"),bestAsk.indexOf("</pre>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(result.indexOf("id=\"answrBestDiv\"") > 0 && result.indexOf("id=\"answerListDiv\"") > 0)
			{
				int index = result.indexOf("id=\"answrBestDiv\"");
				int end = result.indexOf("id=\"answerListDiv\"");
				asktime = result.substring(index,end);
				asktime = asktime.substring(asktime.indexOf("<small>"),asktime.indexOf("</small>"));
				asktime = asktime.substring(asktime.indexOf("<em>")+"<em>".length(),asktime.indexOf("</em>"));
				
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
			if(result.indexOf("id=\"answerListDiv\"") > 0 && result.indexOf("<div style=\"display: none;\" class=\"messagesubmit\" id=\"jianyi_content\">") > 0)
			{
				int index = result.indexOf("id=\"answerListDiv\"");
				int end = result.indexOf("<div style=\"display: none;\" class=\"messagesubmit\" id=\"jianyi_content\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<pre>");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					String singleAskTime = askInfo.substring(askInfo.indexOf("<small>"),askInfo.indexOf("</small>"));
					singleAskTime = singleAskTime.substring(singleAskTime.indexOf("<em>")+"<em>".length(),singleAskTime.indexOf("</em>"));
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</pre>"));
					
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
