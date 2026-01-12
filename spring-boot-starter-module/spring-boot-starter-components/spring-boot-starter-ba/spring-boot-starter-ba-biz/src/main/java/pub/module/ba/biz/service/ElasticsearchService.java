package pub.module.ba.biz.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;
import pub.module.ba.biz.dto.BaAppBasicDto;
import pub.module.web.vo.Result;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class ElasticsearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public Result<?> esNode(BaAppBasicDto baAppBasicDto) {

        //2.构建请求
        IndexRequest request = new IndexRequest("ba_app");//TODO 索引名
        request.id(IdWorker.getIdStr());

        JSONObject jsonObject = new JSONObject();

        jsonObject.set("baAppCode", baAppBasicDto.getBaAppCode());
        jsonObject.set("baAppName", baAppBasicDto.getBaAppName());
        jsonObject.set("userCode", baAppBasicDto.getUserCode());
        jsonObject.set("ip", baAppBasicDto.getIp());
        jsonObject.set("osName", baAppBasicDto.getOsName());
        jsonObject.set("pageName", baAppBasicDto.getPageName());
        jsonObject.set("pageCode", baAppBasicDto.getPageCode());
        jsonObject.set("area", baAppBasicDto.getArea());
        jsonObject.set("dwellTime", baAppBasicDto.getDwellTime());
        jsonObject.set("sourceName", baAppBasicDto.getSourceName());
        jsonObject.set("sourceCode", baAppBasicDto.getSourceCode());
        jsonObject.set("createTime", new Date());
        //TODO: 新老客

        request.source(jsonObject, XContentType.JSON);

        IndexResponse indexResponse;
        try {
            indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es插入数据异常", e);
            throw new RuntimeException(e);
        }

        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            DocWriteResponse.Result result = indexResponse.getResult();
            log.info("CREATE" + result);
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            DocWriteResponse.Result result = indexResponse.getResult();
            log.info("UPDATED" + result);
        }

        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            log.info("处理成功的分片数少于总分片！");
        }

        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();//每一个错误的原因
                log.info(reason);
            }
        }

        return Result.ok();
    }

    public static void main(String[] args) throws IOException {
        //1.获取连接客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.111.4", 9200, "http"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                            credentialsProvider.setCredentials(AuthScope.ANY,
                                    new UsernamePasswordCredentials("elastic", "crpNmhReCKbOHc_6D_TY"));
                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        })
        );

        addEs(client);
//        getEs(client);

//        BaAppBasicDto baAppBasicDto = new BaAppBasicDto();
//
//        baAppBasicDto.setBaAppName("测试金融");
//        baAppBasicDto.setBaAppCode("FINANCE");
////        baAppBasicDto.setPageNo(1);
////        baAppBasicDto.setPageSize(15);
//
//        getEs(client, baAppBasicDto);


    }

    public static void addEs(RestHighLevelClient client) {
        //2.构建请求
        IndexRequest request = new IndexRequest("ba_app");//索引名
        String id = IdWorker.getIdStr();
        request.id(id);
        log.info("id:" + id);

        JSONObject jsonObject = new JSONObject();

        jsonObject.set("baAppCode", "FINANCE");
        jsonObject.set("baAppName", "测试金融");
        jsonObject.set("userCode", "xiaoPang");
        jsonObject.set("ip", "127.0.0.1");
        jsonObject.set("osName", "iPhone 100");
        jsonObject.set("pageName", "H5");
        jsonObject.set("pageCode", "JCDH5001");
        jsonObject.set("area", "山东日照");
        jsonObject.set("dwellTime", "8000");
        jsonObject.set("sourceName", "金彩贷");
        jsonObject.set("sourceCode", "JCD");
        jsonObject.set("createTime", new Date());
        //TODO: 新老客

        request.source(jsonObject, XContentType.JSON);

        IndexResponse indexResponse;
        try {
            indexResponse = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es插入数据异常", e);
            throw new RuntimeException(e);
        }

        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            DocWriteResponse.Result result = indexResponse.getResult();
            log.info("CREATE" + result);
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            DocWriteResponse.Result result = indexResponse.getResult();
            log.info("UPDATED" + result);
        }

        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            log.info("处理成功的分片数少于总分片！");
        }

        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();//每一个错误的原因
                log.info(reason);
            }
        }

    }

    public static void getEs(RestHighLevelClient client, BaAppBasicDto baAppBasicDto) {
//        //        2构建请求
//        GetRequest getRequest = new GetRequest("ba_app", "1976581284613017601");
////        3执行
//        GetResponse getResponse = null;
//        try {
//            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
////        4获取结果
//        log.info(getResponse.getId());
//        log.info(String.valueOf(getResponse.getVersion()));
//        log.info(getResponse.getSourceAsString());

        //1创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest("ba_app");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //字段过滤 TODO 后续做统一配置或字段变更对应变更
        String[] baAppBasicDtoFields = getJsonFields(BaAppBasicDto.class);
        searchSourceBuilder.fetchSource(baAppBasicDtoFields, new String[]{});

        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //搜索条件
        //根据关键字搜索
        if (StringUtils.isNotEmpty(baAppBasicDto.getBaAppCode())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("baAppCode", baAppBasicDto.getBaAppCode());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotEmpty(baAppBasicDto.getBaAppName())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("baAppName", baAppBasicDto.getBaAppName());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        if (StringUtils.isNotEmpty(baAppBasicDto.getPageCode())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("pageCode", baAppBasicDto.getPageCode());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotEmpty(baAppBasicDto.getPageName())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("pageName", baAppBasicDto.getPageName());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        if (StringUtils.isNotEmpty(baAppBasicDto.getSourceCode())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("sourceCode", baAppBasicDto.getSourceCode());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotEmpty(baAppBasicDto.getSourceName())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("sourceName", baAppBasicDto.getSourceName());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        if (StringUtils.isNotEmpty(baAppBasicDto.getUserCode())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("userCode", baAppBasicDto.getUserCode());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        if (StringUtils.isNotEmpty(baAppBasicDto.getArea())) {
            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("area", baAppBasicDto.getArea());
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
//        if (ObjectUtils.isNotEmpty(baAppBasicDto.getOldUser())) {
//            boolQueryBuilder.filter(QueryBuilders.termQuery("oldUser", baAppBasicDto.getOldUser()));
//        }

        searchSourceBuilder.query(boolQueryBuilder);

        //设置分页参数
        int pageNo = 1;
        int pageSize = 10;

        //起始记录下标
        int from = 0;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(pageSize);

        searchRequest.source(searchSourceBuilder);


        List<JSONObject> list = new ArrayList<>();
        try {
            //2执行搜索
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            //3获取响应结果
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;
            //匹配的总记录数
//            long totalHits = hits.totalHits;

            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                JSONObject result = new JSONObject();
                result.set("map", sourceAsMap);
                list.add(result);
            }

//            return Result.ok(list);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(list);

    }

    //动态获取类里的字段名
    public static String[] getJsonFields(Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.constructType(clazz);
        BeanDescription desc = mapper.getSerializationConfig().introspect(type);

        return desc.findProperties()
                .stream()
                .map(BeanPropertyDefinition::getName)
                .toArray(String[]::new);
    }

}
