package top.ilovemyhome.security.soy.core.cache;


import top.ilovemyhome.security.soy.core.cache.impl.MapCache;
import top.ilovemyhome.security.soy.core.cache.impl.SoftHashMap;

/**
 * Simple memory-only based {@link CacheManager CacheManager} implementation usable in production
 * environments.  It will not cause memory leaks as it produces {@link Cache Cache}s backed by
 * {@link SoftHashMap SoftHashMap}s which auto-size themselves based on the runtime environment's memory
 * limitations and garbage collection behavior.
 * <p/>
 * While the {@code Cache} instances created are thread-safe, they do not offer any enterprise-level features such as
 * cache coherency, optimistic locking, failover or other similar features.  For more enterprise features, consider
 * using a different {@code CacheManager} implementation backed by an enterprise-grade caching product (Hazelcast,
 * EhCache, TerraCotta, Coherence, GigaSpaces, etc., etc.).
 *
 * @since 1.0
 */
public class MemoryConstrainedCacheManager extends AbstractCacheManager {

    /**
     * Returns a new {@link MapCache MapCache} instance backed by a {@link SoftHashMap}.
     *
     * @param name the name of the cache
     * @return a new {@link MapCache MapCache} instance backed by a {@link SoftHashMap}.
     */
    @Override
    protected <K, V> Cache<K, V> createCache(String name) {
        return new MapCache<>(name, new SoftHashMap<>());
    }
}
