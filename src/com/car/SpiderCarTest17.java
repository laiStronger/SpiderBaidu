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
 * �����������ʴ�����
 * ץȡhttp://ask.beimai.com/
 * @author dzm
 *	����վ����Ѵ𰸣���ѻش�ʱ��,���ⲹ��
 */
public class SpiderCarTest17 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://ask.beimai.com/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://ask.beimai.com/ask-453784");
		//getHomeHrefInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest17");
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//��ҳ����������Ϣ
			if(result.indexOf("target=\"_blank\">����</a>") > 0)
			{
				String[] hinfo = result.split("target=\"_blank\">����</a>");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == hinfo.length-1)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(hrefInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),hrefInfo.length());
						href = href.substring(0,href.indexOf("\" class="));
						href = "http://ask.beimai.com"+href;
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();

			//����ֻ��һҳ�������
			if(result.indexOf("<div id=\"gpxpager\" class=\"pages\">") == -1)
			{
				System.out.println(url);
				getCategoryHrefInfo(url);
			}
			else
			{
				//��ȡ��������ҳ��
				int pages = 0;
				if(result.indexOf("<span class=\"inline mg_r10\">��") > 0)
				{
					int index = result.indexOf("<span class=\"inline mg_r10\">��")+"<span class=\"inline mg_r10\">��".length();
					String pageInfo = result.substring(index,result.length());
					pageInfo = pageInfo.substring(0,pageInfo.indexOf("ҳ"));
					pageInfo = pageInfo.trim();
					pages = Integer.parseInt(pageInfo);
					//System.out.println(pages);
				}
				//��ȡ�����еķ�ҳ��������url
				String urlInfo = "";
				String urlbe = "";
				String urlend = "";
				if(result.indexOf("\" class=\"inline turnPage\">��һҳ") > 0)
				{
					int end = result.indexOf("\" class=\"inline turnPage\">��һҳ");
					String pageInfo = result.substring(0,end);
					urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),pageInfo.length());
					urlbe = urlInfo.substring(0,urlInfo.indexOf("-")+"-".length());
					urlend = urlInfo.substring(urlInfo.indexOf("-")+"-".length(),urlInfo.length());
					urlend = urlend.substring(urlend.indexOf("-"),urlend.length());
					//System.out.println(urlInfo);
				}
				for(int page = 1; page <= pages; page++)
				{
					String urls = "http://ask.beimai.com/"+urlbe+page+urlend;
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
			if(result.indexOf("<div style=\"min-height:500px;\">") > 0 && result.indexOf("<div class=\"f_r w250\">") > 0)
			{
				int index = result.indexOf("<div style=\"min-height:500px;\">");
				int end = result.indexOf("<div class=\"f_r w250\">");
				cateInfo = result.substring(index,end);
				if(cateInfo.indexOf("<div id=\"gpxpager\" class=\"pages\">") > 0)
				{
					cateInfo = cateInfo.substring(0,cateInfo.indexOf("<div id=\"gpxpager\" class=\"pages\">"));
				}
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
						if(hrefInfo.indexOf("\" class=\"colSp2\"") == -1)
						{
							continue;
						}
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" class=\"colSp2\""));
						if(href.startsWith("/"))
						{
							href = "http://ask.beimai.com"+href;
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
			if(result.indexOf("<div class=\"f_l w920\">") > 0 && result.indexOf("<div class=\"f_r w250\">") > 0)
			{
				int index = result.indexOf("<div class=\"f_l w920\">");
				int end = result.indexOf("<div class=\"f_r w250\">")+"<div class=\"f_r w250\">".length();
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<div class=\"lineH35 bg4 f14 pd_l10 bdT_e3 posR\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"lineH35 bg4 f14 pd_l10 bdT_e3 posR\">");
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</span>"));
				title = title.substring(title.lastIndexOf("\">")+"\">".length(),title.length());
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<span class=\"boxRT mg_r5\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"boxRT mg_r5\">")+"<span class=\"boxRT mg_r5\">".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</span>"));
				//ȥ��
				time = time.trim();
				time = time+ " 00:00:00";
				System.out.println("����ʱ�䣺"+time);
			}
			CarQuestion cq = new CarQuestion();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
			if(result.indexOf("<div class=\"lineH35 bg4 f14 pd_l10 bdT_e3 posR\">") > 0 && result.indexOf("<div class=\"f_r w250\">") > 0)
			{
				int index = result.indexOf("<div class=\"lineH35 bg4 f14 pd_l10 bdT_e3 posR\">");
				int end = result.indexOf("<div class=\"f_r w250\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<li class=\"imgbdXB lineH25 pd10\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("���£�")+"���£�".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("</li>"));
					//ȥ��
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</li>"));
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
