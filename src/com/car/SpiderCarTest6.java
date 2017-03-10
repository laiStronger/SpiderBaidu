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
 * 搜搜问问
 * 抓取http://wenwen.sogou.com/cate/?cid=87228416
 * @author dzm
 * 此网站点击分类下的分类，代码隐蔽，不需直接点击分类，直接找已解决的
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
	 * 获取已解决的分类
	 * @param url
	 */
	public static void getSolveCategoryInfo(MongoTemplate template)
	{
		//initMongo();
		mongoTemplate = template;
		StringBuffer content = new StringBuffer();
		try {
			// 新建URL对象
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
			
			//主页有用链接信息
			String homeInfo = "";
			if(result.indexOf("已解决</a></li>") > 0)
			{
				int index = result.indexOf("\">已解决</a></li>");
				homeInfo = result.substring(0,index);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				href = "http://wenwen.sogou.com"+href;		
				//System.out.println(href);
				//首页分类链接
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//获取分类中总页数
			int pages = 50;
			/*if(result.indexOf("<input type=\"text\" class=\"page-item-jump-text\"") > 0 && result.indexOf("<a id=\"jumpbtn\"") > 0)
			{
				int index = result.indexOf("<input type=\"text\" class=\"page-item-jump-text\"");
				int end = result.indexOf("<a id=\"jumpbtn\"");
				String pageInfo = result.substring(index,end);
				if(pageInfo.indexOf("/共") > 0)
				{
					String[] pageInfos = pageInfo.split("/共");
					String pageinfo = pageInfos[1];
					String[] ps = pageinfo.split("页");
					String page = ps[0];
					pages = Integer.parseInt(page);
					System.out.println(pages);
				}
			}*/
			//获取分类中的分页链接名称url
			String urlInfo = "";
			if(result.indexOf("<div class=\"pagination\">") > 0 && result.indexOf("\" class=\"page_turn\">下一页") > 0)
			{
				int index = result.indexOf("<div class=\"pagination\">");
				int end = result.indexOf("\" class=\"page_turn\">下一页");
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();

			//主页有用链接信息
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
		System.out.println("URL链接："+url);
		StringBuffer content = new StringBuffer();
		try {
			// 新建URL对象
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
			//获取提问者基本信息
			String askerInfo = "";
			if(result.indexOf("<div class=\"question-main\">") > 0 && result.indexOf("<div id=\"otherQuestions_list\"") > 0)
			{
				int index = result.indexOf("<div class=\"question-main\">");
				int end = result.indexOf("<div id=\"otherQuestions_list\"")+"<div id=\"otherQuestions_list\"".length();
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<h3 id=\"questionTitle\">") > 0)
			{
				int index = askerInfo.indexOf("<h3 id=\"questionTitle\">")+"<h3 id=\"questionTitle\">".length();
				
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</h3>"));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<div class=\"question-info\">") > 0 && result.indexOf("<div class=\"question-tit\"") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"question-info\">")+"<div class=\"question-info\">".length();
				int end = askerInfo.indexOf("<div class=\"question-tit\"");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<span class=\"time\">")+"<span class=\"time\">".length(),time.length());
				time = time.substring(0,time.indexOf("</span>"));
				//去空
				time = time.trim();
				System.out.println("提问时间："+time);
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
				cq.setKeyword("汽车");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"网站数据抓取，插入问题error"+e);
			}
			//最佳答案
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
				
				//去空
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//最佳答案回答时间
			String asktime = "";
			if(result.indexOf("<div class=\"ico-pending\">") > 0 && result.indexOf("<div class=\"answer-wrap\">") > 0)
			{
				int index = result.indexOf("<div class=\"ico-pending\">");
				int end = result.indexOf("<div class=\"answer-wrap\">");
				asktime = result.substring(index,end);
				asktime = asktime.substring(asktime.indexOf("<span class=\"time\">")+"<span class=\"time\">".length(),asktime.length());
				asktime = asktime.substring(0,asktime.indexOf("</span>"));
				//去空
				asktime = asktime.trim();
				System.out.println("最佳回答："+asktime+"-----"+bestAsk);
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
					logger.error(url+"网站数据抓取，插入最佳答案error"+e);
				}
			}
			//其他回答
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
					//其他回答回答问题的时间
					String singleAskTime = askInfo.substring(0,askInfo.indexOf("</span>"));;
					//去空
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<div class=\"answer-con\">")+"<div class=\"answer-con\">".length(),askInfo.length());
					singleAsk = singleAsk.substring(0,singleAsk.indexOf("</div>"));
					if(singleAsk.indexOf("<a target=\"_blank\"") > -1)
					{
						singleAsk = singleAsk.substring(0,singleAsk.indexOf("<a target=\"_blank\""));
					}
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//去空
					singleAsk = singleAsk.trim();
					
					listStr.add(singleAsk);
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
