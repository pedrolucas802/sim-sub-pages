package org.unifor.algorithm;

import java.util.List;

public interface PageReplacementAlgorithm {
    String name();
    int simulate(
            List<Integer> referenceString,
            int memorySize,
            int pageQueueSize,
            int pageCount,
            List<Integer> pageQueue,
            List<Integer> actionQueue,
            List<Integer> initialState,
            boolean clockInterrupt,
            int tau
    );
}