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
 * 易车网——问答
 * 抓取http://ask.bitauto.com/
 * @author dzm
 * 没有搞定
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
	 * 获取首页所有分类的href链接
	 */
	public static void getHomeHrefInfo(MongoTemplate template)
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
								
								//首页分类链接
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
	 * 获取已解决的分类
	 * @param url
	 */
	public static void getSolveCategoryInfo(String url)
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
			String homeInfo = "";
			if(result.indexOf("<div class=\"box-sty-new left-box-sty question-list\">") > 0 && result.indexOf("\">已解决</a></li>") > 0)
			{
				int index = result.indexOf("<div class=\"box-sty-new left-box-sty question-list\">");
				int end = result.indexOf("\">已解决</a></li>");
				homeInfo = result.substring(index,end);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				href = "http://ask.bitauto.com"+href;		
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
			int pages = 100;
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
			String urlInfoEnd = "";
			if(result.indexOf("<div class='the_pages'>") > 0 && result.indexOf("\" class=\"next_on\">下一页") > 0)
			{
				int index = result.indexOf("<div class='the_pages'>");
				int end = result.indexOf("/\" class=\"next_on\">下一页");
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
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		try
		{
			result = result.substring(result.indexOf("<div class='ask-infor-box zjda-sty'>")-"<div class='ask-infor-box zjda-sty'>".length(),result.length());
			//获取提问者基本信息
			String askerInfo = "";
			if(result.indexOf("<div class='ask-infor-box zjda-sty'>") > 0)
			{
				int end = 0;
				if(result.indexOf("<div class=\"login-box\">") > 0)
				{
					end = result.indexOf("<div class=\"login-box\">");
				}
				else if(result.indexOf("分页 start") > 0)
				{
					end = result.indexOf("分页 start");
				}
				else if(result.indexOf("<input type=\"hidden\" id=\"issamequestion\"") > 0)
				{
					end = result.indexOf("<input type=\"hidden\" id=\"issamequestion\"");
				}
				
				int index = result.indexOf("<div class='ask-infor-box zjda-sty'>");
				//int end = result.indexOf("分页 start");
				askerInfo = result.substring(index,end);
			}
			if("".equals(askerInfo))
			{
				System.out.println("未录入url："+url);
				return "";
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<div class=\"ask-tit\">") > 0 && askerInfo.indexOf("<span class=\"h-time\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask-tit\">");
				int end = askerInfo.indexOf("<span class=\"h-time\">");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<h1>")+"<h1>".length(),title.indexOf("</h1>"));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<span class=\"h-time\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"h-time\">")+"<span class=\"h-time\">".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</span>"));
				//去空
				time = time.trim();
				System.out.println("提问时间："+time);
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
				cq.setKeyword("汽车");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(homeUrl+"网站数据抓取，插入问题error"+e);
			}
			//最佳答案
			String bestAsk = "";
			if(askerInfo.indexOf("<div class=\"hd-box\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"hd-box\">")+"<div class=\"hd-box\">".length();
				
				bestAsk = askerInfo.substring(index,askerInfo.length());
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("</div>"));
			
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//去空
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//最佳答案回答时间
			String asktime = "";
			if(askerInfo.indexOf("<div class=\"hd-box\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"hd-box\">");
				//int end = askerInfo.indexOf("专家回答 start");
				asktime = askerInfo.substring(index,askerInfo.length());
				int tlen = asktime.indexOf("<span class=\"time-box\">")+"<span class=\"time-box\">".length();
				asktime = asktime.substring(tlen,asktime.length());
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
			if(result.indexOf("专家回答 start") > 0 && result.indexOf("网友回答 end") > 0)
			{
				int index = result.indexOf("专家回答 start");
				int end = result.indexOf("网友回答 end");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<div class=\"nr-box\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					//其他回答回答问题的时间
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span class=\"time-box\">")+"<span class=\"time-box\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("</span>"));
					//去空
					singleAskTime = singleAskTime.trim();
					
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</div>"));
					
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
		return "";
	}
}
