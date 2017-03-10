package com.car;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
/***
 * 福特车友会——专家解答
 * 抓取http://www.iford.cn/plugin.php?id=hux_zhidao:hux_zhidao
 * @author dzm
 * 没有搞定
 */
public class SpiderCarTest13 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://www.iford.cn/plugin.php?id=hux_zhidao:hux_zhidao";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://www.iford.cn/thread-214861-1-1.html");
		//getHomeHrefInfo();
	}
	
	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest13");
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
			URL urlInfo = new URL(homeUrl);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfo.openConnection();// 建立链接

			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
			connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			InputStream inputStream = connection.getInputStream();
			InputStreamReader theHTML = new InputStreamReader(inputStream, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			inputStream.close();
			theHTML.close();
			
			String result = content.toString();
			
			//主页有用链接信息
			String homeInfo = "";
			if(result.indexOf("<li class=sortlist>") > 0 && result.indexOf("<strong>请先登录</strong>") > 0)
			{
				int index = result.indexOf("<li class=sortlist>");
				int end = result.indexOf("<strong>请先登录</strong>");
				homeInfo = result.substring(index,end);
				String[] homes = homeInfo.split("\">更多</a></h2>");
				for(int i = 0; i < homes.length; i++)
				{
					if(i == homes.length-1)
					{
						continue;
					}
					String home = homes[i];
					homeInfo = home.substring(home.lastIndexOf("<a href=\"")+"<a href=\"".length(),home.length());
					homeInfo = homeInfo.replaceAll("amp;", "");
					homeInfo = "http://www.iford.cn/"+homeInfo;
					//System.out.println(homeInfo);
					getCategoryPageInfo(homeInfo);
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
			URL urlInfos = new URL(url);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfos.openConnection();// 建立链接

			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
			connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			InputStream inputStream = connection.getInputStream();
			InputStreamReader theHTML = new InputStreamReader(inputStream, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			inputStream.close();
			theHTML.close();
			
			String result = content.toString();
			
			//获取分类中总页数
			int pages = 0;
			if(result.indexOf("<span title=\"共 ") > 0 && result.indexOf(" 页\">") > 0)
			{
				int index = result.indexOf("<span title=\"共 ")+"<span title=\"共 ".length();
				int end = result.indexOf(" 页\">");
				String pageInfo = result.substring(index,end);
				
				String page = pageInfo.trim();
				pages = Integer.parseInt(page);
				
				//System.out.println(pages);
			}
			//获取分类中的分页链接名称url
			String urlInfo = "";
			if(result.indexOf("<span title=\"共") > 0 && result.indexOf("\" class=\"nxt\">下一页</a></div>") > 0)
			{
				int index = result.indexOf("<span title=\"共");
				int end = result.indexOf("\" class=\"nxt\">下一页</a></div>");
				String pageInfo = result.substring(index,end);
				urlInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href=\"")+"<a href=\"".length(),pageInfo.length());
				urlInfo = urlInfo.substring(0,urlInfo.lastIndexOf("=")+"=".length());
				urlInfo = urlInfo.replaceAll("amp;", "");
				//System.out.println(urlInfo);
			}
			
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://www.iford.cn/"+urlInfo+page;
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
			URL urlInfo = new URL(url);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfo.openConnection();// 建立链接

			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
			connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			InputStream inputStream = connection.getInputStream();
			InputStreamReader theHTML = new InputStreamReader(inputStream, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			inputStream.close();
			theHTML.close();
			
			String result = content.toString();
			
			//主页有用链接信息
			String cateInfo = "";
			if(result.indexOf("问题列表</a></h2>") > 0 && result.indexOf("<div class=\"pg\">") > 0)
			{
				int index = result.indexOf("问题列表</a></h2>");
				int end = result.indexOf("<div class=\"pg\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("有问有答区");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == hinfo.length-1)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(hrefInfo.indexOf("<a href=\"")+"<a href=\"".length(),hrefInfo.length());
						href = href.substring(0,href.indexOf("\" target=\"_blank\""));
						href = "http://www.iford.cn/" + href;
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
			URL urlInfo = new URL(url);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfo.openConnection();// 建立链接

			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
			connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			InputStream inputStream = connection.getInputStream();
			InputStreamReader theHTML = new InputStreamReader(inputStream, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			inputStream.close();
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
			if(result.indexOf("<td class=\"plc vwthd\">") > 0 && result.indexOf("<div class=\"pgs mtm mbm cl\">") > 0)
			{
				int index = result.indexOf("<td class=\"plc vwthd\">");
				int end = result.indexOf("<div class=\"pgs mtm mbm cl\">");
				askerInfo = result.substring(index,end);
			}
			//问题标题
			String title = "";
			if(askerInfo.indexOf("<span id=\"thread_subject\">") > 0)
			{
				int index = askerInfo.indexOf("<span id=\"thread_subject\">")+"<span id=\"thread_subject\">".length();
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</span>"));
				//去空
				title = title.trim();
				System.out.println("问题标题："+title);
			}
			//问题补充
			String description = "";
			if(askerInfo.indexOf("<div class=\"rwdn\">") > 0 && askerInfo.indexOf("<div id=\"p_btn\" class=\"mtw mbm hm cl\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"rwdn\">");
				int end = askerInfo.indexOf("<div id=\"p_btn\" class=\"mtw mbm hm cl\">");
				description = askerInfo.substring(index,end);
				description = description.substring(0,description.indexOf("</table>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//去空
				description = description.trim();
				System.out.println("问题补充："+description);
			}
			
			//问题提问时间
			String time = "";
			if(askerInfo.indexOf("发表于 ") > 0)
			{
				int index = askerInfo.indexOf("发表于 ")+"发表于 ".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</em>"));
				//去空
				time = time.trim();
				System.out.println("提问时间："+time);
			}
			CarQuestion cq = new CarQuestion();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
			if(askerInfo.indexOf("<div class=\"mtn\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"mtn\">")+"<div class=\"mtn\">".length();
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
			
			//其他回答
			if(result.indexOf("<div id=\"relate_subject\">") > 0 && result.indexOf("<div id=\"postlistreply\" class=\"pl\">") > 0)
			{
				int index = result.indexOf("<div id=\"relate_subject\">");
				int end = result.indexOf("<div id=\"postlistreply\" class=\"pl\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("\">发表于 ");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					//此评论被禁止或删除 内容自动屏蔽
					if(askInfo.indexOf("<div class=\"locked\">") > -1)
					{
						continue;
					}
					
					//其他回答回答问题的时间
					String singleAskTime = askInfo.substring(0,askInfo.indexOf("</em>"));
					singleAskTime = singleAskTime.trim();
					
					
					String singleAsk = askInfo.substring(askInfo.indexOf("<td class=\"t_f\""),askInfo.length());
					singleAsk = singleAsk.substring(singleAsk.indexOf("\">")+"\">".length(),singleAsk.length());
					singleAsk = singleAsk.substring(0,singleAsk.indexOf("</td></tr></table>"));
					singleAsk = singleAsk.trim();
					if(!singleAsk.endsWith("<br />") && singleAsk.indexOf("<br />") > 0)
					{
						singleAsk = singleAsk.substring(singleAsk.lastIndexOf("<br />")+"<br />".length(),singleAsk.length());
					}
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//去空
					singleAsk = singleAsk.trim();
					if(result.indexOf("最佳答案") > -1)
					{
						asktime = singleAskTime;
					}
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
				if(!bestAsk.equals("") && result.indexOf("最佳答案") > -1)
				{
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
				
			}
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
	}
	
	
}
