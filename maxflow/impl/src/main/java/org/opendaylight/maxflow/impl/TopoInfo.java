/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/***
 * by using json analyze url getting from opendaylight controller
 */
public class TopoInfo {
	Map<String,Float> remainBwArray = new HashMap<String,Float>() ;
	Map<String,List<String>> connectorinfo = new HashMap<String,List<String>>() ;
	private JsonParser parser = new JsonParser();
	graph TopoGraph = new graph();
	List<node> nodeArray = new LinkedList<node>();
	List<Edge> edgeArray = new LinkedList<Edge>();
	node Source_Node = new node();
	node Dest_Node = new node();
	int nodenum = 0;
	int edgenum = 0;
	OdlUtil odlutil = new OdlUtil("127.0.0.1",8181);
	private JsonArray link;
	private JsonArray node;
	
	/**
	 * get information about nodes and links
	 */
	public void getInfo(float weight) {
		String str = odlutil.getTopology();
		//获得根节点
		JsonElement root = parser.parse(str);
		JsonObject element = root.getAsJsonObject();
		//获得拓扑
		JsonObject Topo = element.getAsJsonArray("topology").get(0).getAsJsonObject();
		node = Topo.getAsJsonArray("node");
		if(node!=null&&node.size()!=0) {
			for(int i =0 ;i<node.size();i++) {
				JsonElement tmp = node.get(i);
				node tmpNode = new node();
				tmpNode.setIndex(i);	
				tmpNode.SetType(tmp.getAsJsonObject().get("node-id").toString().replaceAll("\"", ""));				
				if(tmp.getAsJsonObject().has("host-tracker-service:addresses")) {
					JsonArray ip = tmp.getAsJsonObject().getAsJsonArray("host-tracker-service:addresses");
					tmpNode.setIp(ip.get(0).getAsJsonObject()
					.get("ip").toString().replaceAll("\"", ""));
				}
				nodeArray.add(tmpNode);
				nodenum++;
			}
		}
		link = Topo.getAsJsonArray("link");
		if(link!=null&&link.size()!=0) {
			for(int j=0;j<link.size();j++) {
				JsonElement tmp = link.get(j);
				Edge tmpEdge = new Edge();
				//source
				JsonObject source = tmp.getAsJsonObject().getAsJsonObject("source");
				String source_node = source.get("source-node").toString().replaceAll("\"", "");
				//destination
				JsonObject dest = tmp.getAsJsonObject().getAsJsonObject("destination");
				String dest_node = dest.get("dest-node").toString().replaceAll("\"", "");
				//link id
				tmpEdge.setId(source.get("source-tp").toString().replaceAll("\"", "")
						+"/"+dest.get("dest-tp").toString().replaceAll("\"", ""));
				//set Max weight and remain weight of the link
				tmpEdge.setMaxWeight(weight);
				System.out.println(SwitchToPortStr1(tmpEdge.getId()));
				tmpEdge.setWeight(getLinkRemainBw(SwitchToPortStr1(tmpEdge.getId())));
				//edge
				for(int k = 0;k<nodeArray.size();k++) {
					if(nodeArray.get(k).GetType().equals(source_node)) {
						Source_Node = nodeArray.get(k);
					}else if(nodeArray.get(k).GetType().equals(dest_node)) {
						Dest_Node = nodeArray.get(k);
					}
				}
				tmpEdge.setFrom(Source_Node);
				tmpEdge.setTo(Dest_Node);
				edgeArray.add(tmpEdge);
				edgenum++;
			}
		}
	}
	
	public void setMeter() {
		String meterinfo = odlutil.putMeter();
		System.out.println(meterinfo);
	}
	
	public void setFlow() {
		String flowinfo = odlutil.putTableFlow();
		System.out.println(flowinfo);
	}
	
	/**
	 * get connector info for each switch
	 */
	public Map<String,List<String>> ConnectorInfo(){
		Map<String,List<String>> connectorinfo = new HashMap<String,List<String>>();

		String info1 = odlutil.getPortInfo();
		//获得根节点
		JsonElement root = parser.parse(info1);
		JsonObject element = root.getAsJsonObject().getAsJsonObject("nodes");
		JsonArray nodes = element.getAsJsonArray("node");
		for(int i=0;i<nodes.size();i++) {
			String id = nodes.get(i).getAsJsonObject().get("id").toString().replaceAll("\"", "");
			List<String> temp = new LinkedList<String>();
			JsonArray connectors = nodes.get(i).getAsJsonObject().getAsJsonArray("node-connector");
			for(int j=0;j<connectors.size();j++) {
				String name = connectors.get(j).
						getAsJsonObject().get("flow-node-inventory:name").toString().replaceAll("\"", "");
				if(name.length() > 2) {
					temp.add(name);
				}
			}
			connectorinfo.put(id, temp);
		}
		return connectorinfo;
	}
	
	/**
	 * set connectorInfo
	 */
	public void setConnectorInfo() {
		this.connectorinfo = ConnectorInfo();
	}
	
	/**
	 * get connectorInfo
	 */
	public Map<String,List<String>> getConnectorInfo(){
		return connectorinfo;
	}
	
	/**
	 * configure network Qos for each switch
	 */
	public void setQosForNetwork(float bandrate) {
		//set connector info
		setConnectorInfo();
		
		odlutil.SetBandRate(bandrate);
		//遍历
		for(Map.Entry<String, List<String>> entry: connectorinfo.entrySet()) {
			odlutil.SetNode(entry.getKey());
			System.out.println(entry.getKey());
			setMeter();
			
			List<String> temp = entry.getValue();
			for(int i=0;i<temp.size();i++) {
				odlutil.SetInput(temp.get(i));
				for(int j=0;j<temp.size();j++) {
					if(i!=j) {
						odlutil.SetOutput(temp.get(j));
						setFlow();
					}
				}
			}
		}
	}
	
	/**
	 * node link init
	 */
	public void init(float weight) {
		getInfo(weight);
	}
	
	/**
	 * create graph by using nodes and edges information
	 */
	public void createGraph() {
		TopoGraph.setNodenum(nodenum);
		TopoGraph.init();
		//插入边信息
		for(int i=0;i<edgeArray.size();i++) {
			int j = FindLink(GetAnotherSameEdge(edgeArray.get(i).getId()));
			if(!edgeArray.get(i).isVisited && !edgeArray.get(j).isVisited) {
				//edgeArray.get(j).setWeight(10.0f, false);
				edgeArray.get(i).setVisited(true);
				edgeArray.get(j).setVisited(true);
				TopoGraph.AddEdge(edgeArray.get(i));
				TopoGraph.AddEdge(edgeArray.get(j));
			}
			//TopoGraph.AddEdge(edgeArray.get(i));
		}
		//输出查看是否正确
		//TopoGraph.DescribeGraph();		
	}
	
	/**
	 * switch link id to port info
	 */
	public String SwitchToPortStr(String linkid) {
		String[] tmpStr = linkid.split("/");
		int pos1 = tmpStr[0].indexOf("openflow");
		if(pos1 != -1) {
			return tmpStr[0];
		}else {
			return tmpStr[1];
		}
	}
	
	/**
	 * Switch link id to port string
	 */
	public List<String> SwitchToPortStr1(String linkid) {
		List<String> PortStr = new LinkedList<>();
		PortStr.add(SwitchToPortStr(linkid));
		return PortStr;
	}
	
	/**
	 * handle same edge
	 */
	public String GetAnotherSameEdge(String link1) {
		String[] link1Arr = link1.split("/");
		String link = link1Arr[1] +"/"+ link1Arr[0];
		return link;
	}
	
	/**
	 * calculate remain bandwidth via json file
	 */
	public Map<String,Float> remainBw() {
		Map<String,Float> result = new HashMap<String,Float>();
		String info1 = odlutil.getPortInfo();
		//获得根节点
		JsonElement root = parser.parse(info1);
		JsonObject element = root.getAsJsonObject().getAsJsonObject("nodes");
		JsonArray nodes = element.getAsJsonArray("node");
		for(int i=0;i<nodes.size();i++) {
			JsonArray connectors = nodes.get(i).getAsJsonObject().getAsJsonArray("node-connector");
			for(int j=0;j<connectors.size();j++) {
				String id = connectors.get(j).
						getAsJsonObject().get("id").toString().replaceAll("\"", "");
				if(connectors.get(j).getAsJsonObject().has("opendaylight-port-statistics:flow-capable-node-connector-statistics")) {
					JsonObject statistics = connectors.get(j).getAsJsonObject()
							.getAsJsonObject("opendaylight-port-statistics:flow-capable-node-connector-statistics");
					float rx1 = statistics.getAsJsonObject("bytes").get("received").getAsFloat();
					System.out.println(id);
					result.put(id, rx1);
				}
			}
		}
		Thread.currentThread();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String info2 = odlutil.getPortInfo();
		root = parser.parse(info2);
		element = root.getAsJsonObject().getAsJsonObject("nodes");
		nodes = element.getAsJsonArray("node");
		for(int i=0;i<nodes.size();i++) {
			JsonArray connectors = nodes.get(i).getAsJsonObject().getAsJsonArray("node-connector");
			for(int j=0;j<connectors.size();j++) {
				String id = connectors.get(j).
						getAsJsonObject().get("id").toString().replaceAll("\"", "");
				if(connectors.get(j).getAsJsonObject().has("opendaylight-port-statistics:flow-capable-node-connector-statistics")) {
					JsonObject statistics = connectors.get(j).getAsJsonObject()
							.getAsJsonObject("opendaylight-port-statistics:flow-capable-node-connector-statistics");
					Float rx2 = statistics.getAsJsonObject("bytes").get("received").getAsFloat();
					float duration = (rx2-result.get(id))*8/3/1024;
					result.put(id,duration);
				}
			}
		}
		return result;
	}
	
	/**
	 * set remaining bandwidth
	 */
	public void setRemainBw() {
		this.remainBwArray = remainBw();
	}
	
	/**
	 * get remain bandwidth array
	 */
	public Map<String,Float> getRemainBw(){
		return remainBwArray;
	}
	
	/**
	 * 如果一条链路仅有一个交换机端口，则计算一次
	 * 如果一条链路是由两个交换机端口相连，则返回两者中较小的端口速率
	 */
	public Float getLinkRemainBw(List<String> portStr) {
		if(portStr.size() == 1 ) {
			return getRemainBw().get(portStr.get(0));
		}else {
			if(getRemainBw().containsKey(portStr.get(1))) {
				return Math.min(getRemainBw().get(portStr.get(0)),
					getRemainBw().get(portStr.get(1)));
			}
		}
		return 0.0f;
	}
	
	/**
	 * find index from node array via node id
	 */
	public int FindNode(String node_id) {
		for(int i=0;i<nodenum;i++) {
			if(nodeArray.get(i).getIp()!=null) {
				if(nodeArray.get(i).getIp().equals(node_id)) {
					return nodeArray.get(i).getIndex();
				}
			}	
		}
		return -1;
	}
	
	/**
	 * get link by using link id
	 */
	public int FindLink(String link_id) {
		for(int i=0;i<edgenum;i++) {
			if(edgeArray.get(i).getId().equals(link_id)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * get max flow from source node to destination node
	 */
	public float getMaxFlow(String source,String destination) {
		int Source = FindNode(source);
		int Dest = FindNode(destination);
		//return TopoGraph.BFSPath(Source, Dest);
		return TopoGraph.edmondsKarpMaxFlow(Source, Dest);
	}
}
