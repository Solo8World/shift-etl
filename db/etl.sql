/*
 Navicat Premium Data Transfer

 Source Server         : shift
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : rm-2ze93742pnkanoxpx4o.mysql.rds.aliyuncs.com:3306
 Source Schema         : etl

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 13/10/2020 23:11:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for demo_wenku_book
-- ----------------------------
DROP TABLE IF EXISTS `demo_wenku_book`;
CREATE TABLE `demo_wenku_book` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `show_doc_id` varchar(64) NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `summary` text NOT NULL,
  `price` bigint(10) NOT NULL,
  `original_price` bigint(10) NOT NULL,
  `view_count` int(64) NOT NULL,
  `cover_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for etl_data_process_mapping
-- ----------------------------
DROP TABLE IF EXISTS `etl_data_process_mapping`;
CREATE TABLE `etl_data_process_mapping` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `result_field` varchar(38) DEFAULT NULL COMMENT '查询字段',
  `mapping_field` varchar(38) DEFAULT NULL COMMENT '条件字段',
  `mapping_table` varchar(38) DEFAULT NULL COMMENT '表名',
  `pretreatment` smallint(6) DEFAULT NULL COMMENT '是否预处理',
  `pretreatment_range` longtext COMMENT '预处理数据范围',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='etl映射表字段值设置';

-- ----------------------------
-- Records of etl_data_process_mapping
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for etl_data_process_rule
-- ----------------------------
DROP TABLE IF EXISTS `etl_data_process_rule`;
CREATE TABLE `etl_data_process_rule` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `source_key` varchar(38) NOT NULL COMMENT '来源标识',
  `data_key` varchar(128) NOT NULL COMMENT '源数据键名',
  `has_mapping` smallint(6) DEFAULT NULL COMMENT '是否需联查信息',
  `mapping_id` bigint(20) DEFAULT NULL COMMENT '联查设置关联主键',
  `has_distinct` smallint(6) DEFAULT NULL COMMENT '是否需以此字段去重',
  `target_table` varchar(64) NOT NULL COMMENT '对应表/beanName',
  `target_columns` varchar(32) NOT NULL COMMENT '对应表字段',
  `default_value` varchar(255) DEFAULT NULL COMMENT '默认值',
  `exec_sort` int(11) NOT NULL COMMENT '执行顺序',
  `exec_type` varchar(32) NOT NULL COMMENT '执行类型(insert,replace,update,delete,insert or update,java)',
  `is_condition` smallint(6) DEFAULT NULL COMMENT '是否为条件字段',
  `condition_symbol` varchar(32) DEFAULT NULL COMMENT '条件字符(=,<,<=)/java函数参数类型(String/Integer)',
  `remark` varchar(32) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='etl源数据处理规则';

-- ----------------------------
-- Records of etl_data_process_rule
-- ----------------------------
BEGIN;
INSERT INTO `etl_data_process_rule` VALUES (1, 'wenku-book', 'data.list[].show_doc_id', 0, NULL, 0, 'demo_wenku_book', 'show_doc_id', NULL, 1, 'insert', 0, '', '展示文档主键');
INSERT INTO `etl_data_process_rule` VALUES (2, 'wenku-book', 'data.list[].title', 0, NULL, 0, 'demo_wenku_book', 'title', NULL, 1, 'insert', 0, '', '书籍名称');
INSERT INTO `etl_data_process_rule` VALUES (3, 'wenku-book', 'data.list[].author', 0, NULL, 0, 'demo_wenku_book', 'author', NULL, 1, 'insert', 0, '', '书籍作者');
INSERT INTO `etl_data_process_rule` VALUES (4, 'wenku-book', 'data.list[].summary', 0, NULL, 0, 'demo_wenku_book', 'summary', NULL, 1, 'insert', 0, '', '简介');
INSERT INTO `etl_data_process_rule` VALUES (5, 'wenku-book', 'data.list[].price', 0, NULL, 0, 'demo_wenku_book', 'price', NULL, 1, 'insert', 0, '', '价格');
INSERT INTO `etl_data_process_rule` VALUES (6, 'wenku-book', 'data.list[].original_price', 0, NULL, 0, 'demo_wenku_book', 'original_price', NULL, 1, 'insert', 0, '', '原价');
INSERT INTO `etl_data_process_rule` VALUES (7, 'wenku-book', 'data.list[].view_count', 0, NULL, 0, 'demo_wenku_book', 'view_count', NULL, 1, 'insert', 0, '', '浏览量');
INSERT INTO `etl_data_process_rule` VALUES (8, 'wenku-book', 'data.list[]. cover_url', 0, NULL, 0, 'demo_wenku_book', ' cover_url', NULL, 1, 'insert', 0, '', '图片地址');
COMMIT;

-- ----------------------------
-- Table structure for etl_source
-- ----------------------------
DROP TABLE IF EXISTS `etl_source`;
CREATE TABLE `etl_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '数据源信息主键',
  `source_key` varchar(32) DEFAULT NULL COMMENT '数据源标识',
  `source_url` varchar(128) DEFAULT NULL COMMENT '数据源路径',
  `request_method` varchar(24) DEFAULT NULL COMMENT '请求方式GET POST',
  `content_type` smallint(6) DEFAULT NULL COMMENT '参数传输格式0:form,1:json,2:urlencoded',
  `process_method` int(11) DEFAULT NULL COMMENT '源数据处理方式0:同步处理,1:异步处理,2:自定义处理',
  `remark` varchar(32) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='etl数据源信息';

-- ----------------------------
-- Records of etl_source
-- ----------------------------
BEGIN;
INSERT INTO `etl_source` VALUES (1, 'wenku-book', 'https://wenku.baidu.com/portal/interface/indexvipbook2020', 'GET', 0, 0, '百度文库书籍数据源');
COMMIT;

-- ----------------------------
-- Table structure for etl_source_data
-- ----------------------------
DROP TABLE IF EXISTS `etl_source_data`;
CREATE TABLE `etl_source_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `source_key` varchar(38) DEFAULT NULL COMMENT '数据源key',
  `source_data` longtext COMMENT '源数据',
  `processing_status` int(11) DEFAULT NULL COMMENT '处理状态(0-等待处理  1.处理中,2-处理完成 3-处理失败)',
  `action_date` datetime DEFAULT NULL COMMENT '开始处理时间',
  `process_time` int(11) DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `process_result` longtext COMMENT '处理结果',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='etl源数据';

-- ----------------------------
-- Records of etl_source_data
-- ----------------------------
BEGIN;
INSERT INTO `etl_source_data` VALUES (6, 'wenku-book', '{\"data\":{\"mysub\":[{\"cname\":\"经济管理\",\"jump_url\":\"https://yuedu.baidu.com/book/list/1\",\"cid1\":1},{\"cname\":\"成功励志\",\"jump_url\":\"https://yuedu.baidu.com/book/list/2\",\"cid1\":2},{\"cname\":\"计算机\",\"jump_url\":\"https://yuedu.baidu.com/book/list/3\",\"cid1\":3},{\"cname\":\"社会科学\",\"jump_url\":\"https://yuedu.baidu.com/book/list/4\",\"cid1\":4},{\"cname\":\"历史传记\",\"jump_url\":\"https://yuedu.baidu.com/book/list/15\",\"cid1\":15}],\"list\":[{\"summary\":\"本书系由《华尔街日报》很有名的记者根据连续采访整理而成，以拉瑞·利文斯顿的人称撰写，记述了这位20世纪20年代华尔街的风云人物在股票交易方面的传奇经历和感悟，探讨了股票市场的本质、股票交易的原则以及参与各方的心理与策略。\\r\\n出版90多年来，本书一直是金融从业人员以及股市参与者必读的经典作品之一。\",\"original_price\":\"14.00\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/63d0f703918fa0ec902cdc8c2c9759ee3c6ddb4c.jpg\",\"author\":\"[美]埃德文·拉斐尔\",\"title\":\"股票大作手回忆录（超值畅销版）\",\"show_doc_id\":\"f6c6511e182e453610661ed9ad51f01dc381574c\",\"price\":\"14.00\",\"view_count\":30034},{\"summary\":\"是什么让事物变得流行？从买轿车、买衣服、吃三明治，到给孩子取名字，你是否知道为什么某些产品会大卖，某些故事被人们口口相传，某些电子邮件更易被转发，或者某些视频链接被疯狂地点击，某些谣言更具传播力，某些思想和行为像病毒一样入侵你的大脑……\\r\\n这本书将为你揭示这些口口相传和社会传播背后的科学秘密，并且告诉你如何将产品、思想、行为设计成具有感染力和传播力的内容。无论你是大公司的管理者，还是努力提高公司知名度的小企业主，无论你是官员或政客，还是非营利性组织的工作者，只要你想传递信息，就请翻翻这本书。宾夕法尼亚大学沃顿商学院的市场营销学教授乔纳·伯杰（JonahBerger）通过多年的调查和实验研究，将在这本书里以故事讲述的方式告诉你让所有类型的产品、思想、行为疯狂传播的科学方法。\",\"original_price\":\"9.60\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/cb8065380cd79123ac99891caa345982b2b78058.jpg\",\"author\":\"刘生敏\",\"title\":\"疯传：让你的产品、思想、行为像病毒一样入侵\",\"show_doc_id\":\"f994d05eb9d528ea80c779bc\",\"price\":\"9.60\",\"view_count\":27931},{\"summary\":\"《股票大作手操盘术：融合时间和价格的利弗莫尔准则》由华尔街传奇股票操盘手杰西·利弗莫尔亲笔所著，系统介绍了他自己独创的基于时间与价格两大要素的股市交易法则，配有他具体操盘的详细记录。此外，译者还结合当下股市的交易模式，将利弗莫尔时代的纸带记录数据转化为了现代股市交易图表，使利弗莫尔的交易思想更加易于被理解和掌握。\\r\\n《股票大作手操盘术：融合时间和价格的利弗莫尔准则》适合对股票投资和技术分析感兴趣的读者参考阅读。\",\"original_price\":\"17.50\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/f603918fa0ec08fa014ff8035bee3d6d54fbdac3.jpg\",\"author\":\"[美]杰西·利弗莫尔\",\"title\":\"股票大作手操盘术：融合时间和价格的利弗莫尔准则\",\"show_doc_id\":\"3d651d6f783e0912a2162a52\",\"price\":\"17.50\",\"view_count\":26959},{\"summary\":\"波动市况下的盈利法则，在我国目前的资本市场环境下，大多数投资者都是以做短线交易为主，而超级短线凭借其周期短、对市场波动适应性高等特点，更是深受广大股民喜爱。\",\"original_price\":\"15.60\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/95eef01f3a292df57f1b7566b0315c6035a873ed.jpg\",\"author\":\"老牛\",\"title\":\"股票操盘手之超级短线交易手册\",\"show_doc_id\":\"eee8d52bfd4ffe4733687e21af45b307e871f9e9\",\"price\":\"15.60\",\"view_count\":25003},{\"summary\":\"本书的写作初衷是写一本让大家能看懂的、干货满满的、注重实战而不是理论知识和教条堆砌的指数投资书籍。对于很多上班族而言，如果平常几乎没有研究股票和基金的时间和精力，那么定投指数基金将会是一个不错的投资方式，这类投资者可以好好看看老罗关于定投章节的内容，从中可以学习到定投的优势、定投金额、定投品种以及止盈的方式等相关知识。对于爱好学习以及时间充裕的投资者来说，可以通过学习本书，掌握如何挑选一只好的指数基金，学会运用指数估值数据选择指数基金，并且在不同市场环境下合理运用不同策略投资指数基金，了解指数基金产品进行低风险套利的方法，最终通过学会更多的指数投资相关知识，来帮助自己更好地进行投资理财。\",\"original_price\":\"35.89\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/5ab5c9ea15ce36d3df1ef42537f33a87e950b188.jpg\",\"author\":\"老罗\",\"title\":\"指数基金投资从入门到精通\",\"show_doc_id\":\"c9e1acce294ac850ad02de80d4d8d15abe2300c4\",\"price\":\"35.89\",\"view_count\":24560},{\"summary\":\"一本人人都看得懂、学得会、用得上的基金投资实战指南。从认识基金，到选择基金，再到持有基金、赎回基金、转换基金，作者详细介绍了各类基金的基本知识、操作技巧及风险防范措施，大到基金组合的设计，小到买卖基金省钱、省时的窍门，书中都有详尽的介绍。另外，每一章最后的基金投资实例和明星基金经理操盘实录，可以帮助投资者将本章知识融会贯通，身临其境般地体验基金投资的魅力。\",\"original_price\":\"21.20\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/e61190ef76c6a7ef90a4ba3ffafaaf51f3de662c.jpg\",\"author\":\"老牛\",\"title\":\"基金投资从入门到精通\",\"show_doc_id\":\"feb52a1a195f312b3069a573\",\"price\":\"21.20\",\"view_count\":24413},{\"summary\":\"本书围绕趋势研判、图表研究、买卖点精准把握、未来股价预测和选股这条主线，展示出一套全新的并在实战中已经得到证实的操盘思维模式和完善的交易理念。本书共分8章，内容以实战操盘为重点，全方位剖析技术形态背后的原因，包括主力行为密码、狙击暴涨股技术、特殊形态的处理、白马股精选等。本书适合各类股民阅读。\",\"original_price\":\"19.93\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/279759ee3d6d55fb270fd7f661224f4a21a4dd11.jpg\",\"author\":\"郭浩\",\"title\":\"股票操盘手实战技法\",\"show_doc_id\":\"6c811b60a4e9856a561252d380eb6294dc882254\",\"price\":\"19.93\",\"view_count\":24179},{\"summary\":\"《销售全攻略：销售技巧全程训练》全面解读销售过程中销售员可能面临的难题详细解答应对技巧和方法让销售指导更全面销售应对更有效使销售员对销售全局有更准确的把控能力销售工作更加得心应手。\",\"original_price\":\"23.40\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/4ec2d5628535e5dd036a31d475c6a7efce1b6273.jpg\",\"author\":\"李俊彦;陈亮\",\"title\":\"销售全攻略：销售技巧全程训练\",\"show_doc_id\":\"b680809e910ef12d2af9e766\",\"price\":\"23.40\",\"view_count\":23958},{\"summary\":\"零基础、全图解，以通俗易懂的实战步骤的方式详细讲解了用电脑、手机炒股的步骤、方法和技巧以及进行股票投资基本面分析、K线分析、移动平均线和成交量分析的方法，同时介绍了安装多款炒股软件进行实时查看与分析股市信息，掌握股票买卖技巧、风险防范的方法和技巧，实现时时、处处掌控股票投资，实现资金的增值。\",\"original_price\":\"15.92\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/8b13632762d0f703928383c50ffa513d2697c5f1.jpg\",\"author\":\"马涛\",\"title\":\"从零开始学炒股：股票入门与实战（全彩图解版）\",\"show_doc_id\":\"dab47ae6cfc789eb162dc884\",\"price\":\"15.92\",\"view_count\":23835},{\"summary\":\"本书全面阐释了马云对电子商务、数据时代、未来技术以及社会变革的思考和畅想，详细解读了阿里巴巴未来十年乃至未来三十年的战略规划和发展前景。宏观上，马云对未来十年互联网的发展作出大胆预判和展望，具有前瞻性和洞察性，对于互联网从业者有指导意义。马云宣告DT时代即将到来，eWTP平台必将建立，一场电子商务的革命即将展开。这种新概念与新提法是开创性的。他对企业与个人在未来十年的机遇和陷阱，一一提出对策，具有实战性。马云把过去十几年自己在创业、管理和处世上的人生哲学做了全面的总结回顾。除了阿里巴巴的发展前景，马云还对未来十年中国乃至世界的产品升级、产业升级和人的智慧升级做出了全面分析和惊人预测。在展望未来之余，马云还多维地分享了他关于阿里巴巴上市、公益活动、女性权益、环境保护、农村脱贫、打击假货、乡村教师、食品安全等社会热点话题的观点和看法。\",\"original_price\":\"17.98\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/79f0f736afc37931ea0cbc2ee1c4b74543a91154.jpg\",\"author\":\"阿里巴巴集团\",\"title\":\"马云：未来已来\",\"show_doc_id\":\"20d8d265f342336c1eb91a37f111f18582d00c57\",\"price\":\"17.98\",\"view_count\":23675},{\"summary\":\"量价分析，其核心是“分析”。提高分析水平，掌握分析技巧，才能提高实战成功率。《从零开始学量价分析短线操盘盘口分析与A股买卖点实战》以此为目标，通过基础学习、模式强化、趋势解读、主力控盘、量价形态、黑马股启动等多个方面，全方位地展现了量价交易技术。\\r\\n作者结合多年股市经验，从趋势、主力、日K线图、分时图、黑马股等角度讲解了百种量价形态，根据A股走势进行实战套用。末尾一章“每笔均量”是较为特殊的技术分析指标，它也是发现主力动向、捕获黑马股的重要工具，但鲜为人知，因其成功率较高，应引起我们的关注。\\r\\n本书以实战讲解为核心，内容由浅入深、层层深入，既为投资者入门做好了铺垫，也将多变的股市实战做了深层的阐述，是一本入门、进阶的实用性操作手册。\",\"original_price\":\"19.93\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/241f95cad1c8a786ecfdf67b6b09c93d71cf50eb.jpg\",\"author\":\"杨金\",\"title\":\"从零开始学量价分析：短线操盘、盘口分析与A股买卖点实战\",\"show_doc_id\":\"60ba2830b94ae45c3b3567ec102de2bd9705de5c\",\"price\":\"19.93\",\"view_count\":23552},{\"summary\":\"本书作者用一个简单的公式揭示了股价涨跌的真正原因，并用具体股票的历史数据对公式进行了验证，同时作者还结合自身的实践讲解了如何从企业的年报和财务报表中发掘重要的投资信息，并将这些信息与公式相结合去发掘适合价值投资的股票。\\r\\n本书适合处于亏损状态想摆脱困境去盈利的股民和即将步入股票市场的投资者阅读参考。\",\"original_price\":\"11.20\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/8c1001e93901213fe7ef6f415ee736d12e2e954a.jpg\",\"author\":\"汪远\",\"title\":\"股票投资真理\",\"show_doc_id\":\"5f7cc829793e0912a21614791711cc7931b778f3\",\"price\":\"11.20\",\"view_count\":23269}]},\"status\":{\"msg\":null,\"code\":0}}', 3, '2020-10-10 17:09:31', 2062, 'org.springframework.dao.DataIntegrityViolationException: \n### Error updating database.  Cause: com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long for column \'summary\' at row 2\n### The error may exist in file [/home/lizhuo/IdeaProjects/shift-etl/target/classes/mapper/EtlLoadMapper.xml]\n### The error may involve defaultParameterMap\n### The error occurred while setting parameters\n### SQL: insert into demo_wenku_book          (               summary          ,              original_price          ,               cover_url          ,              author          ,              price          ,              title          ,              show_doc_id          ,              view_count          )          values                         (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )                         , (                   ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              ,                  ?              )\n### Cause: com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long for column \'summary\' at row 2\n; Data truncation: Data too long for column \'summary\' at row 2; nested exception is com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long for column \'summary\' at row 2', '2020-10-10 17:21:14', '2020-10-10 17:21:18');
INSERT INTO `etl_source_data` VALUES (7, 'wenku-book', '{\"data\":{\"mysub\":[{\"cname\":\"经济管理\",\"jump_url\":\"https://yuedu.baidu.com/book/list/1\",\"cid1\":1},{\"cname\":\"成功励志\",\"jump_url\":\"https://yuedu.baidu.com/book/list/2\",\"cid1\":2},{\"cname\":\"计算机\",\"jump_url\":\"https://yuedu.baidu.com/book/list/3\",\"cid1\":3},{\"cname\":\"社会科学\",\"jump_url\":\"https://yuedu.baidu.com/book/list/4\",\"cid1\":4},{\"cname\":\"历史传记\",\"jump_url\":\"https://yuedu.baidu.com/book/list/15\",\"cid1\":15}],\"list\":[{\"summary\":\"本书系由《华尔街日报》很有名的记者根据连续采访整理而成，以拉瑞·利文斯顿的人称撰写，记述了这位20世纪20年代华尔街的风云人物在股票交易方面的传奇经历和感悟，探讨了股票市场的本质、股票交易的原则以及参与各方的心理与策略。\\r\\n出版90多年来，本书一直是金融从业人员以及股市参与者必读的经典作品之一。\",\"original_price\":\"14.00\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/63d0f703918fa0ec902cdc8c2c9759ee3c6ddb4c.jpg\",\"author\":\"[美]埃德文·拉斐尔\",\"title\":\"股票大作手回忆录（超值畅销版）\",\"show_doc_id\":\"f6c6511e182e453610661ed9ad51f01dc381574c\",\"price\":\"14.00\",\"view_count\":30034},{\"summary\":\"是什么让事物变得流行？从买轿车、买衣服、吃三明治，到给孩子取名字，你是否知道为什么某些产品会大卖，某些故事被人们口口相传，某些电子邮件更易被转发，或者某些视频链接被疯狂地点击，某些谣言更具传播力，某些思想和行为像病毒一样入侵你的大脑……\\r\\n这本书将为你揭示这些口口相传和社会传播背后的科学秘密，并且告诉你如何将产品、思想、行为设计成具有感染力和传播力的内容。无论你是大公司的管理者，还是努力提高公司知名度的小企业主，无论你是官员或政客，还是非营利性组织的工作者，只要你想传递信息，就请翻翻这本书。宾夕法尼亚大学沃顿商学院的市场营销学教授乔纳·伯杰（JonahBerger）通过多年的调查和实验研究，将在这本书里以故事讲述的方式告诉你让所有类型的产品、思想、行为疯狂传播的科学方法。\",\"original_price\":\"9.60\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/cb8065380cd79123ac99891caa345982b2b78058.jpg\",\"author\":\"刘生敏\",\"title\":\"疯传：让你的产品、思想、行为像病毒一样入侵\",\"show_doc_id\":\"f994d05eb9d528ea80c779bc\",\"price\":\"9.60\",\"view_count\":27931},{\"summary\":\"《股票大作手操盘术：融合时间和价格的利弗莫尔准则》由华尔街传奇股票操盘手杰西·利弗莫尔亲笔所著，系统介绍了他自己独创的基于时间与价格两大要素的股市交易法则，配有他具体操盘的详细记录。此外，译者还结合当下股市的交易模式，将利弗莫尔时代的纸带记录数据转化为了现代股市交易图表，使利弗莫尔的交易思想更加易于被理解和掌握。\\r\\n《股票大作手操盘术：融合时间和价格的利弗莫尔准则》适合对股票投资和技术分析感兴趣的读者参考阅读。\",\"original_price\":\"17.50\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/f603918fa0ec08fa014ff8035bee3d6d54fbdac3.jpg\",\"author\":\"[美]杰西·利弗莫尔\",\"title\":\"股票大作手操盘术：融合时间和价格的利弗莫尔准则\",\"show_doc_id\":\"3d651d6f783e0912a2162a52\",\"price\":\"17.50\",\"view_count\":26959},{\"summary\":\"波动市况下的盈利法则，在我国目前的资本市场环境下，大多数投资者都是以做短线交易为主，而超级短线凭借其周期短、对市场波动适应性高等特点，更是深受广大股民喜爱。\",\"original_price\":\"15.60\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/95eef01f3a292df57f1b7566b0315c6035a873ed.jpg\",\"author\":\"老牛\",\"title\":\"股票操盘手之超级短线交易手册\",\"show_doc_id\":\"eee8d52bfd4ffe4733687e21af45b307e871f9e9\",\"price\":\"15.60\",\"view_count\":25003},{\"summary\":\"本书的写作初衷是写一本让大家能看懂的、干货满满的、注重实战而不是理论知识和教条堆砌的指数投资书籍。对于很多上班族而言，如果平常几乎没有研究股票和基金的时间和精力，那么定投指数基金将会是一个不错的投资方式，这类投资者可以好好看看老罗关于定投章节的内容，从中可以学习到定投的优势、定投金额、定投品种以及止盈的方式等相关知识。对于爱好学习以及时间充裕的投资者来说，可以通过学习本书，掌握如何挑选一只好的指数基金，学会运用指数估值数据选择指数基金，并且在不同市场环境下合理运用不同策略投资指数基金，了解指数基金产品进行低风险套利的方法，最终通过学会更多的指数投资相关知识，来帮助自己更好地进行投资理财。\",\"original_price\":\"35.89\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/5ab5c9ea15ce36d3df1ef42537f33a87e950b188.jpg\",\"author\":\"老罗\",\"title\":\"指数基金投资从入门到精通\",\"show_doc_id\":\"c9e1acce294ac850ad02de80d4d8d15abe2300c4\",\"price\":\"35.89\",\"view_count\":24560},{\"summary\":\"一本人人都看得懂、学得会、用得上的基金投资实战指南。从认识基金，到选择基金，再到持有基金、赎回基金、转换基金，作者详细介绍了各类基金的基本知识、操作技巧及风险防范措施，大到基金组合的设计，小到买卖基金省钱、省时的窍门，书中都有详尽的介绍。另外，每一章最后的基金投资实例和明星基金经理操盘实录，可以帮助投资者将本章知识融会贯通，身临其境般地体验基金投资的魅力。\",\"original_price\":\"21.20\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/e61190ef76c6a7ef90a4ba3ffafaaf51f3de662c.jpg\",\"author\":\"老牛\",\"title\":\"基金投资从入门到精通\",\"show_doc_id\":\"feb52a1a195f312b3069a573\",\"price\":\"21.20\",\"view_count\":24413},{\"summary\":\"本书围绕趋势研判、图表研究、买卖点精准把握、未来股价预测和选股这条主线，展示出一套全新的并在实战中已经得到证实的操盘思维模式和完善的交易理念。本书共分8章，内容以实战操盘为重点，全方位剖析技术形态背后的原因，包括主力行为密码、狙击暴涨股技术、特殊形态的处理、白马股精选等。本书适合各类股民阅读。\",\"original_price\":\"19.93\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/279759ee3d6d55fb270fd7f661224f4a21a4dd11.jpg\",\"author\":\"郭浩\",\"title\":\"股票操盘手实战技法\",\"show_doc_id\":\"6c811b60a4e9856a561252d380eb6294dc882254\",\"price\":\"19.93\",\"view_count\":24179},{\"summary\":\"《销售全攻略：销售技巧全程训练》全面解读销售过程中销售员可能面临的难题详细解答应对技巧和方法让销售指导更全面销售应对更有效使销售员对销售全局有更准确的把控能力销售工作更加得心应手。\",\"original_price\":\"23.40\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/4ec2d5628535e5dd036a31d475c6a7efce1b6273.jpg\",\"author\":\"李俊彦;陈亮\",\"title\":\"销售全攻略：销售技巧全程训练\",\"show_doc_id\":\"b680809e910ef12d2af9e766\",\"price\":\"23.40\",\"view_count\":23958},{\"summary\":\"零基础、全图解，以通俗易懂的实战步骤的方式详细讲解了用电脑、手机炒股的步骤、方法和技巧以及进行股票投资基本面分析、K线分析、移动平均线和成交量分析的方法，同时介绍了安装多款炒股软件进行实时查看与分析股市信息，掌握股票买卖技巧、风险防范的方法和技巧，实现时时、处处掌控股票投资，实现资金的增值。\",\"original_price\":\"15.92\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/8b13632762d0f703928383c50ffa513d2697c5f1.jpg\",\"author\":\"马涛\",\"title\":\"从零开始学炒股：股票入门与实战（全彩图解版）\",\"show_doc_id\":\"dab47ae6cfc789eb162dc884\",\"price\":\"15.92\",\"view_count\":23835},{\"summary\":\"本书全面阐释了马云对电子商务、数据时代、未来技术以及社会变革的思考和畅想，详细解读了阿里巴巴未来十年乃至未来三十年的战略规划和发展前景。宏观上，马云对未来十年互联网的发展作出大胆预判和展望，具有前瞻性和洞察性，对于互联网从业者有指导意义。马云宣告DT时代即将到来，eWTP平台必将建立，一场电子商务的革命即将展开。这种新概念与新提法是开创性的。他对企业与个人在未来十年的机遇和陷阱，一一提出对策，具有实战性。马云把过去十几年自己在创业、管理和处世上的人生哲学做了全面的总结回顾。除了阿里巴巴的发展前景，马云还对未来十年中国乃至世界的产品升级、产业升级和人的智慧升级做出了全面分析和惊人预测。在展望未来之余，马云还多维地分享了他关于阿里巴巴上市、公益活动、女性权益、环境保护、农村脱贫、打击假货、乡村教师、食品安全等社会热点话题的观点和看法。\",\"original_price\":\"17.98\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/79f0f736afc37931ea0cbc2ee1c4b74543a91154.jpg\",\"author\":\"阿里巴巴集团\",\"title\":\"马云：未来已来\",\"show_doc_id\":\"20d8d265f342336c1eb91a37f111f18582d00c57\",\"price\":\"17.98\",\"view_count\":23675},{\"summary\":\"量价分析，其核心是“分析”。提高分析水平，掌握分析技巧，才能提高实战成功率。《从零开始学量价分析短线操盘盘口分析与A股买卖点实战》以此为目标，通过基础学习、模式强化、趋势解读、主力控盘、量价形态、黑马股启动等多个方面，全方位地展现了量价交易技术。\\r\\n作者结合多年股市经验，从趋势、主力、日K线图、分时图、黑马股等角度讲解了百种量价形态，根据A股走势进行实战套用。末尾一章“每笔均量”是较为特殊的技术分析指标，它也是发现主力动向、捕获黑马股的重要工具，但鲜为人知，因其成功率较高，应引起我们的关注。\\r\\n本书以实战讲解为核心，内容由浅入深、层层深入，既为投资者入门做好了铺垫，也将多变的股市实战做了深层的阐述，是一本入门、进阶的实用性操作手册。\",\"original_price\":\"19.93\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/241f95cad1c8a786ecfdf67b6b09c93d71cf50eb.jpg\",\"author\":\"杨金\",\"title\":\"从零开始学量价分析：短线操盘、盘口分析与A股买卖点实战\",\"show_doc_id\":\"60ba2830b94ae45c3b3567ec102de2bd9705de5c\",\"price\":\"19.93\",\"view_count\":23552},{\"summary\":\"本书作者用一个简单的公式揭示了股价涨跌的真正原因，并用具体股票的历史数据对公式进行了验证，同时作者还结合自身的实践讲解了如何从企业的年报和财务报表中发掘重要的投资信息，并将这些信息与公式相结合去发掘适合价值投资的股票。\\r\\n本书适合处于亏损状态想摆脱困境去盈利的股民和即将步入股票市场的投资者阅读参考。\",\"original_price\":\"11.20\",\"cover_url\":\"https://wkphoto.cdn.bcebos.com/8c1001e93901213fe7ef6f415ee736d12e2e954a.jpg\",\"author\":\"汪远\",\"title\":\"股票投资真理\",\"show_doc_id\":\"5f7cc829793e0912a21614791711cc7931b778f3\",\"price\":\"11.20\",\"view_count\":23269}]},\"status\":{\"msg\":null,\"code\":0}}', 2, '2020-10-10 17:10:53', 1700, '处理完成', '2020-10-10 17:21:21', '2020-10-10 17:21:23');
COMMIT;

-- ----------------------------
-- Table structure for etl_source_param
-- ----------------------------
DROP TABLE IF EXISTS `etl_source_param`;
CREATE TABLE `etl_source_param` (
  `value` varchar(54) NOT NULL COMMENT '值',
  `code` varchar(16) DEFAULT NULL COMMENT '编码',
  `type` varchar(16) DEFAULT NULL COMMENT '类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='etl数据源动态参数';

-- ----------------------------
-- Records of etl_source_param
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for etl_source_valid
-- ----------------------------
DROP TABLE IF EXISTS `etl_source_valid`;
CREATE TABLE `etl_source_valid` (
  `id` int(11) NOT NULL COMMENT '主键',
  `source_id` int(11) NOT NULL COMMENT '接口主键',
  `code_key` varchar(32) NOT NULL COMMENT '请求状态字段',
  `success_code` varchar(32) DEFAULT NULL COMMENT '成功状态码',
  `msg_key` varchar(32) DEFAULT NULL COMMENT '请求信息字段',
  `data_key` varchar(32) DEFAULT NULL COMMENT '请求数据字段',
  `remark` varchar(32) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='etl数据源返回数据有效性验证规则';

-- ----------------------------
-- Records of etl_source_valid
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
