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
