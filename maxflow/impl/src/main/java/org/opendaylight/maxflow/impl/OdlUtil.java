/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import java.util.Base64;
import org.opendaylight.maxflow.impl.HttpRequest;
public class OdlUtil {
	private String url = "";
	HttpRequest http = new HttpRequest();
	int meter_id = 1;
	int flow_id = 1;
	int bandrate = 0;
	String node = "";
	String output = "";
	String input = "";
	String meter_info = "1";	
	String flow_info = "1";
			
	/**
	 * define url
	 */
	public OdlUtil(String host,int port) {
		this.url = "http://"+host+":"+port;
		this.setMeterInfo();
	}
	
	/**
	 * authorization url information by using name and password
	 */
	private String getBasicAuthStr(String name,String password) {
		String userpassword = name+":"+password;
		String encoding = Base64.getEncoder().encodeToString(userpassword.getBytes());
		return encoding;
	}
	
	/**
	 * configure meter info
	 */
	private void setMeterInfo() {
		this.meter_info = "{\n" + 
				"    \"flow-node-inventory:meter\": [\n" + 
				"        {\n" + 
				"            \"meter-id\": "+meter_id +",\n" + 
				"            \"flags\": \"meter-kbps\",\n" + 
				"            \"meter-band-headers\": {\n" + 
				"                \"meter-band-header\": [\n" + 
				"                    {\n" + 
				"                        \"band-id\": "+meter_id+1+",\n" + 
				"                        \"band-rate\": "+bandrate +",\n" + 
				"                        \"band-burst-size\": 0,\n" + 
				"                        \"meter-band-types\": {\n" + 
				"                            \"flags\": \"ofpmbt-drop\"\n" + 
				"                        },\n" + 
				"                        \"drop-rate\": "+ bandrate +",\n" + 
				"                        \"drop-burst-size\": 0\n" + 
				"                    }\n" + 
				"                ]\n" + 
				"            }\n" + 
				"        }\n" + 
				"    ]\n" + 
				"}";
	}
	
	/**
	 * configure flow info
	 */
	private void setFlowInfo() {
		this.flow_info = "{\n" + 
				"    \"flow-node-inventory:flow\": [\n" + 
				"        {\n" + 
				"            \"id\": "+ flow_id+",\n" + 
				"            \"idle-timeout\": 0,\n" + 
				"            \"hard-timeout\": 0,\n" + 
				"            \"barrier\": true,\n" + 
				"            \"instructions\": {\n" + 
				"                \"instruction\": [\n" + 
				"                    {\n" + 
				"                        \"order\": 1,\n" + 
				"                        \"apply-actions\": {\n" + 
				"                            \"action\": [\n" + 
				"                                {\n" + 
				"                                    \"order\": 1,\n" + 
				"                                    \"output-action\": {\n" + 
				"                                        \"max-length\": 65535,\n" + 
				"                                        \"output-node-connector\": "+"\""+output+"\""+"\n" + 
				"                                    }\n" + 
				"                                }\n" + 
				"                            ]\n" + 
				"                        }\n" + 
				"                    },\n" + 
				"                    {\n" + 
				"                        \"order\": 0,\n" + 
				"                        \"meter\": {\n" + 
				"                            \"meter-id\": "+meter_id+"\n" + 
				"                        }\n" + 
				"                    }\n" + 
				"                ]\n" + 
				"            },\n" + 
				"            \"priority\": 20,\n" + 
				"            \"table_id\": 0,\n" + 
				"            \"match\": {\n" + 
				"                \"in-port\": "+"\""+input+"\""+"\n" + 
				"            }\n" + 
				"        }\n" + 
				"    ]\n" + 
				"}";
	}
	
	/**
	 * get topo info
	 */
	public String getTopology(String containerName,String username,String password){
		http.setBasicAuth(getBasicAuthStr(username,password));
		String str = http.sendGet(url+"/restconf/operational/network-topology:network-topology/topology/flow:1"
		+containerName, "");
		return str;
	}
	
	/**
	 * get table information
	 */
	public String getTableFlow(String containerName,String username,String password) {
		//验证用户名和密码
		http.setBasicAuth(getBasicAuthStr(username,password));
		String str = http.sendGet(url+"/restconf/operational/opendaylight-inventory:nodes/node/openflow:1/table/0/"
		+containerName, "");
		return str;
	}
	
	/**
	 * set band rate of switch port
	 */
	public void SetBandRate(float Bandrate) {
		this.bandrate = (int)Bandrate;
	}
	
	/**
	 * set node
	 */
	public void SetNode(String Node) {
		this.node = Node;
	}
	
	/**
	 * set input
	 */
	public void SetInput(String input) {
		this.input = input;
	}
	
	/**
	 * set output
	 */
	public void SetOutput(String output) {
		this.output = output;
	}
	
	/**
	 * set Meter Id
	 */
	public void setMeterId(int meterid) {
		this.meter_id = meterid;
	}
	
	/**
	 * get Meter id
	 */
	public int getMeterId() {
		return meter_id;
	}
	
	/**
	 * configure meter information
	 */
	public String putMeter(String containerName,String username,String password) {
		http.setBasicAuth(getBasicAuthStr(username,password));
		setMeterInfo();
		String str = http.sendPut(url+"/restconf/config/opendaylight-inventory:nodes/node/"
				+ node +"/flow-node-inventory:meter/"+meter_id
				+containerName, meter_info);
		return str;
	}
	 
	/**
	 * configuration flow information
	 */
	public String putTableFlow(String containerName,String username,String password) {
		http.setBasicAuth(getBasicAuthStr(username,password));
		setFlowInfo();
		String str = http.sendPut(url+"/restconf/config/opendaylight-inventory:nodes/node/"
				+ node +"/table/0/flow/"+flow_id
				+containerName, flow_info);
		flow_id++;
		return str;
	}
	
	/**
	 * get Port statistics information
	 */
	public String getPortInfo(String containerName,String username,String password) {
		http.setBasicAuth(getBasicAuthStr(username,password));
		String str = http.sendGet(url+"/restconf/operational/opendaylight-inventory:nodes",  "");
		return str;
	}
	
	public String getTopology(){
		return getTopology("","admin","admin");
	}
	
	public String putMeter() {
		return putMeter("","admin","admin");
	}
	
	public String getTableFlow() {
		return getTableFlow("","admin","admin");
	}
	
	public String putTableFlow() {
		return putTableFlow("","admin","admin");
	}
	
	public String getPortInfo() {
		return getPortInfo("","admin","admin");
	}
}
