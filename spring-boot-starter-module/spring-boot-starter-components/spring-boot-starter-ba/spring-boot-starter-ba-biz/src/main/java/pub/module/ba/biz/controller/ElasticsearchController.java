//package pub.module.ba.biz.controller;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.fasterxml.jackson.databind.BeanDescription;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.MatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.web.bind.annotation.*;
//import pub.module.ba.biz.dto.BaAppBasicDto;
//import pub.module.ba.biz.service.ElasticsearchService;
//import pub.module.web.vo.Result;
//
//import jakarta.annotation.Resource;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Tag(name ="")
//@Tag(name ="用户行为收集")
//@RestController
//@RequestMapping("/es")
//@Slf4j
//public class ElasticsearchController {
//
//    @Resource
//    private ElasticsearchService elasticsearchService;
//    @Resource
//    private RestHighLevelClient restHighLevelClient;
//
//    @PostMapping("/node")
//    @Operation(summary ="锚点接收")
//    public Result<?> esNode(BaAppBasicDto baAppBasicDto) {
//        return elasticsearchService.esNode(baAppBasicDto);
//    }
//
//
//    @GetMapping("/list")
//    @Operation(summary ="查询es数据")
//    public Result<?> list(int pageNo, int pageSize ,BaAppBasicDto baAppBasicDto) {
//        //1创建搜索请求对象
//        SearchRequest searchRequest = new SearchRequest("ba_app");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        //字段过滤 TODO 后续做统一配置或字段变更对应变更
//        String[] baAppBasicDtoFields = getJsonFields(BaAppBasicDto.class);
//        searchSourceBuilder.fetchSource(baAppBasicDtoFields, new String[]{});
//
//        //创建布尔查询对象
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//
//        //搜索条件
//        //根据关键字搜索
//        if (StringUtils.isNotEmpty(baAppBasicDto.getBaAppCode())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("baAppCode", baAppBasicDto.getBaAppCode());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//        if (StringUtils.isNotEmpty(baAppBasicDto.getBaAppName())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("baAppName", baAppBasicDto.getBaAppName());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//
//        if (StringUtils.isNotEmpty(baAppBasicDto.getPageCode())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("pageCode", baAppBasicDto.getPageCode());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//        if (StringUtils.isNotEmpty(baAppBasicDto.getPageName())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("pageName", baAppBasicDto.getPageName());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//
//        if (StringUtils.isNotEmpty(baAppBasicDto.getSourceCode())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("sourceCode", baAppBasicDto.getSourceCode());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//        if (StringUtils.isNotEmpty(baAppBasicDto.getSourceName())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("sourceName", baAppBasicDto.getSourceName());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//
//        if (StringUtils.isNotEmpty(baAppBasicDto.getUserCode())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("userCode", baAppBasicDto.getUserCode());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
//
//        if (StringUtils.isNotEmpty(baAppBasicDto.getArea())) {
//            MatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.matchQuery("area", baAppBasicDto.getArea());
//            boolQueryBuilder.must(multiMatchQueryBuilder);
//        }
////        if (ObjectUtils.isNotEmpty(baAppBasicDto.getOldUser())) {
////            boolQueryBuilder.filter(QueryBuilders.termQuery("oldUser", baAppBasicDto.getOldUser()));
////        }
//
//        searchSourceBuilder.query(boolQueryBuilder);
//
//        //设置分页参数
////        int pageNo = baAppBasicDto.getPageNo();
////        int pageSize = baAppBasicDto.getPageSize();
//        if (pageNo <= 0) {
//            pageNo = 1;
//        }
//        if (pageSize <= 0) {
//            pageSize = 12;
//        }
//
//        //起始记录下标
//        int from = (pageNo - 1) * pageSize;
//        searchSourceBuilder.from(from);
//        searchSourceBuilder.size(pageSize);
//
//        searchRequest.source(searchSourceBuilder);
//
//
//        List<JSONObject> list = new ArrayList<>();
//        try {
//            //2执行搜索
//            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//            //3获取响应结果
//            SearchHits hits = searchResponse.getHits();
//            long totalHits = hits.getTotalHits().value;
//            //匹配的总记录数
////            long totalHits = hits.totalHits;
//
//            SearchHit[] searchHits = hits.getHits();
//            for (SearchHit hit : searchHits) {
//                //源文档
//                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                JSONObject result = new JSONObject();
//                sourceAsMap.put("all", JSONObject.parseObject(String.valueOf(hit)));
//                result.put("result", sourceAsMap);
//                list.add(result);
//            }
//
//            return Result.ok(list);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    @PostMapping("/listAll")
//    @Operation(summary ="查询全部es数据")
//    public Result<?> listAll(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
//
//
//        return Result.ok();
//    }
//
//    //动态获取类里的字段名
//    public static String[] getJsonFields(Class<?> clazz) {
//        ObjectMapper mapper = new ObjectMapper();
//        JavaType type = mapper.constructType(clazz);
//        BeanDescription desc = mapper.getSerializationConfig().introspect(type);
//
//        return desc.findProperties()
//                .stream()
//                .map(BeanPropertyDefinition::getName)
//                .toArray(String[]::new);
//    }
//
//}
