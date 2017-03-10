package com.car;

public class CarFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * ¸ñÊ½×ª»»
	 * @return
	 */
	public static String Stringformat(String content) {
		if(content.indexOf("<br>") > -1)
		{
			content = content.replaceAll("<br>", "\n");
		}
		if(content.indexOf("<br />") > -1)
		{
			content = content.replaceAll("<br />", "\n");
		}
		if(content.indexOf("<p>") > -1)
		{
			content = content.replaceAll("<p>", "\n");
		}
		if(content.indexOf("</p>") > -1)
		{
			content = content.replaceAll("</p>", "");
		}
		/*if(content.indexOf("<span>") > -1)
		{
			content = content.replaceAll("<span>", "\n");
		}
		if(content.indexOf("</span>") > -1)
		{
			content = content.replaceAll("</span>", "");
		}*/
		if(content.indexOf("&nbsp;") > -1)
		{
			content = content.replaceAll("&nbsp;", " ");
		}
		return content;
	}

}
