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
 * 爱意汽车网——汽车答疑首页
 * 抓取http://qcar.ieche.com/
 * @author dzm
 *	此网站无最佳答案，最佳回答时间
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
						//首页分类链接
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

			//问题只有一页的情况下
			if(result.indexOf("</span>/1</b>") > -1)
			{
				//System.out.println(url);
				getCategoryHrefInfo(url);
			}
			else
			{
				//获取分类中总页数
				int pages = 0;
				if(result.indexOf("<div class=\"page_nav\">") > 0 && result.indexOf(" title=\"尾页\">") > 0)
				{
					int index = result.indexOf("<div class=\"page_nav\">");
					int end = result.indexOf(" title=\"尾页\">");
					String pageInfo = result.substring(index,end);
					if(!pageInfo.endsWith("<font color=#999999"))
					{
						pageInfo = pageInfo.substring(pageInfo.lastIndexOf("=")+"=".length(),pageInfo.length());
						pages = Integer.parseInt(pageInfo);
						//System.out.println(pages);
					}
				}
				//获取分类中的分页链接名称url
				String urlInfo = "";
				if(result.indexOf("<div class=\"page_nav\">") > 0 && result.indexOf(" title=\"尾页\">") > 0)
				{
					int index = result.indexOf("<div class=\"page_nav\">");
					int end = result.indexOf(" title=\"尾页\">");
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
			if(result.indexOf("<div id=\"content\">") > 0 && result.indexOf("<div class=\"page_nav\">") > 0)
			{
				int index = result.indexOf("<div id=\"content\">");
				int end = result.indexOf("<div class=\"page_nav\">");
				cateInfo = result.substring(index,end);
				String[] hinfo = {};
				//车吧分类
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
		System.out.println("url链接："+url);
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
			if(result.indexOf("<div id=\"content\">") > 0 && result.indexOf("<div class=\"commentin\">") > 0)
			{
				int index = result.indexOf("<div id=\"content\">");
				int end = result.indexOf("<div class=\"commentin\">");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<h1 id=\"title\">") > 0)
			{
				int index = askerInfo.indexOf("<h1 id=\"title\">")+"<h1 id=\"title\">".length();
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</h1>"));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			//问题补充
			String description = "";
			if(askerInfo.indexOf("<div class=\"review_con\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"review_con\">");
				description = askerInfo.substring(index,askerInfo.length());
				description = description.substring(description.indexOf("<p>"),description.indexOf("</p>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//去空
				description = description.trim();
				System.out.println("问题补充："+description);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<li>时间：<em>") > 0)
			{
				int index = askerInfo.indexOf("<li>时间：<em>")+"<li>时间：<em>".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</em></li>"));
				//去空
				time = time.trim();
				System.out.println("提问时间："+time);
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
				cq.setKeyword("汽车");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"网站数据抓取，插入问题error"+e);
			}
			
			//其他回答
			if(result.indexOf("<ul id=\"postContentList\">") > 0 && result.indexOf("回复结束") > 0)
			{
				int index = result.indexOf("<ul id=\"postContentList\">");
				int end = result.indexOf("回复结束");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<dd>");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("<small>回答者："),askInfo.length());
					singleAskTime = singleAskTime.substring(singleAskTime.indexOf("| ")+"| ".length(),singleAskTime.indexOf("</small>"));
					//去空
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</dd>"));
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//去空
					singleAsk = singleAsk.trim();
					
					System.out.println("其他回答："+singleAskTime+"-----"+singleAsk);
					
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
