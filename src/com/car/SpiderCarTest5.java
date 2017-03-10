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
 * �׳��������ʴ�
 * ץȡhttp://ask.bitauto.com/
 * @author dzm
 * û�и㶨
 */
public class SpiderCarTest5 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest5.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://ask.bitauto.com/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://ask.bitauto.com/detail/5097633/");
		//getCarData("http://ask.bitauto.com/detail/5102763/");
		//getHomeHrefInfo(mongoTemplate);
		
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
			if(result.indexOf("<ul class=\"ask-class-box\">") > 0 && result.indexOf("<div class=\"gzwm-box side-top-m\">") > 0)
			{
				int index = result.indexOf("<ul class=\"ask-class-box\">");
				int end = result.indexOf("<div class=\"gzwm-box side-top-m\">");
				homeInfo = result.substring(index,end);
				
				String[] hinfo = homeInfo.split("<p class=\"p-con\">");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						
						hrefInfo = hrefInfo.substring(0,hrefInfo.indexOf("</p>"));
						String[] hrefs = hrefInfo.split("href=\"");
						for(String href : hrefs)
						{
							if(href.startsWith("/browse"))
							{
								href = href.substring(0,href.indexOf("\" target=\"_blank\">"));
								href = "http://ask.bitauto.com"+href;
								//System.out.println("  "+href);
								
								//��ҳ��������
								getSolveCategoryInfo(href);
							}
						}
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
			if(result.indexOf("<div class=\"box-sty-new left-box-sty question-list\">") > 0 && result.indexOf("\">�ѽ��</a></li>") > 0)
			{
				int index = result.indexOf("<div class=\"box-sty-new left-box-sty question-list\">");
				int end = result.indexOf("\">�ѽ��</a></li>");
				homeInfo = result.substring(index,end);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				href = "http://ask.bitauto.com"+href;		
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
			int pages = 100;
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
			String urlInfoEnd = "";
			if(result.indexOf("<div class='the_pages'>") > 0 && result.indexOf("\" class=\"next_on\">��һҳ") > 0)
			{
				int index = result.indexOf("<div class='the_pages'>");
				int end = result.indexOf("/\" class=\"next_on\">��һҳ");
				String pageInfo = result.substring(index,end);
				urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),pageInfo.length());
				urlInfoEnd = urlInfo.substring(urlInfo.lastIndexOf("/"),urlInfo.length());
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("/p")+"/p".length());
				System.out.println(urlInfo);
				
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://ask.bitauto.com"+urlInfo+page+urlInfoEnd;
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
			if(result.indexOf("<ul class=\"wt-data-list\"") > 0 && result.indexOf("<div class='the_pages'>") > 0)
			{
				int index = result.indexOf("<ul class=\"wt-data-list\"");
				int end = result.indexOf("<div class='the_pages'>");
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
						String  href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\">"));
						
						href = "http://ask.bitauto.com"+href;
						
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
	
	public static String getCarData(String url)
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
			result = result.substring(result.indexOf("<div class='ask-infor-box zjda-sty'>")-"<div class='ask-infor-box zjda-sty'>".length(),result.length());
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class='ask-infor-box zjda-sty'>") > 0)
			{
				int end = 0;
				if(result.indexOf("<div class=\"login-box\">") > 0)
				{
					end = result.indexOf("<div class=\"login-box\">");
				}
				else if(result.indexOf("��ҳ start") > 0)
				{
					end = result.indexOf("��ҳ start");
				}
				else if(result.indexOf("<input type=\"hidden\" id=\"issamequestion\"") > 0)
				{
					end = result.indexOf("<input type=\"hidden\" id=\"issamequestion\"");
				}
				
				int index = result.indexOf("<div class='ask-infor-box zjda-sty'>");
				//int end = result.indexOf("��ҳ start");
				askerInfo = result.substring(index,end);
			}
			if("".equals(askerInfo))
			{
				System.out.println("δ¼��url��"+url);
				return "";
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<div class=\"ask-tit\">") > 0 && askerInfo.indexOf("<span class=\"h-time\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask-tit\">");
				int end = askerInfo.indexOf("<span class=\"h-time\">");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<h1>")+"<h1>".length(),title.indexOf("</h1>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<span class=\"h-time\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"h-time\">")+"<span class=\"h-time\">".length();
				time = askerInfo.substring(index,askerInfo.length());
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
				logger.error(homeUrl+"��վ����ץȡ����������error"+e);
			}
			//��Ѵ�
			String bestAsk = "";
			if(askerInfo.indexOf("<div class=\"hd-box\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"hd-box\">")+"<div class=\"hd-box\">".length();
				
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
			if(askerInfo.indexOf("<div class=\"hd-box\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"hd-box\">");
				//int end = askerInfo.indexOf("ר�һش� start");
				asktime = askerInfo.substring(index,askerInfo.length());
				int tlen = asktime.indexOf("<span class=\"time-box\">")+"<span class=\"time-box\">".length();
				asktime = asktime.substring(tlen,asktime.length());
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
			if(result.indexOf("ר�һش� start") > 0 && result.indexOf("���ѻش� end") > 0)
			{
				int index = result.indexOf("ר�һش� start");
				int end = result.indexOf("���ѻش� end");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<div class=\"nr-box\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					//�����ش�ش������ʱ��
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span class=\"time-box\">")+"<span class=\"time-box\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("</span>"));
					//ȥ��
					singleAskTime = singleAskTime.trim();
					
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</div>"));
					
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
		return "";
	}
}
