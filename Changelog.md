
### v1.4.2(2020-11-19)

**Add**
- 适配FISCO BCOS java-sdk

**兼容性**
- WeBASE-Front v1.4.0+
- WeBASE-Transaction v1.3.0+

详细了解,请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。

### v1.4.1(2020-09-29)


**Fix**
- 更新gradlew版本
- 修复用户KeyStatus状态判断问题
- 修复用户分页的用户总数问题


**兼容性**
- WeBASE-Front v1.4.0+
- WeBASE-Transaction v1.3.0+

详细了解,请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。


### v1.4.0(2020-08-06)

**Add**
- 增加返回 Version 版本接口；

**Fix**
- 默认Aes加密模式由ECB改为更安全的CBC，同时支持在配置选择CBC与ECB


**兼容性**
- WeBASE-Front v1.4.0+
- WeBASE-Transaction v1.3.0+

详细了解,请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。



### v1.3.2(2020-06-17)

**Fix**
- 移除Fastjson，替换为Jackson 2.11.0; web3sdk升级为2.4.1
- 升级依赖包：spring: 4.3.27; log4j: 2.13.3; slf4j: 1.7.30; netty-all: 4.1.44+; guava: 29.0;

**兼容性**
- WeBASE-Front v1.3.0+
- WeBASE-Transaction v1.3.0+

详细了解，请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。


### v1.3.1

 (2020-06-01)
 
**Add**
- 新增导入私钥接口

**Fix**
- 增加私钥签名Credential缓存机制，优化签名性能

**兼容性**
- WeBASE-Front v1.3.0+
- WeBASE-Transaction v1.3.0+

详细了解，请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。


### v1.3.0

 (2020-04-29)
 
**Add**
- 同时支持ECDSA与国密私钥与签名与私钥创建(移除yaml配置文件中的`encryptType`)，可通过`encryptType`字段指定
- 修改用户entity的`int userId`为`String signUserId`，新增`String appId`
- 调用`/user/newUser`创建私钥时，需要传入`signUserId&appId`作为业务流水号；所有私钥与签名接口通过`signUserId`进行调用
- 新增停用私钥用户接口`DELETE /user/{signUseriId}`
- 新增根据appId获取用户分页列表接口`/user/list/{appId}/{pageNumber}/{pageSize}`

**Fix**
- 优化签名服务的性能
- 升级fastjson, jackson, log4j

**兼容性**
- WeBASE-Front v1.3.0+
- WeBASE-Transaction v1.3.0+

详细了解，请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。

### v1.2.2

 (2020-01-02)

**Add**

- 支持国密
- 新增`/encrypt`接口判断是否国密

**Fix**

- bugfix：CommonUtils的`SignatureData`序列化支持国密
- bugifx: 修复start.sh启动时间过长的问题
- 优化：web3sdk升级至v2.2.0

**兼容性**

- WeBASE-Front v1.2.2
- WeBASE-Transaction v1.2.2

详细了解，请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。


### v1.1.0

 (2019-09-12)

**Add**

- 查询用户列表

**Fix**

- bugfix：签名用户地址不一致
- 优化：通过用户编号查询公私钥信息
- 优化：启停脚本通过程序名和端口校验进程

**兼容性**

- WeBASE-Front v1.1.0
- WeBASE-Transaction v1.1.0

详细了解，请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)。



### v1.0.0

(2019-06-27)

WeBASE-Sign（微众区块链中间件平台-签名子系统），主要提供公私钥管理及数据签名功能。

**Add**

- 适配FISCO-BCOS 2.0.0版本
