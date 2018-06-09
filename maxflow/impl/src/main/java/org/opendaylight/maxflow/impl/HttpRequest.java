/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.HttpURLConnection;

public class HttpRequest {
	private  String basicAuth = null;
	private HttpURLConnection conn ;
	public void setBasicAuth(String str) {
		this.basicAuth = "Basic "+str;
	}
	
	public String getBasicAuth() {
		return basicAuth;
	}
	
	/**
	 * 建立连接
	 */
	public void SetConnection(String url,String param) throws IOException {
		String urlNameString = url + "?" + param;
		URL realUrl = new URL(urlNameString);
		conn = (HttpURLConnection)realUrl.openConnection();
		conn.setRequestMethod("GET");
		if(basicAuth!=null) {
			conn.setRequestProperty("Authorization",
					basicAuth);
		}
		conn.setRequestProperty("User-Agent", 
				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
		conn.setRequestProperty("Accept", "application/json");
	}
	
	/**
	 * 向指定URL发送GET方法的请求
	 **/
	public String sendGet(String url,String param) {
		String result = "";
		try {
			String urlNameString = url ;
			URL realUrl = new URL(urlNameString);
			conn = (HttpURLConnection)realUrl.openConnection();
			if(basicAuth!=null) {
				conn.setRequestProperty("Authorization",
						basicAuth);
			}
			conn.setRequestProperty("user-agent","Mozilla/5.0");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("GET");
			InputStream info = (InputStream) conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(info));
			String line = "";
			while((line = in.readLine())!=null) {
				result += line;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * send post request
	 */
	public String sendPost(String url,String param,String charset) {
		String result = "";
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			String urlNameString = url ;
			URL realUrl = new URL(urlNameString);
			//open url connection
			conn = (HttpURLConnection)realUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "");
			//post connection set
			conn.setDoOutput(true);
			conn.setDoInput(true);
			//get output
			out = new PrintWriter(conn.getOutputStream());
			//send param
			out.print(param);
			//flush
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),charset));
			
			String line;
			while((line = in.readLine())!=null) {
				result += line;
			}
		}catch (Exception e) {
			System.out.println("Send POST request error!" + e);
			e.printStackTrace();
		}finally {
			//close input stream
			try {
				if(out!=null) {
					out.close();
				}
				if(in != null) {
					in.close();
				}
			}catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	/*
	 * send PUT request
	 */
	public String sendPut(String url,String param) {
		String result = "";
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			String urlNameString = url ;
			URL realUrl = new URL(urlNameString);
			conn = (HttpURLConnection)realUrl.openConnection();
			if(basicAuth!=null) {
				conn.setRequestProperty("Authorization",
						basicAuth);
			}
			conn.setRequestProperty("user-agent","Mozilla/5.0");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestMethod("PUT");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			//写入数据
			dos.writeBytes(param);
			dos.flush();
			dos.close();
			
			//code
			int code = conn.getResponseCode();
			System.out.println(code);
			if(code == 200) {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while((line = in.readLine())!=null) {
					result += line;
				}
			}
		}catch(Exception e){
			System.out.println("Send POST request error!" + e);
			e.printStackTrace();
		}finally {
			//close input stream
			try {
				if(out!=null) {
					out.close();
				}
				if(in != null) {
					in.close();
				}
			}catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
}
