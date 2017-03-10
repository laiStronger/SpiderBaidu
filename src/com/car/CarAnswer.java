package com.car;

import java.io.Serializable;
import java.util.Date;

/**
 * 汽车抓取 答案实体
 * @author dzm
 *
 */
public class CarAnswer implements Serializable {

	private static final long serialVersionUID = 8659294481599533447L;

	private String id;        //答案id
	
	private String questionId;  //问题id
	
	private String content;   //答案内容
	
	private Date askTime;  //提问时间
	
	private Date createTime;  //创建时间
	
	private String bestAnswer; //1为最佳答案,0为普通答案
	
	private String source;  //问题来源

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getAskTime() {
		return askTime;
	}

	public void setAskTime(Date askTime) {
		this.askTime = askTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBestAnswer() {
		return bestAnswer;
	}

	public void setBestAnswer(String bestAnswer) {
		this.bestAnswer = bestAnswer;
	}
	
	
}
