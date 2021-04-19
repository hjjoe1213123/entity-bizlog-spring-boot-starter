# 工程简介

一个OLTP数据库JPA日志拦截记录的springboot框架

# 开发计划

1. 2021/04/15  1.0.1 实现日式文件打印变更 --已实现
2. 2021/05/05  1.0.2 实现NoSQL数据库存储变更记录 --实现中

#快速开始
### 1、pom添加
```xml
<dependency>
  <groupId>cn.idugou.bizlog</groupId>
  <artifactId>entity-bizlog-spring-boot-starter</artifactId>
  <version>1.0.1</version>
</dependency>
```
### 2、实体类添加相应注解
````java
@Data
@Accessors(chain = true)
@EntityListeners(EntityLogListener.class) //必要
@Entity //必要
@Table(name = "sms_topic_user") //必要
public class SmsTopicUser implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private Date createTime;

    private Date updateTime;

    private Long topicId;

    private Long umsUserId;

    private String queueCode;
}
````
### 3、操作方法添加注解
````java
@Service
public class SmsTopicUserServiceImpl implements SmsTopicUserService {
    @EntityLog("新增") //必要
    @Override
    @Transactional
    public void save() {
        SmsTopicUser topicUser = new SmsTopicUser();
        topicUser.setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setQueueCode("ABCDEFG")
                .setTopicId(1L)
                .setUmsUserId(1L);
        smsTopicUserRepository.save(topicUser);

        System.out.println(topicUser.toString());
    }
}
````

### 4、启动类添加注解
````java
@EnableJpaRepositories(basePackages = {"com.example.bizlogtest.dao"}) //必要
@EntityScan(basePackages = {"com.example.bizlogtest.entity"}) //必要
@EnableJpaAuditing //必要
@EnableEntitylog //必要
@SpringBootApplication
public class BizlogtestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizlogtestApplication.class, args);
    }

}
````
#效果展示
```
2021-04-15 19:38:48.420  INFO 15584 --- [nio-8080-exec-1] c.i.bizlog.listener.EntityLogService     : EntityLogDTO(tableName=sms_topic_user, tableDesc=sms_topic_user, value=新增, ip=127.0.0.1, operateTime=Thu Apr 15 19:38:48 CST 2021, sessionId=15b4b203-cf21-4797-a51a-2ebfde78e1d2, logType=INSERT, userId=null, username=null, nickname=null, rowId=1, version=1, oldValue=null, newValue=SmsTopicUser(id=1, version=0, createTime=Thu Apr 15 19:38:48 CST 2021, updateTime=Thu Apr 15 19:38:48 CST 2021, topicId=1, umsUserId=1, queueCode=ABCDEFG), columnLogMap=null)
```