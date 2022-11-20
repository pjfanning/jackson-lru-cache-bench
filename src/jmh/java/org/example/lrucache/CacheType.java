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

/**
 * A factory for creating a {@link BasicCache} implementation.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public enum CacheType {

    Jackson214Map {
        @Override public <K, V> Map<K, V> create(int maximumSize) {
            return new com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.Builder<K, V>()
                        .initialCapacity(16)
                        .maximumCapacity(maximumSize)
                        .concurrencyLevel(4)
                        .build();
        }
    },
    NewMap {
        @Override public <K, V> Map<K, V> create(int maximumSize) {
            return new PrivateMaxEntriesMap.Builder<K, V>()
                    .initialCapacity(16)
                    .maximumCapacity(maximumSize)
                    .concurrencyLevel(4)
                    .build();
        }
    }
    ;

    /**
     * Creates the cache with the maximum size. Note that some implementations may evict prior to
     * this threshold, and it is the caller's responsibility to adjust accordingly.
     */
    public abstract <K, V> Map<K, V> create(int maximumSize);
}
