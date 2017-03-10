package com.car;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/***
 * 汽配人网——问答
 * 抓取http://www.qipeiren.com/ask/
 * @author dzm
 *	此网站无提问时间，最佳回答，最佳回答时间,回答者时间
 */
public class SpiderCarTest11 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://www.qipeiren.com/ask/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://www.qipeiren.com/ask/show_6560.htm");
		//getHomeHrefInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest11");
	}
	/***
	 * 获取首页所有分类的href链接
	 */
	public static void getHomeHrefInfo(MongoTemplate mongo)
	{
		//initMongo();
		mongoTemplate = mongo;
		StringBuffer content = new StringBuffer();
		try {
			// 新建URL对象
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
			
			//主页有用链接信息
			String homeInfo = "";
			if(result.indexOf("<div class=\"askclasscontent\">") > 0 && result.indexOf("<div class=\"askright\">") > 0)
			{
				int index = result.indexOf("<div class=\"askclasscontent\">");
				int end = result.indexOf("<div class=\"askright\">");
				homeInfo = result.substring(index,end);
				String[] hinfo = homeInfo.split("<h2><a href='");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						hrefInfo = hrefInfo.substring(0,hrefInfo.indexOf("'>"));
						hrefInfo = "http://www.qipeiren.com/ask/"+hrefInfo;
						//System.out.println(i+" "+hrefInfo);
						//首页分类链接
						getCategoryPageInfo(hrefInfo);
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
	 * 获取分类中分页的href链接
	 * @param url
	 */
	public static void getCategoryPageInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// 新建URL对象
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
			
			//获取分类中总页数
			int pages = 0;
			if(result.indexOf("'>共") > 0 && result.indexOf("页</span>") > 0)
			{
				int index = result.indexOf("'>共")+"'>共".length();
				int end = result.indexOf("页</span>");
				String page = result.substring(index,end);
				pages = Integer.parseInt(page.trim());
				//System.out.println(pages);
			}
			//获取分类中的分页链接名称url
			String urlInfo = "";
			if(result.indexOf("<span class='next'><a href=") > 0 && result.indexOf(">下一页") > 0)
			{
				int index = result.indexOf("<span class='next'><a href=")+"<span class='next'><a href=".length();
				int end = result.indexOf(">下一页");
				String pageInfo = result.substring(index,end);
				if(pageInfo.endsWith(".htm"))
				{
					pageInfo = pageInfo.substring(0,pageInfo.indexOf(".htm"));
					pageInfo = pageInfo.substring(0,pageInfo.lastIndexOf("_"));
				}
				urlInfo = pageInfo.trim();
				
				//System.out.println(urlInfo);
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://www.qipeiren.com/ask/"+urlInfo+"_"+page+".htm";
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
	 * 获取分类中所有问题的href链接
	 * @param url
	 */
	public static void getCategoryHrefInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// 新建URL对象
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

			//主页有用链接信息
			String cateInfo = "";
			if(result.indexOf("<div class=\"asklistleft_contentwt\"") > 0 && result.indexOf("asklistleft end") > 0)
			{
				int index = result.indexOf("<div class=\"asklistleft_contentwt\"");
				int end = result.indexOf("asklistleft end");
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
						hrefInfo = hrefInfo.substring(0,hrefInfo.lastIndexOf("\" title="));
						String href =hrefInfo.trim();
						href = "http://www.qipeiren.com/ask/"+href;
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
			// 新建URL对象
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
			//获取提问者基本信息
			String askerInfo = "";
			if(result.indexOf("<div class=\"askcontent\">") > 0 && result.indexOf("相关已解答问题") > 0)
			{
				int index = result.indexOf("<div class=\"askcontent\">");
				int end = result.indexOf("相关已解答问题");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<div class=\"asklistleft\">") > 0 && askerInfo.indexOf("<div class=\"asklistleft_contentwd\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"asklistleft\">");
				int end = askerInfo.indexOf("<div class=\"asklistleft_contentwd\">");
				title = askerInfo.substring(index,end);
				title = title.substring(0,title.indexOf("</b>"));
				title = title.substring(title.indexOf("/>")+"/>".length(),title.length());
				
				title = CarFormat.Stringformat(title);
				title = title.replaceAll("</?[^>]+>", "");
				
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			//问题补充
			String description = "";
			if(askerInfo.indexOf("<div class=\"asklistleft_contentwd\">") > 0 && askerInfo.indexOf("<div class=\"asklistlistclass margintop10 bgcolor1") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"asklistleft_contentwd\">");
				int end = askerInfo.indexOf("<div class=\"asklistlistclass margintop10 bgcolor1");
				description = askerInfo.substring(index,end);
				description = description.substring(description.indexOf("<li>")+"<li>".length(),description.indexOf("</li>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				 
				//去空
				description = description.trim();
				System.out.println("问题补充："+description);
			}
			
			
			CarQuestion cq = new CarQuestion();
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			try
			{
				cq.setTitle(title);
				cq.setContent(description);
				cq.setSource(url);
				
				cq.setCreateTime(new Date());
				cq.setKeyword("汽车");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"网站数据抓取，插入问题error"+e);
			}
			
			//其他回答
			if(result.indexOf("<div class=\"asklistlistclass margintop10 bgcolor1") > 0 && result.indexOf("<div class=\"asklistlistclass margintop10\">") > 0)
			{
				int index = result.indexOf("<div class=\"asklistlistclass margintop10 bgcolor1");
				int end = result.indexOf("<div class=\"asklistlistclass margintop10\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<ul class=\"bgcolorw\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					
					String singleAsk =askInfo.substring(askInfo.indexOf("<li>")+"<li>".length(),askInfo.indexOf("</li>"));
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//去空
					singleAsk = singleAsk.trim();
					
					System.out.println("其他回答："+" "+"-----"+singleAsk);
					
					try
					{
						CarAnswer ca = new CarAnswer();
						ca.setQuestionId(cq.getId());
						ca.setContent(singleAsk);
						ca.setSource(url);
						/*if(!"".equals(singleAskTime))
					{
						ca.setAskTime(sdf.parse(singleAskTime));
					}*/
						ca.setCreateTime(new Date());
						ca.setBestAnswer("0");
						mongoTemplate.insert(ca);
					}
					catch(Exception e)
					{
						logger.error(url+"网站数据抓取，插入其他答案error"+e);
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
