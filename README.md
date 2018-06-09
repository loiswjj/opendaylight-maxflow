# opendaylight-maxflow
develop a application based on SDN network via opendaylight

按照opendaylight官网hello示例进行开发的MD-SAL应用程序maxflow
功能为：计算网络中存在的两个主机之间的网络最大流

难点在于对网络链路中的链路带宽的测量

核心代码：maxflow/impl/src/main/java/org/opendaylight/maxflow/impl
