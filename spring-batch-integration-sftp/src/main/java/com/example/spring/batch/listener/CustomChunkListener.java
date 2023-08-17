package com.example.spring.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j // Similar to: private static Logger log = LoggerFactory.getLogger(CustomChunkListener.class);
@Component
public class CustomChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("before chunk");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("after chunk");
    }
}
