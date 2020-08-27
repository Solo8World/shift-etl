# SHIFT ETL`数据对接工具` 
> 用尽可能少的代码，做尽可能多的事
## 前言
这是一个相对简陋也非常轻量的ETL(Extract-Transform-Load)工具，
如果你有数据接口对接的业务场景，你可以参考它。  

它会以数据库配置的方式，帮助你实现数据接口对接，减少重复编码的工作量，
同时可以随时灵活调整、降低后续业务调整带来的影响。

它的核心代码只有三页,分别是
[EtlExtractService](https://github.com/Solo8World/shift-etl/blob/master/src/main/java/com/example/api/service/EtlExtractService.java) (数据抽取类)、
[EtlProcessService](https://github.com/Solo8World/shift-etl/blob/master/src/main/java/com/example/api/service/EtlProcessService.java) (数据转换类)、
[EtlLoadService](https://github.com/Solo8World/shift-etl/blob/master/src/main/java/com/example/api/service/EtlProcessService.java) (数据加载类)；



## 环境

* java 1.8+
* mysql5.6+

## 快速上手
LOADING...