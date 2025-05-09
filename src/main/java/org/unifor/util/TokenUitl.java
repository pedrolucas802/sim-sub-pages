package org.unifor.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;


@ApplicationScoped
public class TokenUitl {

    public List<Integer> parseTokenList(String csv) {
        if (csv == null || csv.isBlank()) return List.of();

        return Arrays.stream(csv.split("[,|]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::tokenToInt)
                .toList();
    }

    private int tokenToInt(String t) {
        // A‑Z -> 1‑26
        if (t.length() == 1 && Character.isLetter(t.charAt(0)))
            return Character.toUpperCase(t.charAt(0)) - 'A' + 1;
        // fila de ações: L/E -> 1/0
        if (t.equalsIgnoreCase("L")) return 1;
        if (t.equalsIgnoreCase("E")) return 0;
        return Integer.parseInt(t);
    }
}
