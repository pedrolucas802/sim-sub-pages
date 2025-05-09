package org.unifor.algorithm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.LinkedHashMap;
import java.util.List;

@ApplicationScoped
@Named("LRU")
public class LruAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "LRU";
    }

    @Override
    public int simulate(
            List<Integer> referenceString,
            int memorySize,
            int pageQueueSize,
            int pageCount,
            List<Integer> pageQueue,
            List<Integer> actionQueue,
            List<Integer> initialState,
            boolean clockInterrupt,
            int tau
    ) {

        LinkedHashMap<Integer, Boolean> cache = new LinkedHashMap<>(16, 0.75f, true);

        for (Integer p : initialState) {
            if (p != null && p != 0) {
                cache.put(p, Boolean.TRUE);
            }
        }

        int faults = 0;

        for (Integer page : pageQueue) {
            if (cache.containsKey(page)) {
                cache.get(page);
            } else {
                faults++;
                if (cache.size() >= memorySize) {
                    //remove o LRU
                    Integer lru = cache.keySet().iterator().next();
                    cache.remove(lru);
                }
                cache.put(page, Boolean.TRUE);
            }
        }

        return faults;
    }
}