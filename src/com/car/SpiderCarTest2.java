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
 * 新浪汽车——汽车问答
 * 抓取http://qa.auto.sina.com.cn
 * @author dzm
 *
 */
public class SpiderCarTest2 {
	
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest2.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://qa.auto.sina.com.cn/question/answered";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCategoryPageInfo();
		
		//getCarData("http://qa.auto.sina.com.cn/question/info/2263831/");
	}

	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
	}
	
	
	/***
	 * 获取分类中分页的href链接
	 * @param url
	 */
	public static void getCategoryPageInfo(MongoTemplate template)
	{
		//initMongo();
		mongoTemplate = template;
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
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//获取分类中总页数
		int pages = 0;
		if(result.indexOf("<div class=\"L\"><span>") > 0 && result.indexOf("</span>个问题</div>") > 0)
		{
			int index = result.indexOf("<div class=\"L\"><span>")+"<div class=\"L\"><span>".length();
			int end = result.indexOf("</span>个问题</div>");
			String page = result.substring(index,end);
			
			pages = Integer.parseInt(page.trim());
			if(pages%10 > 0)
			{
				pages = pages/10+1;
			}
			else
			{
				pages = pages/10;
			}
			System.out.println(pages);
		}
		//获取分类中的分页链接名称url
		String urlInfo = "";
		if(result.indexOf("上一页") > 0 && result.indexOf("下一页") > 0)
		{
			int index = result.indexOf("上一页");
			int end = result.indexOf("下一页");
			String pageInfo = result.substring(index,end);
			String[] hinfo = pageInfo.split("<li><a href=\"");
			if(hinfo.length > 0)
			{
				String hrefInfo = hinfo[1];
				String[] hrefs = hrefInfo.split("/\">");
				urlInfo = hrefs[0];
				urlInfo= urlInfo.substring(0,urlInfo.lastIndexOf("/"));
				//System.out.println(urlInfo);
			}
		}
		for(int page = 1; page <= pages; page++)
		{
			String urls = "http://qa.auto.sina.com.cn"+urlInfo+"/"+page+"/";
			//System.out.println(urls);
			getCategoryHrefInfo(urls);
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
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//主页有用链接信息
		String cateInfo = "";
		if(result.indexOf("<ul class=\"c dd\">") > 0 && result.indexOf("<div class=\"page\">") > 0)
		{
			int index = result.indexOf("<ul class=\"c dd\">");
			int end = result.indexOf("<div class=\"page\">");
			cateInfo = result.substring(index,end);
			
			String[] hinfo = cateInfo.split("<!--</div>-->");
			if(hinfo.length > 0)
			{
				for(int i = 0; i < hinfo.length; i++)
				{
					if(i == hinfo.length-1)
					{
						continue;
					}
					String hrefInfo = hinfo[i];
					String href = hrefInfo.substring(hrefInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),hrefInfo.lastIndexOf("\" title=\""));
					
					if(href.startsWith("/"))
					{
						href = "http://qa.auto.sina.com.cn"+href;
					}
				
					
					getCarData(href);
				}
				
			}
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
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		try
		{
			//获取提问者基本信息
			String askerInfo = "";
			if(result.indexOf("<div class=\"bread_nav\">") > -1 && result.indexOf("<div class=\"ll\">") > 0)
			{
				int index = result.indexOf("<div class=\"bread_nav\">")-"<div class=\"bread_nav\">".length();
				int end = result.indexOf("<div class=\"ll\">");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<div class=\"ff\">") > -1 && askerInfo.indexOf("<div class=\"bread_nav\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"bread_nav\">");
				int end = askerInfo.indexOf("<div class=\"ff\">");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<i>")+"<i>".length(),title.length());
				title = title.substring(0,title.indexOf("</i>"));
				//去空
				title = title.trim();
				
				title = CarFormat.Stringformat(title);
				title = title.replaceAll("</?[^>]+>", "");
				
				System.out.println("问题标题："+title);
			}
			//问题补充
			String description = "";
			if(askerInfo.indexOf("<div class=\"ff\">") > -1 && askerInfo.indexOf("<div class=\"gg\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ff\">");
				int end = askerInfo.indexOf("<div class=\"gg\">");
				description = askerInfo.substring(index,end);
				
				//去空
				description = description.trim();
				if(description.indexOf("<p>") > -1 && description.indexOf("</p>") > -1)
				{
					description = description.substring(description.indexOf("<p>"),description.lastIndexOf("</p>"));
				}
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				System.out.println("问题补充："+description);
			}
			
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<p>信誉：<i style=\"color:#ED5900;\">") > 0 && askerInfo.indexOf("发表") > 0)
			{
				int index = askerInfo.indexOf("<p>信誉：<i style=\"color:#ED5900;\">");
				int end = askerInfo.indexOf("发表");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<span>")+"<span>".length(),time.length());
				//去空
				time = time.trim();
				System.out.println("提问时间："+time);
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
				cq.setKeyword("汽车");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"网站数据抓取，插入问题error"+e);
			}
			//最佳答案
			String bestAsk = "";
			//最佳答案回答时间
			String asktime = "";
			if(askerInfo.indexOf("最佳答案") > 0 && askerInfo.indexOf("<dl class=\"n\"></dl>") > 0)
			{
				int index = askerInfo.indexOf("最佳答案");
				int end = askerInfo.indexOf("<dl class=\"n\"></dl>");			
				
				bestAsk = askerInfo.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("最佳答案"),bestAsk.indexOf("<div class=\"L jj_1\">"));
				bestAsk = bestAsk.substring(bestAsk.indexOf("<p>"),bestAsk.indexOf("</p>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//去空
				bestAsk = bestAsk.trim();

				asktime = askerInfo.substring(index,end+"<dl class=\"n\"></dl>".length());
				asktime = asktime.substring(asktime.indexOf("<div style=\"width:84px\"><span>")+"<div style=\"width:84px\"><span>".length(),asktime.indexOf("<dl class=\"n\"></dl>"));
				asktime = asktime.substring(0,asktime.indexOf("</span>"));
				asktime = asktime.trim();
				System.out.println("最佳回答:"+asktime+" "+bestAsk);
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
			
			Map<String, String> hasMap = new HashMap<String,String>();
			//其他回答
			if(result.indexOf("<div style=\"border-top:1px solid #e0e0e0;\" class=\"bl2 mt\">") > 0 && result.indexOf("<div class=\"ll\">") > 0)
			{
				int index = result.indexOf("<div style=\"border-top:1px solid #e0e0e0;\" class=\"bl2 mt\">");
				int end = result.indexOf("<div class=\"ll\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<div class=\"jj_2_2\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("<div style=\"width:84px;\"><span>")+"<div style=\"width:84px;\"><span>".length(),askInfo.indexOf("</span></div>"));
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<div class=\"jj_2_2\">")+"<div class=\"jj_2_2\">".length(),askInfo.indexOf("<div class=\"L jj_1\">"));
					singleAsk = singleAsk.substring(singleAsk.indexOf("<p>")+"<p>".length(),singleAsk.indexOf("</p>"));
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//去空
					singleAsk = singleAsk.trim();
					if(hasMap.get(singleAsk) == null)
					{
						hasMap.put(singleAsk, singleAskTime);
						
						System.out.println("其他回答："+hasMap.size()+"---"+singleAskTime+"-----"+singleAsk);
						
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
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
		
	}
}
