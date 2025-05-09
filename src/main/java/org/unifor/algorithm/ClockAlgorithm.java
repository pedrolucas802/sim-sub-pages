package org.unifor.algorithm;

import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("CLOCK")
public class ClockAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String name() {
        return "CLOCK";
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
        Integer[] frames = new Integer[memorySize];
        boolean[] refBits = new boolean[memorySize];
        int pointer = 0;
        int faults = 0;

        //Carrega preinicialização de entradas não nulas de initialState
        for (int i = 0; i < memorySize && i < initialState.size(); i++) {
            Integer p = initialState.get(i);
            if (p != null && p != 0) {
                frames[i] = p;
                refBits[i] = false;
            }
        }

        // percorre a sequência real de referências
        for (Integer page : pageQueue) {
            boolean hit = false;
            for (int i = 0; i < memorySize; i++) {
                if (frames[i] != null && frames[i].equals(page)) {
                    refBits[i] = true;  // concede segunda chance
                    hit = true;
                    break;
                }
            }
            if (hit) {
                continue;
            }

            faults++;

            // tenta preencher um slot vazio
            boolean placed = false;
            for (int i = 0; i < memorySize; i++) {
                if (frames[i] == null) {
                    frames[i] = page;
                    refBits[i] = true;
                    placed = true;
                    break;
                }
            }
            if (placed) {
                continue;
            }

            // varredura de segunda chance
            while (true) {
                if (!refBits[pointer]) {
                    frames[pointer] = page;
                    refBits[pointer] = true;
                    pointer = (pointer + 1) % memorySize;
                    break;
                } else {
                    refBits[pointer] = false;
                    pointer = (pointer + 1) % memorySize;
                }
            }
        }

        return faults;
    }
}