package org.example.lrucache;

import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class PutsAndGetsBench extends BenchmarkLauncher {
    final static int INITIAL_ENTRIES = 16;
    final static int MAX_ENTRIES = 200;
    final static int THREAD_COUNT = 20;
    final static int ITERATION_COUNT = 1000;

    @Benchmark
    public void jackson214Map() throws Exception {
        com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap map =
                new com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.Builder<Integer, String>()
                        .initialCapacity(INITIAL_ENTRIES)
                        .maximumCapacity(MAX_ENTRIES)
                        .concurrencyLevel(4)
                        .build();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            ArrayList futures = new ArrayList<Future<?>>(ITERATION_COUNT);
            for (int i = 0; i < ITERATION_COUNT; i++) {
                final int pos = i;
                futures.add(executorService.submit(() -> {
                    map.put(pos, "value" + pos);
                    map.get(pos);
                }));
            }
            futures.forEach((Consumer<Future<?>>) future -> {
                try {
                    future.get(100, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
          executorService.shutdown();
        }
    }

    @Benchmark
    public void newMap() throws Exception {
        PrivateMaxEntriesMap map = new PrivateMaxEntriesMap.Builder<Integer, String>()
                .initialCapacity(INITIAL_ENTRIES)
                .maximumCapacity(MAX_ENTRIES)
                .concurrencyLevel(4)
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            ArrayList futures = new ArrayList<Future<?>>(ITERATION_COUNT);
            for (int i = 0; i < ITERATION_COUNT; i++) {
                final int pos = i;
                futures.add(executorService.submit(() -> {
                    map.put(pos, "value" + pos);
                    map.get(pos);
                }));
            }
            futures.forEach((Consumer<Future<?>>) future -> {
                try {
                    future.get(100, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            executorService.shutdown();
        }
    }
}
