package com;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Test {
	private static String url = "http://zhidao.autohome.com.cn/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
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
		}
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		String result = content.toString();
		
		//主页有用链接信息
		String homeInfo = "";
		if(result.indexOf("<dl class=\"qa-con-fenl\">") > 0 && result.indexOf("<div class=\"qa-con-box fn-right\">") > 0)
		{
			int index = result.indexOf("<dl class=\"qa-con-fenl\">");
			int end = result.indexOf("<div class=\"qa-con-box fn-right\">");
			homeInfo = result.substring(index,end);
			
			
			String[] hinfo = homeInfo.split("<a href=\"");
			if(hinfo.length > 0)
			{
				String hrefInfo = hinfo[0];
				String[] hrefs = hrefInfo.split("\"");
				String href = hrefs[0];
				System.out.println(href);
			}
		} 
		
		
	}

}
