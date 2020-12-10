package com.fufu.service;

import com.alibaba.fastjson.JSON;
import com.fufu.pojo.Content;
import com.fufu.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //将获取的京东数据放入索引文档内
    public boolean parseContent(String keyword) throws Exception{
        List<Content> goods = HtmlParseUtil.parseJD(keyword);
        BulkRequest request = new BulkRequest();
        request.timeout("2m");

        for(int i=0; i< goods.size(); i ++){
            request.add(
                    new IndexRequest("jd_goods").source(JSON.toJSONString(goods.get(i)), XContentType.JSON));

        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }


    //将文档库里存储的数据进行elasticsearch查询
    public List<Content> search(String keyword, Integer pageFrom, Integer pageSize) throws IOException {
        if (pageFrom < 0) {
            pageFrom = 1;
        }


        //多条件查询
        SearchRequest request = new SearchRequest("jd_goods");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //分页
        builder.from(pageFrom);
        builder.size(pageSize);

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder(); //生成高亮查询器
        highlightBuilder.field("title");      //高亮查询字段
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
        highlightBuilder.preTags("<span style='color:red'>");   //高亮设置
        highlightBuilder.postTags("</span>");

        //精准匹配
        TermQueryBuilder termBuilder = QueryBuilders.termQuery("title", keyword);
        builder.query(termBuilder);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        builder.highlighter(highlightBuilder);

        request.source(builder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        //解析返回结果
//        List<Map<String, Object>> list = new ArrayList<>();

        List<Content> contents = new ArrayList<>();
        for(SearchHit documentFileds:response.getHits().getHits()){

            Content content = new Content();
            Map<String, Object> source = documentFileds.getSourceAsMap();
            Set<String> keys = source.keySet();
            for(String key:keys){
                if(key=="title"){
                    Map<String, HighlightField> highlightFields = documentFileds.getHighlightFields();
                    HighlightField title = highlightFields.get("title");
                    Map<String, Object> sourceAsMap = documentFileds.getSourceAsMap();
                    if(title != null){
                        org.elasticsearch.common.text.Text[] fragments = title.fragments();
                        String n_title = "";
                        for (Text text : fragments) {
                            n_title += text;
                        }
                        content.setItem(content, key, n_title);
                    }
                }else {
                    content.setItem(content, key, (String) source.get(key));
                }
            }
            contents.add(content);
//            list.add(documentFileds.getSourceAsMap());
        }

        return contents;

    }

}
