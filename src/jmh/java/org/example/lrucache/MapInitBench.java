package org.example.lrucache;

import com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.concurrent.ConcurrentHashMap;

public class MapInitBench extends BenchmarkLauncher {
    final static int INITIAL_ENTRIES = 16;
    final static int MAX_ENTRIES = 200;

    @Benchmark
    public void jackson214Map() throws Exception {
        new com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.Builder<String, String>()
                .initialCapacity(INITIAL_ENTRIES)
                .maximumCapacity(MAX_ENTRIES)
                .concurrencyLevel(4)
                .build();
    }

    @Benchmark
    public void newMap() throws Exception {
        new PrivateMaxEntriesMap.Builder<String, String>()
                .initialCapacity(INITIAL_ENTRIES)
                .maximumCapacity(MAX_ENTRIES)
                .concurrencyLevel(4)
                .build();
    }

    @Benchmark
    public void jackson213Map() throws Exception {
        new ConcurrentHashMap<String, String>(INITIAL_ENTRIES, 0.8f, 4);
    }
}
