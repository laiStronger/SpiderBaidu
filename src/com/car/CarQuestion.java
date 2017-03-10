package com.car;

import java.io.Serializable;
import java.util.Date;

/**
 * 汽车抓取 问题实体
 * @author dzm
 *
 */
public class CarQuestion implements Serializable {

	private static final long serialVersionUID = 4942136953872116301L;

	private String id;        //问题id

	private String title;    //标题
	
	private String content;  //内容
	
	private String keyword;  //关键字
	
	private String source;  //问题来源
	
	private Date askTime;  //提问时间
	
	private Date createTime;  //创建时间
	
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getAskTime() {
		return askTime;
	}

	public void setAskTime(Date askTime) {
		this.askTime = askTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
