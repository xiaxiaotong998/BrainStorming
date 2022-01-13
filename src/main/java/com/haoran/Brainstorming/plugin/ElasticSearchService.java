package com.haoran.Brainstorming.plugin;

import com.haoran.Brainstorming.config.service.BaseService;
import com.haoran.Brainstorming.model.SystemConfig;
import com.haoran.Brainstorming.service.ISystemConfigService;
import com.haoran.Brainstorming.util.MyPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Component
@DependsOn("mybatisPlusConfig")
public class ElasticSearchService implements BaseService<RestHighLevelClient> {

    @Resource
    private ISystemConfigService systemConfigService;
    private final Logger log = LoggerFactory.getLogger(ElasticSearchService.class);
    private RestHighLevelClient client;
    private String name;

    public static XContentBuilder topicMappingBuilder;

    static {
        try {
            topicMappingBuilder = JsonXContent.contentBuilder()
                    .startObject()
                    .startObject("properties")
                    // .startObject("id")
                    // .field("type", "integer")
                    // .endObject()
                    .startObject("title")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("index", "true")
                    .endObject()
                    .startObject("content")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("index", "true")
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public RestHighLevelClient instance() {
        if (this.client != null) return client;
        try {
            SystemConfig systemConfigHost = systemConfigService.selectByKey("elasticsearch_host");
            String host = systemConfigHost.getValue();
            SystemConfig systemConfigPort = systemConfigService.selectByKey("elasticsearch_port");
            String port = systemConfigPort.getValue();
            SystemConfig systemConfigName = systemConfigService.selectByKey("elasticsearch_index");
            name = systemConfigName.getValue();

            if (StringUtils.isEmpty(host) || StringUtils.isEmpty(port)) return null;
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, Integer.parseInt(port), "http")));
            if (!this.existIndex()) this.createIndex("topic", topicMappingBuilder);
            return client;
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public boolean createIndex(String type, XContentBuilder mappingBuilder) {
        try {
            if (this.instance() == null) return false;
            CreateIndexRequest request = new CreateIndexRequest(name);
            request.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_shards", 5));
            if (mappingBuilder != null) request.mapping(type, mappingBuilder);
            CreateIndexResponse response = this.client.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean existIndex() {
        try {
            if (this.instance() == null) return false;
            GetIndexRequest request = new GetIndexRequest();
            request.indices(name);
            request.local(false);
            request.humanReadable(true);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 删除索引
    public boolean deleteIndex() {
        try {
            if (this.instance() == null) return false;
            DeleteIndexRequest request = new DeleteIndexRequest(name);
            request.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void createDocument(String type, String id, Map<String, Object> source) {
        try {
            if (this.instance() == null) return;
            IndexRequest request = new IndexRequest(name, type, id);
            request.source(source);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void updateDocument(String type, String id, Map<String, Object> source) {
        try {
            if (this.instance() == null) return;
            UpdateRequest request = new UpdateRequest(name, type, id);
            request.doc(source);
            client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteDocument(String type, String id) {
        try {
            if (this.instance() == null) return;
            DeleteRequest request = new DeleteRequest(name, type, id);
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public void bulkDocument(String type, Map<String, Map<String, Object>> sources) {
        try {
            if (this.instance() == null) return;
            BulkRequest requests = new BulkRequest();
            Iterator<String> it = sources.keySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                count++;
                String next = it.next();
                IndexRequest request = new IndexRequest(name, type, next);
                request.source(sources.get(next));
                requests.add(request);
                if (count % 1000 == 0) {
                    client.bulk(requests, RequestOptions.DEFAULT);
                    requests.requests().clear();
                    count = 0;
                }
            }
            if (requests.numberOfActions() > 0) client.bulk(requests, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void bulkDeleteDocument(String type, List<Integer> ids) {
        try {
            if (this.instance() == null) return;
            BulkRequest requests = new BulkRequest();
            int count = 0;
            for (Integer id : ids) {
                count++;
                DeleteRequest request = new DeleteRequest(name, type, String.valueOf(id));
                requests.add(request);
                if (count % 1000 == 0) {
                    client.bulk(requests, RequestOptions.DEFAULT);
                    requests.requests().clear();
                    count = 0;
                }
            }
            if (requests.numberOfActions() > 0) client.bulk(requests, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     *
     *
     * @param pageNo
     * @param pageSize
     * @param keyword
     * @param fields
     * @return  {@link Page}
     */
    public MyPage<Map<String, Object>> searchDocument(Integer pageNo, Integer pageSize, String keyword, String... fields) {
        try {
            if (this.instance() == null) return new MyPage<>();
            SearchRequest request = new SearchRequest(name);
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.query(QueryBuilders.multiMatchQuery(keyword, fields));
            builder.from((pageNo - 1) * pageSize).size(pageSize);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            long totalCount = response.getHits().getTotalHits();

            List<Map<String, Object>> records = Arrays.stream(response.getHits().getHits()).map(hit -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", hit.getId());
                map.putAll(hit.getSourceAsMap());
                return map;
            }).collect(Collectors.toList());
            MyPage<Map<String, Object>> page = new MyPage<>(pageNo, pageSize);
            page.setTotal(totalCount);
            page.setRecords(records);
            return page;
        } catch (IOException e) {
            log.error(e.getMessage());
            return new MyPage<>();
        }
    }
}
