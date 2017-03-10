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
 * ���س��ѻᡪ��ר�ҽ��
 * ץȡhttp://www.iford.cn/plugin.php?id=hux_zhidao:hux_zhidao
 * @author dzm
 * û�и㶨
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
	 * ��ȡ��ҳ���з����href����
	 */
	public static void getHomeHrefInfo(MongoTemplate mongo)
	{
		//initMongo();
		mongoTemplate = mongo;
		StringBuffer content = new StringBuffer();
		try {
			URL urlInfo = new URL(homeUrl);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfo.openConnection();// ��������

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
			
			//��ҳ����������Ϣ
			String homeInfo = "";
			if(result.indexOf("<li class=sortlist>") > 0 && result.indexOf("<strong>���ȵ�¼</strong>") > 0)
			{
				int index = result.indexOf("<li class=sortlist>");
				int end = result.indexOf("<strong>���ȵ�¼</strong>");
				homeInfo = result.substring(index,end);
				String[] homes = homeInfo.split("\">����</a></h2>");
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
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getCategoryPageInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL urlInfos = new URL(url);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfos.openConnection();// ��������

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
			
			//��ȡ��������ҳ��
			int pages = 0;
			if(result.indexOf("<span title=\"�� ") > 0 && result.indexOf(" ҳ\">") > 0)
			{
				int index = result.indexOf("<span title=\"�� ")+"<span title=\"�� ".length();
				int end = result.indexOf(" ҳ\">");
				String pageInfo = result.substring(index,end);
				
				String page = pageInfo.trim();
				pages = Integer.parseInt(page);
				
				//System.out.println(pages);
			}
			//��ȡ�����еķ�ҳ��������url
			String urlInfo = "";
			if(result.indexOf("<span title=\"��") > 0 && result.indexOf("\" class=\"nxt\">��һҳ</a></div>") > 0)
			{
				int index = result.indexOf("<span title=\"��");
				int end = result.indexOf("\" class=\"nxt\">��һҳ</a></div>");
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
	 * ��ȡ���������������href����
	 * @param url
	 */
	public static void getCategoryHrefInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL urlInfo = new URL(url);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfo.openConnection();// ��������

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
			
			//��ҳ����������Ϣ
			String cateInfo = "";
			if(result.indexOf("�����б�</a></h2>") > 0 && result.indexOf("<div class=\"pg\">") > 0)
			{
				int index = result.indexOf("�����б�</a></h2>");
				int end = result.indexOf("<div class=\"pg\">");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("�����д���");
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
		System.out.println("URL���ӣ�"+url);
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL urlInfo = new URL(url);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) urlInfo.openConnection();// ��������

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
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<td class=\"plc vwthd\">") > 0 && result.indexOf("<div class=\"pgs mtm mbm cl\">") > 0)
			{
				int index = result.indexOf("<td class=\"plc vwthd\">");
				int end = result.indexOf("<div class=\"pgs mtm mbm cl\">");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<span id=\"thread_subject\">") > 0)
			{
				int index = askerInfo.indexOf("<span id=\"thread_subject\">")+"<span id=\"thread_subject\">".length();
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</span>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("<div class=\"rwdn\">") > 0 && askerInfo.indexOf("<div id=\"p_btn\" class=\"mtw mbm hm cl\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"rwdn\">");
				int end = askerInfo.indexOf("<div id=\"p_btn\" class=\"mtw mbm hm cl\">");
				description = askerInfo.substring(index,end);
				description = description.substring(0,description.indexOf("</table>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				description = description.trim();
				System.out.println("���ⲹ�䣺"+description);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("������ ") > 0)
			{
				int index = askerInfo.indexOf("������ ")+"������ ".length();
				time = askerInfo.substring(index,askerInfo.length());
				time = time.substring(0,time.indexOf("</em>"));
				//ȥ��
				time = time.trim();
				System.out.println("����ʱ�䣺"+time);
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
				cq.setKeyword("����");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"��վ����ץȡ����������error"+e);
			}
			//��Ѵ�
			String bestAsk = "";
			if(askerInfo.indexOf("<div class=\"mtn\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"mtn\">")+"<div class=\"mtn\">".length();
				bestAsk = askerInfo.substring(index,askerInfo.length());
				bestAsk = bestAsk.substring(0,bestAsk.indexOf("</div>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
				
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			
			//�����ش�
			if(result.indexOf("<div id=\"relate_subject\">") > 0 && result.indexOf("<div id=\"postlistreply\" class=\"pl\">") > 0)
			{
				int index = result.indexOf("<div id=\"relate_subject\">");
				int end = result.indexOf("<div id=\"postlistreply\" class=\"pl\">");
				
				String otherAsk = result.substring(index,end);
				
				String[] asks = otherAsk.split("\">������ ");
				for(int i = 0; i < asks.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String askInfo = asks[i];
					//�����۱���ֹ��ɾ�� �����Զ�����
					if(askInfo.indexOf("<div class=\"locked\">") > -1)
					{
						continue;
					}
					
					//�����ش�ش������ʱ��
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
					
					//ȥ��
					singleAsk = singleAsk.trim();
					if(result.indexOf("��Ѵ�") > -1)
					{
						asktime = singleAskTime;
					}
					System.out.println("�����ش�"+singleAskTime+"-----"+singleAsk);
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
						logger.error(url+"��վ����ץȡ������������error"+e);
					}
				}
				if(!bestAsk.equals("") && result.indexOf("��Ѵ�") > -1)
				{
					System.out.println("��ѻش�"+asktime+"-----"+bestAsk);
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
						logger.error(url+"��վ����ץȡ��������Ѵ�error"+e);
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
