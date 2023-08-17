package com.example.spring.batch.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class CustomChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        ChunkListener.super.beforeChunk(context);
    }

    @Override
    public void afterChunk(ChunkContext context) {
        ChunkListener.super.afterChunk(context);
    }
}
