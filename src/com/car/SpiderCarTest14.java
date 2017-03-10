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
 * �����١����ûش�
 * ץȡhttp://wenda.cheduoshao.com/hwdList_page1/
 * @author dzm
 * ����վ�����ⲹ��,��ѻش�ʱ�䣬��ѻش�
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
	 * ��ȡ��ҳ���з����href����
	 */
	public static void getHomeHrefInfo(MongoTemplate mongo)
	{
		//initMongo();
		mongoTemplate = mongo;
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
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
			
			//��ҳ����������Ϣ
			String homeInfo = "";
			if(result.indexOf("ȫ�����ʴ�") > 0 && result.indexOf("�鿴ȫ���ѽ������ >") > 0)
			{
				int index = result.indexOf("ȫ�����ʴ�");
				int end = result.indexOf("�鿴ȫ���ѽ������ >");
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
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getCategoryPageInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
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
			
			//��ȡ��������ҳ��
			int pages = 0;
			String urlInfo = "";
			if(result.indexOf("<div class=\"c_page clearfix\">") > 0 && result.indexOf("��һҳ") > 0)
			{
				int index = result.indexOf("<div class=\"c_page clearfix\">");
				int end = result.indexOf("��һҳ");
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
	 * ��ȡ���������������href����
	 * @param url
	 */
	public static void getCategoryHrefInfo(String url)
	{
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
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
			
			//��ҳ����������Ϣ
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
			// �½�URL����
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
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class=\"blank11\"></div>") > 0 && result.indexOf("<div class=\"blank15\"></div>") > 0)
			{
				int index = result.indexOf("<div class=\"blank11\"></div>");
				int end = result.indexOf("<div class=\"blank15\"></div>");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<span class=\"c_000000\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"c_000000\">")+"<span class=\"c_000000\">".length();
				//int end = askerInfo.indexOf("<div class=\"user-box\">");
				title = askerInfo.substring(index,askerInfo.length());
				title = title.substring(0,title.indexOf("</span>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			//String description = "";
		
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<span class=\"c_000000\">") > 0 && askerInfo.indexOf("<li class=\"list_head_li c_size12 c_666666\">") > 0)
			{
				int index = askerInfo.indexOf("<span class=\"c_000000\">");
				int end = askerInfo.indexOf("<li class=\"list_head_li c_size12 c_666666\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("<li class=\"c_size12 c_aaaaaa\">")+"<li class=\"c_size12 c_aaaaaa\">".length(),time.indexOf("&nbsp;&nbsp;"));
				//ȥ��
				time = time.trim();
				System.out.println("����ʱ�䣺"+time);
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
				cq.setKeyword("����");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"��վ����ץȡ����������error"+e);
			}
			
			//�����ش�
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
					
					
					//�����ش�ش������ʱ��
					String singleAskTime = askInfo.substring(askInfo.indexOf("<span class=\"f_l\">")+"<span class=\"f_l\">".length(),askInfo.length());
					singleAskTime = singleAskTime.substring(0,singleAskTime.indexOf("&nbsp;&nbsp;"));
					singleAskTime = singleAskTime.trim();
					
					
					String singleAsk = askInfo.substring(0,askInfo.indexOf("</span>"));;
					
					singleAsk = CarFormat.Stringformat(singleAsk);
					singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
					
					//ȥ��
					singleAsk = singleAsk.trim();
					
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
			}
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
	}
}
