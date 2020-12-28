## Http

#### TCP/IP  protocol

不同硬件，操作系统之间通信必须有一个规则，如，如何检测目标，哪一边先开始通信，通信内容规范，如何结束通信，需要事先约定。这些规则称为协议。

协议中存在中内容，电缆规格到ip地址选定，协议族的统称。

分层管理：应用层，链路层，网络层，数据链路层





### 应用层协议有：FTP，文件传输协议，DNS （domain name system）,Http

DNS负责域名解析，即提供 IP地址与域名之间的转换

### 传输层协议：udp ,tcp

作用：应用层报文分割，打上标记序号与端口号转发给网络层

tcp协议：三次握手保证可靠性 

 握手标志：sync(synchronize)+ack(ackknolegement)

1.c发sync 2.s返回sync+ack 3.发送ack数据包

tcp协议会将Http请求报文分割成报文段，加上序号，传给对方，接收方进行重组。//不懂，不是流吗？

### 网络层：IP

该层负责数据包（最小的网络传输单位）传输路线的选定,即中转，增加MAC地址后转发给链路层
IP：internetprotocol简称，包括MAC地址（网卡所属地址）+IP地址等，IP地址可变换，MAC基本不会
ARP：Address Resolution Protocol,根据IP可以查到MAC，信息通过设备中转时需要

### 链路层：操作系统，驱动，网卡（NIC）,光纤



### URI

url是uri子集

uri指定方式：  完整请求uri    http：//host/indext.htm     或者使用相对路径，在请求头另外指定host

协议名+认证信息+服务器ip+服务器port+file path+？+查询字符串+片段标识符（已获取的子资源，可选）

RFC（request for comments）,进行http通信的必要条件

### Http请求与响应

##### 请求报文

包括 请求方法+请求uri+协议版本+请求头+请求体   
请求方法包括：get，post,put,delete

put传输文件，如果用PUT来达到更改资源，需要client提交资源全部信息，server会进put或update,以httpcode区分，幂等性


post 不幂等,实现部分资源更新

GET参数通过URL传递有限制，POST放在Request body无限制

GET没POST安全，因为参数直接暴露在URL上，所以不能用来传递敏感信息	

**GET和POST本质上就是TCP链接，并无差别**

****对于GET方式的请求，浏览器会把http header和body一并发送出去，服务器响应200（返回数据）；

而对于POST，浏览器先发送header，服务器响应100 continue，浏览器再发送body，服务器响应200 ok（返回数据）,两次包的TCP在验证数据包完整性上，有非常大的优点。并不是所有浏览器都会在POST中发送两次包，Firefox就只发送一次。



请求头信息：Host ,Connection,ContentType(？包括哪几种),ContentLenth

响应报文包括 协议版本，状态码，状态码备注，响应头，响应体

### Http无状态

对于发送的请求与响应**不做持久化处理**，为了更快的处理大量事务

为了能保存用户登录状态，使用cookie技术****

### 2.7HTTP持久连接

初始版本，每通信一次就断开一次，开销很大
keep-alive 只要任意一端没提出断开，则保持连接
Http1.1默认keep-alive,1.0未标准化

#### 2.7.2管线化

并行请求，减少请求时间

### 2.8cookie技术

server 响应 header:Set-Cookie
客户端会自动加入cookie再返回，通过这个判断请求从何处来与请求所处状态

### 2.9Http完整报文组成部分

- 请求行：方法，uri，http版本
- header
- CR+LF
- body

#### 2.91header细分

##### 2.91general header 

-  cachcontrol:max-age/private/no-cach(客户端不要缓存）
- connection：keep-alive
- via 代理服务器

##### 2.92requst header

- cookie：请求头 expires,服务端只能通过覆盖让其过期
- accept-charset 
- host 区分代理服务器中某个主机
- upgrade:websocket

##### 2.93resonse header



Content-Type:multipart/form-data 

Content-Range:bytes =1000-2000,5001-10000 

部分请求

### 3HTTP状态码：

####  2XX OK

204 no content   告诉客户端可以使用当前，不需要更新
206 partial content       设置content-range

3xx重定向
301绝对重定向
302临时重定向
304 和重定向没关系，告诉客户端可以使用缓存

4xx bad request 客户端错误
400：语法错误
401:unauthorized 

403: forbidden 拒绝
404: not found 找不到资源

5xx:服务器错误

500： internal server error

503: service unavailable，超载

#### Https

1.http+ssl(secure socket layer) = https  ssl不仅可以和http结合

2.内容加密

3.md5+sha1校验防止内容被串改

4.对称加密：密钥会被窃取

5.非对称加密：你使用我的公钥加密，发来文件，我用我的私钥解密 效率低

6.https综合两者，在交换密钥环节使用非对称，交换完密钥使用对称

7.https还是慢加密解密太累人了，使用ssl加速器缓解硬件压力

8.一般情况下，使用https传输敏感信息，http传输正常信息



#### http瓶颈

无法获取服务器最新消息，必须轮询才知道

Spdy:Google提出的基于传输控制协议(TCP)的应用层协议，压缩、多路复用和优先级来缩短加载时间

- okhttp采用在应用层与传输层之家增加会话层解决，spdy可以无限处理并发，并分配请求的优先级
- 支持压缩http 的header 数据包减少
- 支持服务器向客户端推送
- 服务器提示

websocket:http建立连接后，进行切换协议的握手，切换完成后就可以相互发送数据



