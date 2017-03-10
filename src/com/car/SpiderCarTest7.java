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
 * 搜狐汽车——问答
 * 抓取http://ask.auto.sohu.com/
 * @author dzm
 *
 */
public class SpiderCarTest7 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://ask.auto.sohu.com";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://ask.auto.sohu.com/q-3579799.shtml");
		//getHomeHrefInfo();
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
			if(result.indexOf("<dl class=\"category_list\">") > 0 && result.indexOf("<div class=\"weibo\">") > 0)
			{
				int index = result.indexOf("<dl class=\"category_list\">");
				int end = result.indexOf("<div class=\"weibo\">");
				homeInfo = result.substring(index,end);
				
				String[] hinfo = homeInfo.split("href=\"");
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
						if(!href.endsWith(".shtml"))
						{
							href = href.substring(0,href.indexOf(".shtml"));
							href = href+".shtml";
						}
						//System.out.println(i+"  "+href);
						//首页分类链接
						getSolveCategoryInfo(href);
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
			if(result.indexOf("<div class=\"t_tab user_tab\">") > 0 && result.indexOf("\">已解决") > 0)
			{
				int index = result.indexOf("<div class=\"t_tab user_tab\">");
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
			String urlInfo = "";
			if(result.indexOf("<span class=\"pagenum\">") > 0 && result.indexOf("\">尾页") > 0)
			{
				int index = result.indexOf("<span class=\"pagenum\">");
				int end = result.indexOf("\">尾页");
				String pageInfo = result.substring(index,end);
				pageInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),pageInfo.length());
				if(pageInfo.endsWith(".shtml"))
				{
					pageInfo = pageInfo.substring(0,pageInfo.indexOf(".shtml"));
					String page = pageInfo.substring(pageInfo.lastIndexOf("_")+1,pageInfo.length());
					pages = Integer.parseInt(page);
					urlInfo = pageInfo.substring(0,pageInfo.lastIndexOf("_"));
				}
				//System.out.println(pages+" "+urlInfo);
			}
			
			
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://ask.auto.sohu.com"+urlInfo+"_"+page+".shtml";
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
			if(result.indexOf("<div class=\"t_tab_list\">") > 0 && result.indexOf("<div class=\"page pers\">") > 0)
			{
				int index = result.indexOf("<div class=\"t_tab_list\">");
				int end = result.indexOf("<div class=\"page pers\">");
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
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\">"));
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
			if(result.indexOf("<div class=\"askSituation\">") > 0 && result.indexOf("<h2 style=\"margin-top:20px\">") > 0)
			{
				int index = result.indexOf("<div class=\"askSituation\">");
				int end = result.indexOf("<h2 style=\"margin-top:20px\">");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<h1 style=\"padding-top:7px;font-size:14px;margin-left:0px\">") > 0 && askerInfo.indexOf("<span class=\"txt_red\">") > 0)
			{
				int index = askerInfo.indexOf("<h1 style=\"padding-top:7px;font-size:14px;margin-left:0px\">")+"<h1 style=\"padding-top:7px;font-size:14px;margin-left:0px\">".length();
				int end = askerInfo.indexOf("<span class=\"txt_red\">");
				title = askerInfo.substring(index,end);
				//去空
				title = title.trim();
				title = title.substring(0,title.indexOf("</h1>"));
				System.out.println("问题标题："+title);
			}
			//问题补充
			String description = "";
			if(askerInfo.indexOf("<span class=\"dis_block\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"dis_block\">")+"<span class=\"dis_block\">".length();
				description = askerInfo.substring(index,askerInfo.length());
				description = description.substring(0,description.indexOf("</span>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//去空
				description = description.trim();
				System.out.println("问题补充："+description);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("提问者") > 0)
			{
				int index = askerInfo.indexOf("提问者")+"提问者".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(time.indexOf("<span class=\"date\">")+"<span class=\"date\">".length(),time.indexOf("</span>"));
				
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
			//最佳答案
			String bestAsk = "";
			if(result.indexOf("<h2 class=\"ok\" style=\"border-color:orange orange #BDBDBD orange;\">") > 0 && result.indexOf("<h2 class=\"other\">") > 0)
			{
				int index = result.indexOf("<h2 class=\"ok\" style=\"border-color:orange orange #BDBDBD orange;\">");
				int end = result.indexOf("<h2 class=\"other\">");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("<div class=\"situationContent\""),bestAsk.length());
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("</span>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
			
				//去空
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//最佳答案回答时间
			String asktime = "";
			if(result.indexOf("<h2 class=\"ok\" style=\"border-color:orange orange #BDBDBD orange;\">") > 0 && result.indexOf("<h2 class=\"other\">") > 0)
			{
				int index = result.indexOf("<h2 class=\"ok\" style=\"border-color:orange orange #BDBDBD orange;\">");
				int end = result.indexOf("<h2 class=\"other\">");
				asktime = result.substring(index,end);
				asktime = asktime.substring(asktime.indexOf("<div class=\"situationContent\""),asktime.length());
				asktime = asktime.substring(asktime.indexOf("<span class=\"date\">")+"<span class=\"date\">".length(),asktime.length());
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
			if(result.indexOf("<h2 class=\"other\">") > 0 && result.indexOf("<h2 style=\"margin-top:20px\">") > 0)
			{
				int index = result.indexOf("<h2 class=\"other\">");
				int end = result.indexOf("<h2 style=\"margin-top:20px\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<span class=\"sp_line\"></span>");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == asks.length-1)
					{
						continue;
					}
					String askInfo = asks[i];
					askInfo = askInfo.substring(askInfo.indexOf("<span id="),askInfo.length());
					
					
					
					//其他回答回答问题的时间
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span class=\"date\">")+"<span class=\"date\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("</span>"));
					singleAskTime = singleAskTime.trim();
					
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</span>"));;
					
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
