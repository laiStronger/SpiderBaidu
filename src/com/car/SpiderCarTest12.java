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
 * 易车二手车网——问答
 * 抓取http://www.taoche.com/ask/
 * @author dzm
 *
 */
public class SpiderCarTest12 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://www.taoche.com/ask";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://www.taoche.com/ask/1059652.html");
		//getHomeHrefInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest12");
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
			String homeInfo = "";
			if(result.indexOf("<div class=\"tc14-line-box  wdzxlist clearfix\">") > 0 && result.indexOf("<ul class=\"wt-data-list\" id=\"newQuestion\"") > 0)
			{
				int index = result.indexOf("<div class=\"tc14-line-box  wdzxlist clearfix\">");
				int end = result.indexOf("<ul class=\"wt-data-list\" id=\"newQuestion\"");
				homeInfo = result.substring(index,end);
				homeInfo = homeInfo.substring(homeInfo.indexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				homeInfo = homeInfo.substring(0,homeInfo.indexOf("\" target=\"_blank\""));
				homeInfo = "http://www.taoche.com"+homeInfo;
				//System.out.println(homeInfo);
				getCategoryPageInfo(homeInfo);
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
			int pages = 0;
			String urlInfo = "";
			if(result.indexOf("<span id=\"ucPager\">") > 0 && result.indexOf("></a></div></div>") > 0)
			{
				int index = result.indexOf("<span id=\"ucPager\">");
				int end = result.indexOf("></a></div></div>");
				String pageInfo = result.substring(index,end);
				
				String page = pageInfo.substring(0,pageInfo.lastIndexOf("</a>"));
				page = page.substring(page.lastIndexOf("\">")+"\">".length(),page.length());
				pages = Integer.parseInt(page);
				urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),pageInfo.lastIndexOf("\" class=\"next_on\">"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("=")+"=".length());
				
				//System.out.println(pages+" "+urlInfo);
			}
			
			
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://www.taoche.com"+urlInfo+page;
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
			if(result.indexOf("<ul id=\"ulList\"") > 0 && result.indexOf("<span id=\"ucPager\">") > 0)
			{
				int index = result.indexOf("<ul id=\"ulList\"");
				int end = result.indexOf("<span id=\"ucPager\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("<a href='");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("' target='_blank'"));
						href = "http://www.taoche.com" + href;
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
			if(result.indexOf("<div class=\"bt_page tc14-line-20 \">") > 0 && result.indexOf("<div class=\"title-box2\">") > 0)
			{
				int index = result.indexOf("<div class=\"bt_page tc14-line-20 \">");
				int end = result.indexOf("<div class=\"title-box2\">");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<div class=\"ask-tit\">") > 0 && askerInfo.indexOf("<div class=\"user-box\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask-tit\">");
				int end = askerInfo.indexOf("<div class=\"user-box\">");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<h1>")+"<h1>".length(),title.indexOf("<a class=\""));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			//问题补充
			String description = "";
			if(askerInfo.indexOf("<div class=\"ask-con\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask-con\">")+"<div class=\"ask-con\">".length();
				description = askerInfo.substring(index,askerInfo.length());
				description = description.substring(0,description.indexOf("</div>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//去空
				description = description.trim();
				System.out.println("问题补充："+description);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<div class=\"ask-tit\">") > 0 && askerInfo.indexOf("<div class=\"user-box\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"ask-tit\">");
				int end = askerInfo.indexOf("<div class=\"user-box\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<span class=\"h-time\">")+"<span class=\"h-time\">".length(),time.indexOf("</span>"));
				//去空
				time = time.trim();
				time = time.replaceAll("/", "-");
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
			if(result.indexOf("<div id=\"divBestAnswer\"") > 0 && result.indexOf("<div class=\"tc14-fctit\">") > 0)
			{
				int index = result.indexOf("<div id=\"divBestAnswer\"");
				int end = result.indexOf("<div class=\"tc14-fctit\">");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("<div class=\"nr-box\">")+"<div class=\"nr-box\">".length(),bestAsk.length());
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("</div>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//去空
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//最佳答案回答时间
			String asktime = "";
			if(result.indexOf("<div id=\"divBestAnswer\"") > 0 && result.indexOf("<div class=\"tc14-fctit\">") > 0)
			{
				int index = result.indexOf("<div id=\"divBestAnswer\"");
				int end = result.indexOf("<div class=\"tc14-fctit\">");
				asktime = result.substring(index,end);
				asktime = asktime.substring(asktime.indexOf("<span class=\"time-box\">")+"<span class=\"time-box\">".length(),asktime.length());
				asktime = asktime.substring(0,asktime.indexOf("</span>"));
				
				//去空
				asktime = asktime.trim();
				asktime = asktime.replaceAll("/", "-");
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
			if(result.indexOf("<div id=\"divCertifiedAnswerList\"") > 0 && result.indexOf("<div class=\"tc14-line-box  wdzxlist clearfix\">") > 0)
			{
				int index = result.indexOf("<div id=\"divCertifiedAnswerList\"");
				int end = result.indexOf("<div class=\"tc14-line-box  wdzxlist clearfix\">");
				
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
					singleAskTime = singleAskTime.trim();
					singleAskTime = singleAskTime.replaceAll("/", "-");
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</div>"));;
					
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
