package org.unifor.algorithm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.*;
import org.unifor.algorithm.PageReplacementAlgorithm;

@ApplicationScoped
@Named("OPTIMAL")
public class OptimalAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "OPTIMAL";
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
        Map<Integer, Deque<Integer>> futureIndices = new HashMap<>();
        for (int i = 0; i < pageQueue.size(); i++) {
            futureIndices
                    .computeIfAbsent(pageQueue.get(i), k -> new ArrayDeque<>())
                    .addLast(i);
        }

        List<Integer> frames = new ArrayList<>(memorySize);
        for (Integer p : initialState) {
            if (frames.size() < memorySize && p != null && p != 0) {
                frames.add(p);
            }
        }

        int faults = 0;

        for (int idx = 0; idx < pageQueue.size(); idx++) {
            Integer current = pageQueue.get(idx);
            Deque<Integer> dq = futureIndices.get(current);
            if (dq != null) {
                dq.pollFirst();
            }

            if (frames.contains(current)) {
                continue;
            }

            faults++;
            if (frames.size() < memorySize) {
                frames.add(current);
            } else {
                int victimIndex = 0;
                int farthestUse = -1;
                for (int j = 0; j < frames.size(); j++) {
                    Integer f = frames.get(j);
                    Deque<Integer> future = futureIndices.getOrDefault(f, new ArrayDeque<>());
                    int next = future.isEmpty() ? Integer.MAX_VALUE : future.peekFirst();
                    if (next > farthestUse) {
                        farthestUse = next;
                        victimIndex = j;
                    }
                }
                frames.set(victimIndex, current);
            }
        }

        return faults;
    }
}