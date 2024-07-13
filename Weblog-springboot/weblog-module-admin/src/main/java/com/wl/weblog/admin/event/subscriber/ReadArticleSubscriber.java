package com.wl.weblog.admin.event.subscriber;

import com.wl.weblog.admin.event.ReadArticleEvent;
import com.wl.weblog.common.domain.mapper.ArticleMapper;
import com.wl.weblog.common.domain.mapper.StatisticsArticlePVMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Component
@Slf4j
public class ReadArticleSubscriber implements ApplicationListener<ReadArticleEvent>
{
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent (ReadArticleEvent event)
    {
        Long articleId = event.getArticleId();

        //获取当前线程名
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 文章阅读事件消费成功，文章 ID: {}", articleId);
        // 更新文章阅读量
        articleMapper.increaseReadNum(articleId);
        log.info("==> 文章阅读量 +1 操作成功，articleId: {}", articleId);

        //当日文章 PV 访问量 +1
        LocalDate currDate = LocalDate.now();
        articlePVMapper.increasePVCount(currDate);
        log.info("==> 当日文章 PV 访问量 +1 操作成功，currDate: {}", currDate);
    }
}
