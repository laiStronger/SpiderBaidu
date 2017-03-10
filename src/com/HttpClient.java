package com;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpClient {
	
	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

	private HttpURLConnection conn = null;

	public String get(String urlAddr, Map<String, Object> paramMap) throws Exception {
		String content = "";
		StringBuffer params = new StringBuffer();
		Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> element = (Map.Entry<String, Object>) it
					.next();
			params.append((String) element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}

		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}
		try {
			URL url = new URL(urlAddr + "?" + params.toString());
			this.conn = ((HttpURLConnection) url.openConnection());

			this.conn.setDoOutput(false);
			this.conn.setRequestMethod("GET");
			this.conn.setUseCaches(false);
			this.conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			this.conn.setRequestProperty("Content-Length", "0");
			this.conn.setDoInput(true);
			this.conn.connect();

			int code = this.conn.getResponseCode();
			if (code != 200) {
				log.info("ERROR===" + code);
			} else {
				log.info("Success!");
				InputStream in = this.conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null)
					content = content + line + "\r\n";
			}
		} catch (Exception e) {
			log.error("post error.", e);
		} finally {
			this.conn.disconnect();
		}
		return content;
	}

	public String post(String urlAddr, Map<String, Object> paramMap)
			throws Exception {
		String content = "";
		StringBuffer params = new StringBuffer();
		Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> element = (Map.Entry<String, Object>) it
					.next();
			params.append((String) element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}

		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}
		try {
			URL url = new URL(urlAddr);
			this.conn = ((HttpURLConnection) url.openConnection());

			this.conn.setDoOutput(true);
			this.conn.setRequestMethod("POST");
			this.conn.setUseCaches(false);
			this.conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			this.conn.setRequestProperty("Content-Length",
					String.valueOf(params.length()));
			this.conn.setDoInput(true);
			this.conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					this.conn.getOutputStream(), "UTF-8");
			out.write(params.toString());
			out.flush();
			out.close();

			int code = this.conn.getResponseCode();
			if (code != 200) {
				log.info("ERROR===" + code);
				InputStream in = this.conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null)
					content = content + line + "\r\n";
			} else {
				log.info("Success!");
				InputStream in = this.conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null)
					content = content + line + "\r\n";
			}
		} catch (Exception e) {
			log.error("post error.", e);
		} finally {
			this.conn.disconnect();
		}
		return content;
	}
	
	public String post(String urlAddr, Map<String, Object> paramMap, Map<String, Object> headerMap)
			throws Exception {
		String content = "";
		StringBuffer params = new StringBuffer();
		Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> element = (Map.Entry<String, Object>) it
					.next();
			params.append((String) element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}

		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}
		try {
			URL url = new URL(urlAddr);
			this.conn = ((HttpURLConnection) url.openConnection());

			this.conn.setDoOutput(true);
			this.conn.setRequestMethod("POST");
			this.conn.setUseCaches(false);
			this.conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			this.conn.setRequestProperty("Content-Length",
					String.valueOf(params.length()));
			Iterator<Map.Entry<String, Object>> iter = headerMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object> element = (Map.Entry<String, Object>) iter
						.next();
				this.conn.setRequestProperty(element.getKey(),
						(String)element.getValue());
			}
			this.conn.setDoInput(true);
			this.conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					this.conn.getOutputStream(), "UTF-8");
			out.write(params.toString());
			out.flush();
			out.close();

			int code = this.conn.getResponseCode();
			if (code != 200) {
				log.info("ERROR===" + code);
				InputStream in = this.conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null)
					content = content + line + "\r\n";
			} else {
				log.info("Success!");
				InputStream in = this.conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null)
					content = content + line + "\r\n";
			}
		} catch (Exception e) {
			log.error("post error.", e);
		} finally {
			this.conn.disconnect();
		}
		return content;
	}

	public String post(String urlAddr, String json) throws Exception {
		String content = "";
		try {
			URL url = new URL(urlAddr);
			this.conn = ((HttpURLConnection) url.openConnection());

			this.conn.setDoOutput(true);
			this.conn.setRequestMethod("POST");
			this.conn.setUseCaches(false);
			this.conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			this.conn.setRequestProperty("Content-Length",
					String.valueOf(json.length()));
			this.conn.setDoInput(true);
			this.conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					this.conn.getOutputStream(), "UTF-8");
			out.write(json);
			out.flush();
			out.close();

			int code = this.conn.getResponseCode();
			if (code != 200) {
				log.info("ERROR===" + code);
			} else {
				log.info("Success!");
				InputStream in = this.conn.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, "UTF-8"));
				String line = "";
				while ((line = br.readLine()) != null)
					content = content + line + "\r\n";
			}
		} catch (Exception e) {
			log.error("post error.", e);
		} finally {
			this.conn.disconnect();
		}
		return content;
	}

	public static void main(String[] args) throws Exception {
		HttpClient client = new HttpClient();
		String urlAddr = "http://api.weibo.com/2/proxy/badges/issue.json";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", "2067112424");
		params.put("uids", "2365944120");
		params.put("badge_id", "sAFStotI");
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization:", ":OAuth2 2.00XvY5xCy35tPCce51a8fd4909nTEN");
		headers.put("API-RemoteIP", "115.182.70.160");
		headers.put("appkey", "2067112424");
		headers.put("cuid", "2711966011");
		String response = client.post(urlAddr, params, headers);
		System.out.println(response);
	}
}