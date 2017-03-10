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
 * ������������������
 * ץȡhttp://www.12365auto.com/zjdy/index_1_1.shtml
 * @author dzm
 *  ����վ��ʱû���ҵ����ѻش�(�����ش�)
 */
public class SpiderCarTest9 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest3.class);
	private static MongoTemplate mongoTemplate;
	//private static String homeUrl = "http://www.12365auto.com/zjdy/index_1.shtml";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://www.12365auto.com/zjdy/2010-05-12/20100512093800.shtml");
		//getCategoryPageInfo();
	}

	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		System.out.println("SpiderCarTest9");
	}
	
	/***
	 * ��ȡ�����з�ҳ��href����
	 * @param url
	 */
	public static void getCategoryPageInfo(MongoTemplate mongo)
	{
		//initMongo();
		mongoTemplate = mongo;
		StringBuffer content = new StringBuffer();
		try {
			// �½�URL����
			URL u = new URL("http://www.12365auto.com/zjdy/index_1.shtml");
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, "gb2312");
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
			if(result.indexOf("<div class='p_page'>") > 0 && result.indexOf("<div class=\"news_hy_right\">") > 0)
			{
				int index = result.indexOf("<div class='p_page'>");
				int end = result.indexOf("<div class=\"news_hy_right\">");
				String pageInfo = result.substring(index,end);
				pageInfo = pageInfo.substring(pageInfo.lastIndexOf("<a href='")+"<a href='".length(),pageInfo.lastIndexOf(".shtml"));
				
				
				String page = pageInfo.substring(pageInfo.lastIndexOf("_")+1,pageInfo.length());
				pages = Integer.parseInt(page);
				urlInfo = pageInfo.substring(0,pageInfo.lastIndexOf("_"));
				
				//System.out.println(pages+" "+urlInfo);
			}
			
			
			for(int page = 1; page <= pages; page++)
			{
				String urls = "http://www.12365auto.com"+urlInfo+"_"+page+".shtml";
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
			InputStreamReader theHTML = new InputStreamReader(in, "gb2312");
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
			in.close();
			theHTML.close();
			
			String result = content.toString();
			
			//��ҳ����������Ϣ
			String cateInfo = "";
			if(result.indexOf("<div class='zn_l_c ans_b'>") > 0 && result.indexOf("<div class='p_page'>") > 0)
			{
				int index = result.indexOf("<div class='zn_l_c ans_b'>");
				int end = result.indexOf("<div class='p_page'>");
				cateInfo = result.substring(index,end);
				
				String[] hinfo = cateInfo.split("</span><a href='");
				if(hinfo.length > 0)
				{
					for(int i = 0; i < hinfo.length; i++)
					{
						if(i == 0)
						{
							continue;
						}
						String hrefInfo = hinfo[i];
						String href = hrefInfo.substring(0,hrefInfo.indexOf("' target='_blank'>"));
						href = "http://www.12365auto.com/zjdy/"+href;
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
			//��ȡ�����߻�����Ϣ
			String askerInfo = "";
			if(result.indexOf("<div class=\"pjnr\">") > 0 && result.indexOf("<form name=\"aspnetForm\"") > 0)
			{
				int index = result.indexOf("<div class=\"pjnr\">")-"<div class=\"pjnr\">".length();
				int end = result.indexOf("<form name=\"aspnetForm\"");
				askerInfo = result.substring(index,end);
			}
			//�������
			String title = "";
			if(askerInfo.indexOf("<div class=\"pjnr\">") > 0 && askerInfo.indexOf("<div class=\"show\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"pjnr\">")+"<div class=\"pjnr\">".length();
				int end = askerInfo.indexOf("<div class=\"show\">");
				title = askerInfo.substring(index,end);
				title = title.substring(title.indexOf("<h1>")+"<h1>".length(),title.indexOf("</h1>"));
				//ȥ��
				title = title.trim();
				System.out.println("������⣺"+title);
			}
			//���ⲹ��
			String description = "";
			if(askerInfo.indexOf("<div class=\"pjnr\">") > 0 && askerInfo.indexOf("<div class=\"tshf\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"pjnr\">");
				int end = askerInfo.indexOf("<div class=\"tshf\">");
				description = askerInfo.substring(index,end);
				description = description.substring(description.indexOf("<p>")+"<p>".length(),description.indexOf("</p>"));
				
				description = CarFormat.Stringformat(description);
				description = description.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				description = description.trim();
				System.out.println("���ⲹ�䣺"+description);
			}
			
			//��������ʱ��
			String time = "";
			if(askerInfo.indexOf("<div class=\"pjnr\">") > 0 && askerInfo.indexOf("<div class=\"tshf\">") > 0)
			{
				int index = askerInfo.indexOf("<div class=\"pjnr\">");
				int end = askerInfo.indexOf("<div class=\"tshf\">");
				time = askerInfo.substring(index,end);
				time = time.substring(time.indexOf("��������ʱ�䣺")+"��������ʱ�䣺".length(),time.lastIndexOf("</p>"));
				//ȥ��
				time = time.trim();
				System.out.println("����ʱ�䣺"+time);
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
				cq.setKeyword("����");
				mongoTemplate.insert(cq);
			}
			catch(Exception e)
			{
				logger.error(url+"��վ����ץȡ����������error"+e);
			}
			//��Ѵ�
			String bestAsk = "";
			if(result.indexOf("<div class=\"tshf\">") > 0 && result.indexOf("<div id=\"ckepop\"") > 0)
			{
				int index = result.indexOf("<div class=\"tshf\">");
				int end = result.indexOf("<div id=\"ckepop\"");
				bestAsk = result.substring(index,end);
				bestAsk = bestAsk.substring(bestAsk.indexOf("<p>")+"<p>".length(),bestAsk.indexOf("</p>"));
				
				bestAsk = CarFormat.Stringformat(bestAsk);
				bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				bestAsk = bestAsk.trim();
				//System.out.println(bestAsk);
			}
			
			//��Ѵ𰸻ش�ʱ��
			String asktime = "";
			if(result.indexOf("<div class=\"tshf\">") > 0 && result.indexOf("<div id=\"ckepop\"") > 0)
			{
				int index = result.indexOf("<div class=\"tshf\">");
				int end = result.indexOf("<div id=\"ckepop\"");
				asktime = result.substring(index,end);
				asktime = asktime.substring(asktime.indexOf("ר�Ҵ�ʱ�䣺")+"ר�Ҵ�ʱ�䣺".length(),asktime.lastIndexOf("</p>"));
				//ȥ��
				asktime = asktime.trim();
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
			
			//�����ش�
			if(result.indexOf("<div class=\"rev_more\" id=\"MoreCommentContain\">") > 0 && result.indexOf("<form name=\"aspnetForm\"") > 0)
			{
				int index = result.indexOf("<div class=\"rev_more\" id=\"MoreCommentContain\">");
				int end = result.indexOf("<form name=\"aspnetForm\"");
				
				String otherAsk = result.substring(index,end);
				
				otherAsk = otherAsk.substring(otherAsk.indexOf("<span id=\"sp_commentcount2\">")+"<span id=\"sp_commentcount2\">".length(),otherAsk.indexOf("</span> ������"));
				System.out.println("�����ܹ���"+otherAsk.trim()+"���ش�");
				int pages = Integer.parseInt(otherAsk.trim());
				if(pages > 0)
				{
					System.out.println(pages);
				}
				try
				{
					CarAnswer ca = new CarAnswer();
					ca.setQuestionId(cq.getId());
					ca.setContent(otherAsk);
					ca.setSource(url);
					/*if(!"".equals(singleAskTime))
					{
						ca.setAskTime(sdf.parse(singleAskTime));
					}*/
					ca.setCreateTime(new Date());
					ca.setBestAnswer("0");
					//mongoTemplate.insert(ca);
				}
				catch(Exception e)
				{
					logger.error(url+"��վ����ץȡ������������error"+e);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("error:"+url);
		}
		
	}
}
