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
 * 新浪汽车——汽车问答
 * 抓取http://k.pcauto.com.cn/
 * @author dzm
 *
 */
public class SpiderCarTest4 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest2.class);
	private static MongoTemplate mongoTemplate;
	//private static String homeUrl = "http://k.pcauto.com.cn";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getHomeHrefInfo();
		//getCarData("http://k.pcauto.com.cn/question/3828751.html");
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
			URL u = new URL("http://k.pcauto.com.cn");
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
			if(result.indexOf("<div class=\"tbB\">") > 0 && result.indexOf("<div class=\"boxwtzs\">") > 0)
			{
				int index = result.indexOf("<div class=\"tbB\">");
				int end = result.indexOf("<div class=\"boxwtzs\">");
				homeInfo = result.substring(index,end);
				
				String[] hinfo = homeInfo.split("<dt><a href=\"");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\">"));
						href = "http://k.pcauto.com.cn"+href;
						//System.out.println(href);
						getSolveCategoryInfo(href);
						
					}
					
				}
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url");
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
			if(result.indexOf("<span class=\"subMark pagination\">") > 0 && result.indexOf("\">已解决") > 0)
			{
				int index = result.indexOf("<span class=\"subMark pagination\">");
				int end = result.indexOf("\">已解决");
				homeInfo = result.substring(index,end);
				String href = homeInfo.substring(homeInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
						
				//System.out.println(href);
				//首页分类链接
				getCategoryPageInfo(href);
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url"+url);
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
			if(result.indexOf("<strong class=\"orange\">") > 0 && result.indexOf("</strong> 条问题</p>") > 0)
			{
				int index = result.indexOf("<strong class=\"orange\">")+"<strong class=\"orange\">".length();
				int end = result.indexOf("</strong> 条问题</p>");
				String page = result.substring(index,end);
				page = page.trim();
				pages = Integer.parseInt(page);
				if(pages%25 > 0)
				{
					pages = pages/25+1;
				}
				else
				{
					pages = pages/25;
				}
				//System.out.println(pages);
				
			}
			//获取分类中的分页链接名称url
			String urlInfo = "";
			if(result.indexOf("<div class=\"pcauto_page\">") > 0 && result.indexOf("<p class=\"pNum\">") > 0)
			{
				int index = result.indexOf("<div class=\"pcauto_page\">");
				int end = result.indexOf("<p class=\"pNum\">");
				String pageInfo = result.substring(index,end);
				
				urlInfo= pageInfo.substring(pageInfo.indexOf("href='")+"href='".length(),pageInfo.indexOf(".html'>"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("/"));
				System.out.println(urlInfo);
				
			}
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://k.pcauto.com.cn"+urlInfo+"/p"+page+".html";
				//System.out.println(urls);
				getCategoryHrefInfo(urls);
			}
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url"+url);
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
			if(result.indexOf("提问时间") > 0 && result.indexOf("<div class=\"pcauto_page\">") > 0)
			{
				int index = result.indexOf("提问时间");
				int end = result.indexOf("<div class=\"pcauto_page\">");
				cateInfo = result.substring(index,end);
				String[] hinfo = {};
				if(cateInfo.contains("<i class=\"iTitle\"><a id=ytest href=\""))
				{
					hinfo = cateInfo.split("<i class=\"iTitle\"><a id=ytest href=\"");
				}
				else if(cateInfo.contains("<i class=\"iTitle\"><a  href=\""))
				{
					hinfo = cateInfo.split("<i class=\"iTitle\"><a  href=\"");
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
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\">"));
						
						
						//System.out.println(i+"  "+href);
						
						getCarData(href);
					}
					
				}
				else
				{
					System.out.println(hinfo.length);
				}
			} 
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println("error-url"+url);
		}
		
	}
	
	public static void getCarData(String url)
	{
		System.out.println("请求链接:"+url);
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
			if(result.indexOf("<div class=\"modOut modPblm\" id=\"question_content\">") > 0 && result.indexOf("<div class=\"layB\">") > 0)
			{
				int index = result.indexOf("<div class=\"modOut modPblm\" id=\"question_content\">");
				int end = result.indexOf("<div class=\"layB\">");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<i class=\"icon_js\"></i>") > 0 && askerInfo.indexOf("<span class=\"sxs orange\">悬赏") > 0)
			{
				int index = askerInfo.indexOf("<i class=\"icon_js\"></i>")+"<i class=\"icon_js\"></i>".length();
				int end = askerInfo.indexOf("<span class=\"sxs orange\">悬赏");
				title = askerInfo.substring(index,end);			
				title = title.substring(0,title.indexOf("</h1>"));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			//问题补充
			//String description = "";
			
			//采集问题标题和问题描述，如果二者重复，保留问题描述即可。
			/*if(title.equals(description))
			{
				
			}*/
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("发布时间：") > 0 && askerInfo.indexOf("<span class=\"sData\">") > 0)
			{
				int index = askerInfo.indexOf("发布时间：")+"发布时间：".length();
				int end = askerInfo.indexOf("<span class=\"sData\">");
				time = askerInfo.substring(index,end);
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
				logger.error(url+"网站数据抓取，插入问题error"+e);
			}
			//最佳答案
			String bestAsk = "";
			if(result.indexOf("<strong>最佳答案：</strong>") > 0 && result.indexOf("<div class=\"exInfo\">") > 0)
			{
				int index = result.indexOf("<strong>最佳答案：</strong>");
				int end = result.indexOf("<div class=\"exInfo\">");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("<div class=\"dInfo gray\">"));
				bestAsk = bestAsk.substring(bestAsk.lastIndexOf("<p>"),bestAsk.lastIndexOf("</p>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//去空
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//最佳答案回答时间
			String asktime = "";
			if(result.indexOf("<strong>最佳答案：</strong>") > 0 && result.indexOf("<div class=\"exInfo\">") > 0)
			{
				int index = result.indexOf("<strong>最佳答案：</strong>");
				int end = result.indexOf("<div class=\"exInfo\">");
				asktime = result.substring(index,end);
				asktime = asktime.substring(0,asktime.indexOf("<div class=\"dInfo gray\">"));
				asktime = asktime.substring(asktime.lastIndexOf("<span class=\"sTime\">")+"<span class=\"sTime\">".length(),asktime.indexOf("</span>"));
				
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
			if(result.indexOf("<strong>其他回答</strong>") > 0 && result.indexOf("<div class=\"layB\">") > 0)
			{
				int index = result.indexOf("<strong>其他回答</strong>");
				int end = result.indexOf("<div class=\"layB\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<i class=\"blue\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					String singleAskTime = askInfo.substring(askInfo.indexOf("</i>")+"</i>".length(),askInfo.indexOf("</div>"));
					singleAskTime = singleAskTime.trim();
					
					
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<p>")+"<p>".length(),askInfo.indexOf("</p>"));
					singleAskTime = singleAskTime.trim();
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
