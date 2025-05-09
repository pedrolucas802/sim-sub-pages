package org.unifor.algorithm;

import java.util.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("FIFO")
public class FifoAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "FIFO";
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
        // conjunto de paginas atualmente nos quadros
        Set<Integer> frames = new HashSet<>();
        // fila FIFO de ordem de chegada
        Deque<Integer> queue = new ArrayDeque<>();
        int faults = 0;

        for (Integer p : initialState) {
            if (p != 0 && frames.size() < memorySize) {
                if (frames.add(p)) {
                    queue.addLast(p);
                }
            }
        }

        // percorre a seq de refs
        for (Integer p : pageQueue) {
            if (frames.contains(p)) {
                continue;
            }
            faults++;
            if (frames.size() < memorySize) {
                frames.add(p);
                queue.addLast(p);
            } else {
                Integer victim = queue.removeFirst();
                frames.remove(victim);
                // carrega nova pagina
                frames.add(p);
                queue.addLast(p);
            }
        }

        return faults;
    }
}