package com;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/mongo-config.xml");
		MongoTemplate mongoTemplate = (MongoTemplate)ctx.getBean("mongoTemplate");

		Map<String,SpiderData> map = new HashMap<String,SpiderData>();
		//深度为0
		String key = "朝鲜酒店";
		//list里面存储的是的关键字深度为1
		List<String> list = getKeywordList(key);
		for(String str : list)
		{
			SpiderData se = new SpiderData();
			se.setKey(str);
			se.setValue(1);
			map.put(str, se);
		}
		
		if(null != list && list.size() > 0)
		{
			for(String str : list)
			{
				Thread.sleep(1000*60*2);
				List<String> listStr = getKeywordList(str);
				if(null != listStr && listStr.size() > 0)
				{
					for(String res : listStr)
					{
						if(null == map.get(res))
						{
							SpiderData se = new SpiderData();
							se.setKey(res);
							se.setValue(2);
							map.put(res, se);
						}
						else
						{
							System.out.println("res="+res);
						}
					}
				}
			}
		}
		int count = 0;
		for(Map.Entry<String, SpiderData> entity : map.entrySet())
		{
			String keyValue = entity.getKey();
			SpiderData sd = entity.getValue();
			Query query = new Query();
			query.addCriteria(Criteria.where("keyword").is(keyValue));
			List<SpiderData> lsd = mongoTemplate.find(query, SpiderData.class);
			if(lsd.size() == 0)
			{
				mongoTemplate.insert(sd);
				count++;
				logger.info("插入SpiderData表第"+count+"数据");
			}
		}
	}
	
	public static List<String> getKeywordList(String key) throws Exception {    
		URL url = new URL("http://cas.baidu.com/?action=login");
		HttpURLConnection connection = null;
		connection = (HttpURLConnection) url.openConnection();// 建立链接

		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
		connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		String headerName = "";
		for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
			
			System.out.println(headerName);
		}
		if(true)
		{
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		HttpClient client = new HttpClient();
		String urlAddr = "http://fengchao.baidu.com/nirvana/request.ajax?path=nirvana/GET/kr/word&reqid=" + System.currentTimeMillis() + "_" + new Random().nextInt();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eventId", "");
		params.put("params", "{\"logid\":112588392,\"planid\":0,\"unitid\":0,\"entry\":\"kr_tools\",\"query\":\"" + key + "\",\"regions\":\"\",\"rgfilter\":1,\"device\":0,\"querytype\":1,\"querySessions\":[\"" + key + "\"]}");
		params.put("path", "nirvana/GET/kr/word");
		//params.put("token", "0974de98b2be23f9028c631323cebebfb47dd3f86a4afd3d344301e996a77ad21d14f3a6ef380d5ddc2aa3a7");
		//params.put("token", "803d6f8ab78eabfe05f890a311b8fd3b52abf0abdaf5a5e5972e585e269ee7e55ad6d6cd8cca6a9d65d74fa2");
		params.put("token", "5153208f83728a0852722b35af0d59d86f685d3f73f265b1fb6d8bc154d23819887d499e8f1d43d225d369fa");
		params.put("userid", "3749275");
		Map<String, Object> headers = new HashMap<String, Object>();
		//headers.put("Cookie", "BAIDUID=23C1CB80C32FB9491F4FD6B647A5CA5B:FG=1; BAIDUPSID=23C1CB80C32FB9491F4FD6B647A5CA5B; __cas__st__3=0974de98b2be23f9028c631323cebebfb47dd3f86a4afd3d344301e996a77ad21d14f3a6ef380d5ddc2aa3a7; __cas__id__3=3749275; __cas__rn__=172343278; SIGNIN_UC=70a2711cf1d3d9b1a82d2f87d633bd8a01723432788; uc_login_unique=25a32633041505b1d16e0f0af1699c5b; SAMPLING_USER_ID=3749275; SFSSID=b819e641f3bca088ee4807f41c0ec7f4");
		//headers.put("Cookie", "BAIDUID=FEECC7A603FAE4856CD0B2A44D5C7AD7:FG=1; BAIDUPSID=517DB7E274DB57B5A90B96AEDE8532CE; H_PS_PSSID=11338_11077_1438_11156_10488_11394_11399_11277_11241_11281_11151_11243_11404_10618; __cas__st__3=803d6f8ab78eabfe05f890a311b8fd3b52abf0abdaf5a5e5972e585e269ee7e55ad6d6cd8cca6a9d65d74fa2; __cas__id__3=3749275; __cas__rn__=172856730; SIGNIN_UC=70a2711cf1d3d9b1a82d2f87d633bd8a01728567300; uc_login_unique=1238d9be03f396fba579c29bdbadf7da; SAMPLING_USER_ID=3749275");
		//headers.put("Cookie", "BAIDUID=698871B985476C8C97745BAD79C681E5:FG=1; BAIDUPSID=A7E080C09C5D78F081086BA1447053C4; H_PS_PSSID=11328_1433_11362_10774_11226_11396_11399_11277_11240_11280_11151_11242_11404_10619_9149; __cas__st__3=b44a877c39cf3a0e5b79d19ee4ac36a8038a631a2efafe8ef27a82ae5ed1ec01da15518d953c4feb8321af17; __cas__id__3=3749275; __cas__rn__=172940954; SIGNIN_UC=70a2711cf1d3d9b1a82d2f87d633bd8a01729409544; uc_login_unique=04e2da4d06821e261feb93dd956611ca; SAMPLING_USER_ID=3749275");
		headers.put("Cookie", "BAIDUID=CCD30A6F4002784D1433F22723F2884B:FG=1; BAIDUPSID=CCD30A6F4002784D1433F22723F2884B; uc_login_unique=ac807886b3ac4f0b6130be54c26122a4; SIGNIN_UC=70a2711cf1d3d9b1a82d2f87d633bd8a01730296822; __cas__st__3=02702262a15118d768380c3e7ec176d0b86ad29064492925662b19fa8c8676ebbae646f5142e90ec13da6da2; __cas__id__3=3749275; __cas__rn__=173029682; SAMPLING_USER_ID=3749275");
		String response = client.post(urlAddr, params, headers);
		//去掉结果集中扩展关键字
		/*if(response.indexOf("grouprsn") != -1)
		{
			response = response.substring(0,response.indexOf("grouprsn"));
		}*/
		String[] strs = response.split("\"word\":\"");
		for(String str : strs)
		{
			if(str.indexOf("\",\"attr_index\"") != -1)
			{
				int length = str.indexOf("\",\"attr_index\"");
				String res = str.substring(0,length);
				list.add(res);
				System.out.println(res);
			}
		}
		logger.info(response);
		return list;
	}

}
