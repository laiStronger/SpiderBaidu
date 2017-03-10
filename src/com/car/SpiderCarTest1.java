package com.car;

import java.io.BufferedInputStream;
import java.io.IOException;
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
 * ����֮�ҡ���֪��
 * ץȡhttp://zhidao.autohome.com.cn
 * @author dzm
 *
 */
public class SpiderCarTest1 {
	private static Logger logger = LoggerFactory.getLogger(SpiderCarTest1.class);
	private static MongoTemplate mongoTemplate;
	private static String homeUrl = "http://zhidao.autohome.com.cn/index.html";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
		//getCarData("http://zhidao.autohome.com.cn/question/38948932.html");
		//getHomeHrefInfo();
	}

	public static void initMongo()
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");
	}
	
	
	/***
	 * ��ȡ��ҳ���з����href����
	 */
	public static void getHomeHrefInfo(MongoTemplate template)
	{
		//initMongo();
		mongoTemplate = template;
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
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//��ҳ����������Ϣ
		String homeInfo = "";
		if(result.indexOf("<dl class=\"qa-con-fenl\">") > 0 && result.indexOf("<div class=\"qa-con-box fn-right\">") > 0)
		{
			int index = result.indexOf("<dl class=\"qa-con-fenl\">");
			int end = result.indexOf("<div class=\"qa-con-box fn-right\">");
			homeInfo = result.substring(index,end);
			
			String[] hinfo = homeInfo.split("<a href=\"");
			if(hinfo.length > 0)
			{
				for(int i = 0; i < hinfo.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String hrefInfo = hinfo[i];
					String[] hrefs = hrefInfo.split("\"");
					String href = hrefs[0];
					if(href.startsWith("/"))
					{
						href = "http://zhidao.autohome.com.cn"+href;
					}
					//System.out.println(i+"  "+href);
					
					//��ҳ��������
					getCategoryPageInfo(href);
					
				}
				
			}
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
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//��ȡ��������ҳ��
		int pages = 0;
		if(result.indexOf("<input type=\"text\" class=\"page-item-jump-text\"") > 0 && result.indexOf("<a id=\"jumpbtn\"") > 0)
		{
			int index = result.indexOf("<input type=\"text\" class=\"page-item-jump-text\"");
			int end = result.indexOf("<a id=\"jumpbtn\"");
			String pageInfo = result.substring(index,end);
			if(pageInfo.indexOf("/��") > 0)
			{
				String[] pageInfos = pageInfo.split("/��");
				String pageinfo = pageInfos[1];
				String[] ps = pageinfo.split("ҳ");
				String page = ps[0];
				pages = Integer.parseInt(page);
				System.out.println(pages);
			}
		}
		//��ȡ�����еķ�ҳ��������url
		String urlInfo = "";
		if(result.indexOf("<div class=\"qa-list-page\">") > 0 && result.indexOf("<span class=\"page-item\">...</span>") > 0)
		{
			int index = result.indexOf("<div class=\"qa-list-page\">");
			int end = result.indexOf("<span class=\"page-item\">...</span>");
			String pageInfo = result.substring(index,end);
			String[] hinfo = pageInfo.split("<a href=\"");
			if(hinfo.length > 0)
			{
				String hrefInfo = hinfo[1];
				String[] hrefs = hrefInfo.split(".html");
				urlInfo = hrefs[0];
				urlInfo= urlInfo.substring(0,urlInfo.lastIndexOf("-"));
				//System.out.println(urlInfo);
			}
		}
		for(int page = 1; page <= pages; page++)
		{
			String urls = "http://zhidao.autohome.com.cn"+urlInfo+"-"+page+".html";
			//System.out.println(urls);
			getCategoryHrefInfo(urls);
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
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();

		/*String res = result.substring(result.indexOf("<input type=\"text\" class=\"page-item-jump-text\""),result.indexOf("<a id=\"jumpbtn\""));
		System.out.println(res);*/
		
		//��ҳ����������Ϣ
		String cateInfo = "";
		if(result.indexOf("<ul class=\"qa-list-con\">") > 0 && result.indexOf("<div class=\"qa-list-page\">") > 0)
		{
			int index = result.indexOf("<ul class=\"qa-list-con\">");
			int end = result.indexOf("<div class=\"qa-list-page\">");
			cateInfo = result.substring(index,end);
			
			String[] hinfo = cateInfo.split("<a target=\"_blank\" href=\"");
			if(hinfo.length > 0)
			{
				for(int i = 0; i < hinfo.length; i++)
				{
					if(i == 0)
					{
						continue;
					}
					String hrefInfo = hinfo[i];
					String[] hrefs = hrefInfo.split("\">");
					String href = hrefs[0];
					if(href.startsWith("/"))
					{
						href = "http://zhidao.autohome.com.cn"+href;
					}
					//System.out.println(i+"  "+href);
					
					getCarData(href);
				}
				
			}
		} 
	}

	public static void getCarData(String url)
	{
		System.out.println("url���ӣ�"+url);
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
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//��ȡ�����߻�����Ϣ
		String askerInfo = "";
		if(result.indexOf("<div class=\"qa-uibox qa-uibox-bor01\">") > 0 && result.indexOf("<div class=\"qa-dmbox-bom\">") > 0)
		{
			int index = result.indexOf("<div class=\"qa-uibox qa-uibox-bor01\">");
			int end = result.indexOf("<div class=\"qa-dmbox-bom\">");
			askerInfo = result.substring(index,end);
		}
		//�������
		String title = "";
		if(askerInfo.indexOf("<h1 class=\"qa-dm-title\">") > 0 && askerInfo.indexOf("<a href=\"javascript:void(0);\" id=\"cllectq\"") > 0)
		{
			int index = askerInfo.indexOf("<h1 class=\"qa-dm-title\">")+"<h1 class=\"qa-dm-title\">".length();
			int end = askerInfo.indexOf("<a href=\"javascript:void(0);\" id=\"cllectq\"");
			title = askerInfo.substring(index,end);
			//ȥ��
			title = title.trim();
			title = title.substring(0,title.length()-"</h1>".length());
			System.out.println("������⣺"+title);
		}
		//���ⲹ��
		String description = "";
		if(askerInfo.indexOf("<div class=\"qa-dm-chara02\">") > 0)
		{
			int index = askerInfo.indexOf("<div class=\"qa-dm-chara02\">")+"<div class=\"qa-dm-chara02\">".length();
			description = askerInfo.substring(index,askerInfo.length());
			String[] des = description.split("</div>");
			if(des.length > 0)
			{
				description = des[0];
			}
			description = CarFormat.Stringformat(description);
			description = description.replaceAll("</?[^>]+>", "");
			
			//ȥ��
			description = description.trim();
			System.out.println("���ⲹ�䣺"+description);
		}
		
		//��������ʱ��
		String time = "";
		if(askerInfo.indexOf("<span class=\"fn-right\">") > 0)
		{
			int index = askerInfo.indexOf("<span class=\"fn-right\">")+"<span class=\"fn-right\">".length();
			time = askerInfo.substring(index,askerInfo.length());
			String[] des = time.split("</span>");
			if(des.length > 0)
			{
				time = des[0];
			}
			//ȥ��
			time = time.trim();
			System.out.println("����ʱ�䣺"+time);
		}
		CarQuestion cq = new CarQuestion();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
		if(result.indexOf("<div class=\"qa-dm-chara05\">") > 0 && result.indexOf("���������ﵽ���������") > 0)
		{
			int index = result.indexOf("<div class=\"qa-dm-chara05\">");
			int end = result.indexOf("���������ﵽ���������");
			bestAsk = result.substring(index,end);
			String[] bsa = bestAsk.split("</div>");
			if(bsa.length > 0)
			{
				bestAsk = bsa[0];
			}
			bestAsk = CarFormat.Stringformat(bestAsk);
			bestAsk = bestAsk.replaceAll("</?[^>]+>", "");
			//ȥ��
			bestAsk = bestAsk.trim();
			//System.out.println(bestAsk);
		}
		
		//��Ѵ𰸻ش�ʱ��
		String asktime = "";
		if(result.indexOf("<div class=\"qa-dm-area02\">") > 0 && result.indexOf("���������ﵽ���������") > 0)
		{
			int index = result.indexOf("<div class=\"qa-dm-area02\">");
			int end = result.indexOf("���������ﵽ���������");
			asktime = result.substring(index,end);
			int tlen = asktime.indexOf("<span class=\"fn-right\">")+"<span class=\"fn-right\">".length();
			asktime = asktime.substring(tlen,asktime.length());
			String[] des = asktime.split("</span>");
			if(des.length > 0)
			{
				asktime = des[0];
			}
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
		if(result.indexOf("<ul class=\"qa-dm-num-list\">") > 0 && result.indexOf("<div class=\"qa-uibox qa-uibox-margin01\">") > 0)
		{
			int index = result.indexOf("<ul class=\"qa-dm-num-list\">");
			int end = result.indexOf("<div class=\"qa-uibox qa-uibox-margin01\">");
			
			String otherAsk = result.substring(index,end);
			
			String[] asks = otherAsk.split("<span class=\"fn-right\">");
			for(int i = 0; i < asks.length; i++)
			{
				if(i == 0)
				{
					continue;
				}
				String askInfo = asks[i];
				String[] aintimes = askInfo.split("</span>");
				//�����ش�ش������ʱ��
				String singleAskTime = "";
				if(aintimes.length > 0)
				{
					singleAskTime = aintimes[0];
				}
				
				String[] ains = askInfo.split("<div class=\"qa-dm-chara02\">");
				String singleAsk = "";
				if(ains.length > 0)
				{
					singleAsk = ains[1];
					if(singleAsk.indexOf("</blockquote></div>") > -1)
					{
						singleAsk = singleAsk.substring(singleAsk.indexOf("</blockquote></div>")+"</blockquote></div>".length(),singleAsk.length());
					}
					if(singleAsk.indexOf("</BLOCKQUOTE></DIV>") > -1)
					{
						singleAsk = singleAsk.substring(singleAsk.indexOf("</BLOCKQUOTE></DIV>")+"</BLOCKQUOTE></DIV>".length(),singleAsk.length());
					}
					if(singleAsk.indexOf("<div class=\"yy_reply_cont\">") > -1)
					{
						singleAsk = singleAsk.substring(singleAsk.indexOf("<div class=\"yy_reply_cont\">")+"<div class=\"yy_reply_cont\">".length(),singleAsk.length());
					}
					singleAsk = singleAsk.substring(0,singleAsk.indexOf("</div>"));
				}
				singleAsk = CarFormat.Stringformat(singleAsk);
				singleAsk = singleAsk.replaceAll("</?[^>]+>", "");
				
				//ȥ��
				singleAsk = singleAsk.trim();
				System.out.println("�����ش�"+i+" "+singleAskTime+"-----"+singleAsk);
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
}
