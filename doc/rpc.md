RPC（Remote Procedure Call）即远程过程调用，是一种计算机通信协议，允许程序在不同计算机之间进行通信和交互，就像本地调用一样

## 基本设计

![](.\img\design.jpg)

rpc

├── example-common       -- 示例公共模块：和服务相关的接口和数据模型

├── example-provider     -- 示例服务提供者：真正实现接口的模块

├── example-consumer     -- 示例服务消费者：调用服务的模块

└── rpc-core    -- rpc框架



## web服务器

让服务提供者提供可远程访问的服务，能够处理请求并返回响应

可以选择SpringBoot内嵌的Tomcat、NIO框架 Netty 和 Vert.x 等

[Eclipse Vert.x (vertx.io)](https://vertx.io/)

[Vert.x (vertx-china.github.io)](https://vertx-china.github.io/docs/)

1. 引入Vert.x依赖

   ```xml
   <dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-core</artifactId>
    <version>4.5.7</version>
   </dependency>
   ```

2. 编写一个web服务器的接口HttpServer，定义统一的启动服务的方法，便于后续扩展，比如实现多种不同的web服务器

3. 编写基于Vert.x实现的Web服务器，能够监听指定端口并处理请求

   ```java
   public class VertHttpServer implements HttpServer{
       @Override
       public void doStart(int port) {
           // 创建vertx实例
           Vertx vertx = Vertx.vertx();
   
           // 创建 http 服务器
           io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
   
           // 监听端口并处理请求
           httpServer.requestHandler(request -> {
               // 处理 http 请求
               System.out.println(request.method()+" "+ request.uri());
   
               // 发送 http 响应
               request.response()
                       .putHeader("content-type", "text/plain")
                       .end("hello from vert.x http server");
           });
   
           // 启动 http 服务器并监听指定端口
           httpServer.listen(port, httpServerResult -> {
               if(httpServerResult.succeeded()) {
                   System.out.println("Server is now listening on port: "+port);
               } else{
                   System.out.println("Fail to start server:"+httpServerResult.cause());
               }
           });
       }
   }
   ```

4. 验证web服务器能否正常启动成功并接受请求

   ```java
   public static void main(String[] args) {
       VertxHttpServer vertHttpServer = new VertHttpServer();
       vertxHttpServer.doStart(8080);
   }
   ```

   浏览器访问 `localhost:8080` 看能否正常打印



## 本地服务注册器

使用线程安全的ConcurrentHashMap 存储服务注册信息，key为服务名称、value为服务的实现类。之后可以根据要调用的服务名称获取对应的实现类，然后通过反射调用方法

本地服务注册器和注册中心区别：注册中心的作用侧重管理注册的服务、提供服务信息给消费者；本地服务注册器作用是根据服务名获取对应的实现类。

```java
public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new HashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 获取服务实现类
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}
```

服务提供者启动后将注册服务到注册器中



## 序列化器

序列化：将java对象转为可传输的字节数组

反序列化：将字节数组转为java对象

序列化方式：java原生序列化、JSON、Hessian、Kryo、protobuf等

**JSON**

优点：

1. 易读性好，可读性强，便于人类理解与调试；
2. 跨语言支持广泛

缺点：

1. 序列化后的数据量比较大，因为JSON使用文本格式存储数据，需要额外的字符表示键、值和数据结构
2. 不能很好的处理复杂数据结构和循环引用，可能导致性能下降或序列化失败

**Hessian**

[Hessian Binary Web Service Protocol (caucho.com)](https://hessian.caucho.com/)

优点：

1. 二进制序列化，序列化后的数据量小，网络传输效率高

2. 支持跨语言，适用于分布式系统中的调用

缺点：

1. 性能较JSON较低
2. 对象必须实现 Serializable 接口

**Kryo**

[EsotericSoftware/kryo: Java binary serialization and cloning: fast, efficient, automatic (github.com)](https://github.com/EsotericSoftware/kryo)

优点：

1. 高性能，序列化反序列化快
2. 支持循环引用和自定义序列化器，适用于复杂的对象结构
3. 无需实现 Serializable 接口

缺点：

1. 不跨语言，只适用java
2. 序列化格式不够友好，不易读和调试
3. 线程不安全

​	**Protobuf**

优点：

1. 二进制序列化，数据量小
2. 支持跨语言
3. 支持版本化向前/向后兼容

缺点：

1. 配置相对复杂，需要先定义数据结构的消息格式
2. 序列化格式不够友好，不易读和调试



**动态使用序列化器:**

1. 定义序列化器名称常量
2. 添加配置项
3. 定义序列化器工厂：序列化器是可以复用的，没必要每次执行序列化操作前都创建一个新的对象。所以可以使用 **工厂模式+单例模式**来简化创建和获取序列化器对象的操作。
4. 动态获取序列化器



**自定义序列化器：**

1. 指定SPI配置目录

   系统内置的SPI机制会加载 resource 资源目录下的 `META-INF/services` 目录，那么我们自定义的序列化器可以如法炮制，改为读取 `META-INF/rpc` 目录。还可以将SPI分为系统内置的SPI（`META-INF/rpc/system`）和用户自定义的SPI（`META-INF/rpc/custom`）

   这样所有接口的首先类都可以通过SPI动态加载，不用在代码中硬编码。

2. 编写SpiLoader 加载器，读取配置并加载实现类

   1. 用Map来存储已加载的配置信息 ：键名->实现类
   2. 扫描指定路径，读取配置文件，获取信息并存到Map中
   3. 定义获取实例的方法，根据传入的接口和键名，从Map中找到对应的实现类，然后通过反射获取实现类对象。可以维护一个对象实例缓存，创建过一次的对象从缓存中读取即可。

3. 修改序列化器工厂



## 提供者处理调用-请求处理器

请求处理是RPC框架的关键，作用是：处理接收到的请求，并根据请求参数找到对应的服务和方法，通过反射实现调用，最后封装返回结果并响应请求

**1. 编写请求类和响应类**

请求类 RpcRequest 的作用：封装调用所需的信息，比如服务名称、方法名称、调用参数的类型列表、参数列表等。这些都是java反射所需的参数

响应类 RpcResponse 的作用：封装调用方法得到的返回值以及调用信息（异常等）等

**2. 编写请求处理器**

请求处理器 HttpServerHandler：

1. 反序列化请求对象，并从请求对象中获取参数
2. 根据服务名称从本地服务注册器中获取服务实现类
3. 通过反射机制调用方法，得到返回结果
4. 对结果进行封装和序列化，并写入响应中

**3. 给HttpServer绑定请求处理器**



## 消费者发起调用-代理

分布式系统中，我们调用其他项目的接口时，一般只关注请求参数和结果，而不关注具体的实现过程。我们可以通过生成代理对象来简化消费方的调用，代理方式分为静态代理和动态代理

### 静态代理

为每一个特定类型的接口或对象编写一个代理类。

构造http请求去调用服务提供者

### 动态代理

根据要生成的对象类型，自动生成一个代理对象

常用的动态代理实现方式有 JDK动态代理和基于字节码生成的动态代理（比如CGLIB），前者简单易用、无需引入额外的库，缺点是只能对接口进行代理；后者更灵活、可以对任何类进行代理，但性能略低于JDK动态代理。



1. 编写代理类 ServiceProxy， 需要实现 InvocationHandler 接口的 invoke 方法
2. 创建动态代理工厂 ServiceProxyFactory， 根据指定类创建动态代理对象
3. 通过调用工厂获取动态代理对象



## 全局配置加载

配置项：

1. 注册中心地址
2. 服务接口
3. 序列化方式
4. 网络通信协议
5. 超时设置
6. 负载均衡策略
7. 服务端线程模型



实现：

1. 新建配置类，用于保存配置信息
2. 新建工具类 ConfigUtils，读取配置类并并返回配置对象
3. 维护全局配置对象：在项目启动时，从配置文件中读取配置并创建对象实例，之后就可以集中的从对象中取配置信息，而不用每次加载配置时再重新读取配置、并创建新的对象，减少了性能开销。使用双检锁单例模式

使用RpcApplication 类作为 RPC项目启动入口、并维护项目全局用到的变量。



扩展：

1. 支持读取yml、ymal等不同格式文件
2. 支持监听配置文件的变更，并自动更新配置对象：props.autoLoad() 可以实现配置文件的监听和自动加载
3. 配置分组
4. 配置支持中文



## 接口Mock

实际开发和测试中有时可能无法直接访问真实的远程服务，这种情况下就需要使用mock服务来模拟远程服务的行为，以便进行接口测试和开发。

模拟对象，通常用于测试代码中，特别时单元测试中，便于我们跑通测试

使用动态代理创建调用方法时返回固定值的mock对象

1. 通过修改配置文件的方式开启mock
2. 增加代理类用于生成mock代理服务返回固定值
3. 服务代理工厂新增获取mock代理对象的方法。可以通过读取配置mock来区分创建哪种代理对象



## SPI机制

SPI（Service Provider Interface）服务提供接口是java的机制，主要用于实现模块化开发和插件化扩展。

SPI机制允许服务提供者通过特定的配置文件将自己的实现注册到系统中，然后通过反射机制动态加载这些实现，而不需要修改原始框架代码，从而实现系统解耦、提高可扩展性。

**系统实现：**

Java内已经提供了SPI机制相关的API接口，可以直接使用。

1. 在 resources 资源目录下创建 `META-INF/services` 目录，并创建一个名称为要实现的接口的空文件

2. 文件中填写自己定制的接口实现类的完整类路径

3. 直接使用系统内置的ServiceLoader 动态加载指定接口的实现类

   ```java
   Serializer serializer = null;
   ServiceLoader<Serializer> serviceLoader = ServiceLoader.load(Serializer.class);
   for(Serializer service : serviceLoader) {
       serializer = service;
   }
   ```



**自定义实现：**

根据配置加载类即可

读取配置文件，得到一个`序列化器名称 => 序列化器实现类` 的映射，之后就可以根据用户配置的序列化器名动态加载指定实现类对象



扩展：

1. 序列化器工厂可以使用懒加载方式创建序列化器实例：懒汉式单例模式
2. SPI loader 支持懒加载，获取实例时才加载对应的类：双检锁单例模式



## 注册中心

帮助服务消费者获取服务提供者的调用地址，而不用将调用地址硬编码到项目中

注册中心核心能力：

1. 数据分布式存储：集中注册信息数据存储、读取和共享
2. 服务注册：服务提供者上报服务信息到注册中心
3. 服务发现：服务消费者从注册中心拉取服务信息
4. 心跳检测：定期检查服务提供者的存活状态
5. 服务注销：手动剔除节点、或者自动剔除失效节点
6. 更多优化点：注册中心本身的容错、服务消费者缓存等

### Etcd

[etcd-io/etcd: Distributed reliable key-value store for the most critical data of a distributed system (github.com)](https://github.com/etcd-io/etcd)

Etcd是Go语言实现的、开源的、分布式的键值存储系统，主要用于分布式系统中的服务发现、配置管理和分布式锁等场景

Etcd性能很高，采用Raft一致性算法来保证数据的一致性和可靠性，具有高可用性、强一致性、分布式等特点。



Etcd使用**层次化的键值对**来存储数据，支持类似于文件系统路径的层次结构，能够灵活地单key查询、按前缀查询、按范围查询



Etcd核心数据结构：

1. key：Etcd中的基本数据单元，类似文件系统中的文件名。每个键都唯一标识一个值，并且可以包含子键，形成类似于路径的层次结构
2. value：于键关联的数据，可以是任意类型的数据，通常是字符串形式



Etcd核心特性：

1. Lease（租约）：设置键值对的过期时间，租约过期时键值对自动删除
2. Watch（监听）：可以监听特定键值对的变化，键值对发生变化时会触发相应的通知



Etcd如何保证数据一致性？

从表层看，Etcd支持事务操作，能够保证数据一致性

从底层看，Etcd使用Raft一致性算法来保证数据的一致性

Raft算法选举出一个 leader 节点，该节点负责接受客户端的写请求，并将写操作复制到其他节点上。当客户端发送写请求时，领导者首先将写操作写入自己的日志，并将写操作的日志条目分发给其他节点，其他节点收到后写入自己日志。一旦超过半数节点都将日志条目成功写入自己的日志，该日志条目就被视为已提交，领导者会向客户端发送成功响应。



可视化工具：[evildecay/etcdkeeper: web ui client for etcd (github.com)](https://github.com/evildecay/etcdkeeper/?tab=readme-ov-file)



Java客户端：[etcd-io/jetcd: etcd java client (github.com)](https://github.com/etcd-io/jetcd)

常用客户端：

1. kvClient：用于对etcd中的键值对进行操作，通过kvClient可以进行设置值、获取值、删除值等操作
2. leaseClient：用于管理etcd的租约机制，用于为键值对分配生存时间，租约到期自动删除，通过其可以创建、获取、续约和撤销租约
3. watchClient：用于监视etcd键值变化，键值变化时接收通知
4. clusterClient：集群
5. authClient：身份验证和授权
6. maintenanceClient：维护操作，如健康检查、数据库备份等
7. lockClient：用于实现分布式锁功能
8. electionClient：用于实现分布式选举功能



### 存储结构设计

由于一个服务可能有多个服务提供者，可以有两种结构设计：

1. 层级结构：将服务理解为文件夹、将服务对应的多个节点理解为文件夹下的文件，那么可以通过服务名称，用前缀查询的方式查询到某个服务的所有节点。键名规则可以是：业务前缀/服务名/服务节点地址。适合Zookeeper和Etcd这种支持层级查询的中间件
2. 列表结构：将所有服务节点以列表的形式整体作为value。适合Redis



### 注册中心开发

1. 注册信息定义：model包下新建 ServiceMetaInfo 类，封装服务的注册信息，包括服务名称、服务版本号、服务地址、服务分组等

2. 注册中心配置：config包下新建 RegisterConfig 类，让用户配置连接注册中心所需的信息，比如注册中心类别、地址、用户名、密码、连接超时时间等

3. 注册中心接口：可以实现不同注册中心，和序列化机制一样可以使用SPI机制动态加载。提供初始化、注册服务、注销服务、服务发现、服务销毁等方法

4. etcd注册中心实现：EtcdRegistry 类



### 支持配置和扩展注册中心

能够通过填写配置来指定注册中心，并且支持自定义注册中心，和序列化器一样使用工厂创建对象、使用SPI动态加载自定义的注册中心

1. 注册中心常量
2. 使用工厂模式，支持根据key从SPI获取注册中心对象实例
3. 在META-INF/system 目录下编写注册中心接口的SPI配置文件
4. 初始化注册中心，在RpcApplication中

调用流程：

1. 服务消费者先从注册中心获取节点信息，再调用地址并执行
2. 修改服务代理逻辑，从注册中心获取服务地址



### 心跳检测和续期机制

定期发送心跳信号来检测系统的状态。实现心跳检测一般需要两个关键：定时、网络请求

思路：使用Etcd自带的key过期机制，给注册节点一个生命倒计时，让节点定期续期重置自己的倒计时，如果节点宕机一直不续期就会过期删除

1. 服务提供者向Etcd注册自己的服务信息，并在注册时设置TTL
2. Etcd在收到服务提供者的注册信息后，会自动维护信息的TTL，并在TTL过期时删除该信息
3. 服务提供者定期请求Etcd续签自己的注册信息，重置TTL。续期时间一定要小于过期时间
4. 在本地维护一个已注册节点集合，注册时添加节点key到集合中，只需要续期集合内的key即可



实现：

1. 给注册中心 Registry 接口添加心跳检测方法
2. 维护续期节点集合：注册时添加，注销时删除
3. 实现心跳检测接口：利用定时任务对集合中的节点执行重新注册操作
4. 开启heartBeat：注册中心初始化时开启



### 服务节点下线机制

主动下线：服务提供者正常退出时，从注册中心删除注册信息

被动下线：服务提供者异常退出时，利用Etcd的key过期机制自动移除



利用 JVM 的 ShutdownHook 实现java项目正常退出时主动删除注册信息：

 JVM 的 ShutdownHook 是Java虚拟机提供的一种机制，允许开发者在 JVM 关闭前执行一些清理工作或其他必要操作，例如关闭数据库连接、释放资源、保存临时数据等



1. 编写 destory 方法，将当前下线节点从注册中心删除
2. 项目初始化时注册ShutdownHook，当程序正常退出时会执行注册中心的destory方法



### 消费端服务缓存

正常情况下，服务节点信息列表的更新频率是不高的，所以在服务消费者从注册中心获取到服务节点信息后完全可以缓存到本地，下次就不用请求注册中心，提高性能。

1. 增加本地缓存：用一个列表存储服务信息即可
2. 使用：优先从缓存获取服务，如果没有再从注册中心获取并设置到缓存中
3. 服务缓存更新-监听机制

使用Etcd的watch监听机制，当监听到某个key发生修改或删除时就会触发事件来通知监听者。

什么时候去创建watch监听器呢？缓存是在服务消费端维护和使用的，所以应该消费端去监听，在服务发现方法中对所有key进行监听



实现：

1. 注册中心添加监听key的方法
2. 实现类中增加正在监听key的集合，防止重复监听
3. 实现监听方法：如果出现删除key事件，则清理服务注册缓存
4. 在消费端获取服务时调用watch方法，对获取到的服务key进行监听



### 基于Zookeeper注册中心

和Etcd相似

1. 安装
2. 引入客户端依赖[Getting Started | Apache Curator](https://curator.apache.org/docs/getting-started)
3. 实现接口
4. SPI补充



扩展：

1. 完善服务注册信息，比如节点注册时间
2. 更多注册中心，比如redis
3. 保证注册中心高可用：Etcd集群
4. 服务注册信息失效兜底策略：如果消费端调用节点失败，从注册中心更新服务信息和本地缓存
5. 注册中心监听key时采用观察者模式处理：定义一个Listen接口，根据key的变更类型调用不同方法



## 自定义协议

自定义RPC协议分为两大核心部分：

自定义网络传输

自定义消息结构

### 网络传输设计

网络传输设计的目标时：选择一个能高性能通信的网络协议和传输方式

HTTP协议的头信息比较大，影响传输性能，HTTP是应用层协议，我们设计的RPC协议也是应用层协议。追求更高的性能应该使用传输层的TCP协议完成网络传输。

### 消息结构设计

消息结构设计目标是：用最少空间传输信息

分析HTTP请求结构，得到RPC消息所需的信息：

1. 魔数：安全校验，防止服务器处理非框架发来的信息
2. 版本号：保证请求和响应的一致性
3. 序列化方式：告诉服务端和客户端如何解析数据
4. 类型：标识请求还是响应
5. 状态：如果是响应，记录响应结果
6. id：唯一标识某个请求，用来追踪每个请求
7. 数据长度：解决半包粘包问题
8. 数据：数据

![](.\img\消息结构设计.jpg)



### 实现

#### 消息结构

1. 新建协议消息类：ProtocolMessage
2. 定义协议相关常量
3. 定义枚举类

#### 网络传输

之前使用的是Vert.x的HttpServer，现在使用Vert.x的TCP服务器

1. TCP服务器实现 VertxTcpServer
2. TCP客户端实现 VertxTcpClient

#### 编码解码器

Vert.x的TCP服务器收发的消息是Buffer类型，不能直接写入一个对象，因此需要编码解码器

1. 编码器 ProtocolMessageEncoder
2. 解码器 ProtocolMessageDecoder

#### 请求处理器

接受请求然后通过反射调用服务实现类

与HttpServerHandler类似，区别在于需要使用编码解码器

通过Vert.x提供的 `Handler<NetSocket>` 接口， 可以定义TCP请求处理器

#### 请求发送

将代理服务 ServiceProxy 的HTTP发送请求改为TCP请求



### 粘包半包问题

**半包**

收到的数据更少了

解决：在消息头设置请求体长度，服务端接收时，判断每次消息的长度是否符合预期，不完整就不读，留到下一次接收消息时再读

**粘包**

收到是数据更多了

解决：每次只读取指定长度的数据，超过长度的留着下一次接受到消息再读

**Vert.x解决半包和粘包**

使用内置的 RecordParser 完美解决半包粘包问题。记录解析器（Record Parser）允许您轻松解析由字节序列分割的协议，或者固定长度的协议。 它将输入缓冲区序列转换为按照配置重组后的缓冲区序列 （固定大小或带分隔符的记录）

实际使用中消息体的长度是不固定的，可以将读取完整的消息分为两步：

1. 读取消息的请求头，因为请求头信息长度是固定的
2. 根据请求头中数据长度信息来调整 RecoedParser的固定长度，保证完整读取请求体



## 负载均衡

### 分类

**二层负载均衡：**二层指数据链路层，数据以数据帧的形式通过交换机进行传输。这一层没有IP概念，通过MAC地址对机器进行区分。负载均衡服务器通过一个虚拟MAC地址接收请求，通过改写目标MAC地址的方式将请求转发到不同服务器

**三层负载均衡：**三层指网络层，对外提供一个虚拟的 IP 地址（VIP）以接收请求，然后根据算法将请求转发到 IP 地址不同的目标机器。

**四层负载均衡：**四层即传输层，除了包含三层的 IP 地址信息之外，还多了源目端口号的概念，可以区分同一机器上不同的应用。这一层的负载均衡会更加灵活，对外提供一个虚拟的 IP 地址 + 端口号来接收请求，然后根据算法将请求转发到不同目标机器的不同端口上。

四层负载均衡可以通过软件实现，比如主流且开源的 LVS（Linux Virtual Server），底层可选多种负载模式，比如 NAT（网络地址转换）、DR（直接路由）、TUN（隧道）。

四层负载均衡的优点是：一方面是性能很高、比较稳定，支撑个十几万、几十万并发不成问题；另一方面是成本低、纯软件实现，因此在企业中的应用很广泛。

**七层负载均衡：**七层指应用层，是计算机网络模型的最上层，因此能得到请求最为详细的信息，比如 HTTP 请求头等。可以根据域名或主机 IP + 端口接收请求，并通过应用层信息（请求头、Cookie 等）灵活地转发请求，比如将手机端用户转发到服务器 A、桌面端用户转发到服务器 B 等。这种方式实现成本最低，也最为灵活，因此也是我们应用开发人员最常用的了。

实现方式多种多样，比如主流的 Nginx、HAProxy 都可以，写个配置基本就能转发请求了，大部分情况下性能也够用了。

所属于这层的 `DNS 负载均衡` ，基于 DNS 域名解析服务，可以将同一个域名解析为不同的 IP 地址，从而让用户访问到不同服务器上的项目。这种方式实现起来不难，但转发逻辑不够灵活，而且 DNS 存在缓存，不利于修改。



### 负载均衡算法

轮询：适用于服务器性能相近的情况

随机：适用于服务器性能相近且负载均匀的情况

加权轮询：根据服务器的性能或权重分配请求，性能好的服务器会获得更多请求，适用于服务器性能不均的情况

加权随机：根据权重随机选择

最小连接数：选择当前连接数最少的服务器来处理请求，适用于长连接场景

IP Hash：根据客户端IP地址的哈希值选择服务器处理请求，确保同一客户端的请求始终被分配到同一台服务器，适用于需要保持会话一致性的场景



一致性Hash：将整个hash值空间划分为一个环状结构，每个节点或服务器在环上占据一个位置，每个请求根据hash值映射到环上一个点，然后顺时针寻找第一个大于等于该hash值的节点，将请求路由到该节点上。

解决了节点下线和倾斜问题

节点下线后，下线节点负责的请求交给顺时针下一个节点处理

倾斜问题：引入虚拟节点，将每个物理节点映射到多个虚拟节点，使得节点分布均匀，减少负载差异



### 支持配置和扩展负载均衡器

1. 定义常量
2. 使用工厂模式，支持根据key从SPI获取负载均衡对象实例
3. 编写SPI配置文件
4. 全局配置添加配置项



扩展：

1. 更多负载均衡器，比如最少活跃负载均衡器，最小连接数负载均衡器
2. 自定义一致性Hash算法中的Hash算法，比如使用IP计算Hash值，保证同IP请求发送给相同服务器