/*
 * Copyright 2014 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.lrucache;

import java.util.Map;
import java.util.Random;

import org.example.lrucache.ycsb.generator.NumberGenerator;
import org.example.lrucache.ycsb.generator.ScrambledZipfianGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * A benchmark that evaluates the read/write performance of a cache. The cache is pre-populated for
 * a 100% hit rate and a Zipf distribution of keys is used to mimic application usage patterns.
 * <p>
 * <pre>{@code
 *   ./gradlew jmh -PincludePattern=GetPutBenchmark
 * }</pre>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@State(Scope.Group)
@Warmup(iterations = 3)
@SuppressWarnings({"CanonicalAnnotationSyntax", "LexicographicalAnnotationAttributeListing",
        "PMD.JUnit4TestShouldUseAfterAnnotation", "PMD.MethodNamingConventions"})
public class ConcurrentBench {
    private static final int SIZE = (2 << 14);
    private static final int MASK = SIZE - 1;
    private static final int ITEMS = SIZE / 3;

    @Param({
            "Jackson214Map",
            "NewMap"
    })
    CacheType cacheType;

    Map<Integer, Boolean> cache;
    Integer[] ints;

    @State(Scope.Thread)
    public static class ThreadState {
        static final Random random = new Random();
        int index = random.nextInt();
    }

    @Setup
    public void setup() {
        ints = new Integer[SIZE];
        cache = cacheType.create(2 * SIZE);

        // Enforce full initialization of internal structures
        for (int i = 0; i < 2 * SIZE; i++) {
            cache.put(i, Boolean.TRUE);
        }
        cache.clear();

        // Populate using a realistic access distribution
        NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
        for (int i = 0; i < SIZE; i++) {
            ints[i] = generator.nextValue().intValue();
            cache.put(ints[i], Boolean.TRUE);
        }
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        //cache.cleanUp();
    }

    @Benchmark @Group("read_only") @GroupThreads(8)
    public Boolean readOnly(ThreadState threadState) {
        return cache.get(ints[threadState.index++ & MASK]);
    }

    @Benchmark @Group("write_only") @GroupThreads(8)
    public void writeOnly(ThreadState threadState) {
        cache.put(ints[threadState.index++ & MASK], Boolean.TRUE);
    }

    @Benchmark @Group("readwrite") @GroupThreads(6)
    public Boolean readwrite_get(ThreadState threadState) {
        return cache.get(ints[threadState.index++ & MASK]);
    }

    @Benchmark @Group("readwrite") @GroupThreads(2)
    public void readwrite_put(ThreadState threadState) {
        cache.put(ints[threadState.index++ & MASK], Boolean.TRUE);
    }
}
