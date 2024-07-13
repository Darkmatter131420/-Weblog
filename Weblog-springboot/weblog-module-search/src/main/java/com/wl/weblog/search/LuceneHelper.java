package com.wl.weblog.search;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class LuceneHelper {

    /**
     * 创建索引
     * @param indexDir 索引存放的目录
     * @param documents 文档
     */
    public void createIndex(String indexDir, List<Document> documents) {
        try {
            File dir = new File(indexDir);

            // 判断索引目录是否存在
            if (dir.exists()) {
                // 删除目录中的内容
                FileUtils.cleanDirectory(dir);
            } else {
                // 若不存在，创建目录
                FileUtils.forceMkdir(dir);
            }

            // 读取目录索引
            Directory directory = FSDirectory.open(Paths.get(indexDir));

            // 中文分析器
            Analyzer analyzer = new SmartChineseAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // 创建索引
            IndexWriter indexWriter = new IndexWriter(directory, config);

            // 添加文档
            documents.forEach(document -> {
                try {
                    indexWriter.addDocument(document);
                } catch (IOException e) {
                    log.error("添加Lucene文档错误", e);
                }
            });
            // 提交
            indexWriter.commit();
            indexWriter.close();
        }catch (Exception e) {
            log.error("创建Lucene索引失败", e);
        }
    }

    /**
     * 关键词搜索，查询数据总量
     * @param indexDir 索引目录
     * @param word 查询关键字
     * @param columns 需要搜索的字段
     */
    public long searchTotal(String indexDir, String word, String[] columns) {
        try {
            //打开索引目录
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // 中文分析器
            Analyzer analyzer = new SmartChineseAnalyzer();
            // 查询解析器
            QueryParser queryParser = new MultiFieldQueryParser(columns, analyzer);
            // 解析查询关键字
            Query query = Parser.parse(word);

            // 搜索文档
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            // 返回文档数
            return topDocs.totalHits.value;
        }catch (Exception e) {
            log.error("搜索Lucene索引失败", e);
            return 0;
        }
    }
}
