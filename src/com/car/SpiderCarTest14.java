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
 * 车多少——好回答
 * 抓取http://wenda.cheduoshao.com/hwdList_page1/
 * @author dzm
 * 此网站无问题补充,最佳回答时间，最佳回答
 */
public class SpiderCarTest14 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://wenda.cheduoshao.com/hwdList_page1/";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://wenda.cheduoshao.com/knlgRead/991156_1.html");
		//getHomeHrefInfo();
	}
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest14");
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
			if(result.indexOf("全部好问答") > 0 && result.indexOf("查看全部已解决问题 >") > 0)
			{
				int index = result.indexOf("全部好问答");
				int end = result.indexOf("查看全部已解决问题 >");
				homeInfo = result.substring(index,end);
				homeInfo = homeInfo.substring(homeInfo.indexOf("<a href=\"")+"<a href=\"".length(),homeInfo.length());
				homeInfo = homeInfo.substring(0,homeInfo.indexOf("\">"));
				homeInfo = "http://wenda.cheduoshao.com"+homeInfo;
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
			if(result.indexOf("<div class=\"c_page clearfix\">") > 0 && result.indexOf("下一页") > 0)
			{
				int index = result.indexOf("<div class=\"c_page clearfix\">");
				int end = result.indexOf("下一页");
				String pageInfo = result.substring(index,end);
				
				String page = pageInfo.substring(0,pageInfo.lastIndexOf("</a>"));
				page = page.substring(page.lastIndexOf("'>")+"'>".length(),page.length());
				pages = Integer.parseInt(page);
				urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a class='c_next' href='")+"<a class='c_next' href='".length(),pageInfo.lastIndexOf("'>"));
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("_page")+"_page".length());
				
				//System.out.println(pages+" "+urlInfo);
			}
			
			
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://wenda.cheduoshao.com"+urlInfo+page+"/";
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
			if(result.indexOf("<div class=\"ask_list\">") > 0 && result.indexOf("<div class=\"c_page clearfix\">") > 0)
			{
				int index = result.indexOf("<div class=\"ask_list\">");
				int end = result.indexOf("<div class=\"c_page clearfix\">");
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
						String href = hrefInfo.substring(0,hrefInfo.indexOf("\" target=\"_blank\""));
						href = "http://wenda.cheduoshao.com" + href;
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
			if(result.indexOf("<div class=\"blank11\"></div>") > 0 && result.indexOf("<div class=\"blank15\"></div>") > 0)
			{
				int index = result.indexOf("<div class=\"blank11\"></div>");
				int end = result.indexOf("<div class=\"blank15\"></div>");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<span class=\"c_000000\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"c_000000\">")+"<span class=\"c_000000\">".length();
				//int end = askerInfo.indexOf("<div class=\"user-box\">");
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</span>"));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			//问题补充
			//String description = "";
		
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("<span class=\"c_000000\">") > 0 && askerInfo.indexOf("<li class=\"list_head_li c_size12 c_666666\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"c_000000\">");
				int end = askerInfo.indexOf("<li class=\"list_head_li c_size12 c_666666\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<li class=\"c_size12 c_aaaaaa\">")+"<li class=\"c_size12 c_aaaaaa\">".length(),time.indexOf("&nbsp;&nbsp;"));
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
			
			//其他回答
			if(result.indexOf("<li class=\"c_aaaaaa c_size14\">") > 0 && result.indexOf("<li class='blank10'></li>") > 0)
			{
				int index = result.indexOf("<li class=\"c_aaaaaa c_size14\">");
				int end = result.indexOf("<li class='blank10'></li>");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("<span class=\"c_666666\">");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					
					
					//其他回答回答问题的时间
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span class=\"f_l\">")+"<span class=\"f_l\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("&nbsp;&nbsp;"));
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
