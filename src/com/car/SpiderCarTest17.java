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
 * 北迈网——问答中心
 * 抓取http://ask.beimai.com/
 * @author dzm
 *	此网站无最佳答案，最佳回答时间,问题补充
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//主页有用链接信息
			if(result.indexOf("target=\"_blank\">更多</a>") > 0)
			{
				String[] hinfo = result.split("target=\"_blank\">更多</a>");
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
			InputStreamReader theHTML = new InputStreamReader(in, "UTF-8");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();

			//问题只有一页的情况下
			if(result.indexOf("<div id=\"gpxpager\" class=\"pages\">") == -1)
			{
				System.out.println(url);
				getCategoryHrefInfo(url);
			}
			else
			{
				//获取分类中总页数
				int pages = 0;
				if(result.indexOf("<span class=\"inline mg_r10\">共") > 0)
				{
					int index = result.indexOf("<span class=\"inline mg_r10\">共")+"<span class=\"inline mg_r10\">共".length();
					String pageInfo = result.substring(index,result.length());
					pageInfo = pageInfo.substring(0,pageInfo.indexOf("页"));
					pageInfo = pageInfo.trim();
					pages = Integer.parseInt(pageInfo);
					//System.out.println(pages);
				}
				//获取分类中的分页链接名称url
				String urlInfo = "";
				String urlbe = "";
				String urlend = "";
				if(result.indexOf("\" class=\"inline turnPage\">下一页") > 0)
				{
					int end = result.indexOf("\" class=\"inline turnPage\">下一页");
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
			if(result.indexOf("<div class=\"f_l w920\">") > 0 && result.indexOf("<div class=\"f_r w250\">") > 0)
			{
				int index = result.indexOf("<div class=\"f_l w920\">");
				int end = result.indexOf("<div class=\"f_r w250\">")+"<div class=\"f_r w250\">".length();
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<div class=\"lineH35 bg4 f14 pd_l10 bdT_e3 posR\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"lineH35 bg4 f14 pd_l10 bdT_e3 posR\">");
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</span>"));
				title = title.substring(title.lastIndexOf("\">")+"\">".length(),title.length());
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<span class=\"boxRT mg_r5\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"boxRT mg_r5\">")+"<span class=\"boxRT mg_r5\">".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</span>"));
				//去空
				time = time.trim();
				time = time+ " 00:00:00";
				System.out.println("提问时间："+time);
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
				cq.setKeyword("汽车");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"网站数据抓取，插入问题error"+e);
			}
			
			//其他回答
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
					String singleAskTime = askInfo.substring(askInfo.indexOf("更新：")+"更新：".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("</li>"));
					//去空
					singleAskTime = singleAskTime.trim();
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</li>"));
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
