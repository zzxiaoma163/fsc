# fsc
## 使用netty实现文件同步

### 项目说明：
    本项目采用Java Netty作为后台，本项目由三个工程组成。
    fscmonitor:监控目录中文件的变化
    fscclient:把监控目录中的文件发送到服务器
    fscserver:服务器接收文件

### 主要配置说明：
	fscmonitor
		monitor.monitoraddress:监控的目录
		monitor.address:生成文本的目录
	fscclient
		client.serverip:服务器ip
		client.port:服务器端口
		client.monitoraddress:监控的目录
		client.address:监控生成的文本目录
	fscserver
		server.port:服务器端口
		server.filepath:存储目录

### 项目顺序启动:
	fscmonitor:FileMonitor
	fscserver:FscServerMain
	fscclient:FscClientMain
	
QQ群：69740685