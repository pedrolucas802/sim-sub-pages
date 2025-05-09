package org.unifor.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.unifor.algorithm.PageReplacementAlgorithm;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.*;

@ApplicationScoped
public class SimulationService {

    @Inject
    Instance<PageReplacementAlgorithm> algorithms;

    private final List<Map<String,Object>> history = new ArrayList<>();

    public Map<String,Object> simulateAndRecord(
            List<Integer> refs,
            int memorySize,
            int pageQueueSize,
            int pageCount,
            List<Integer> pageQueue,
            List<Integer> actionQueue,
            List<Integer> initialState,
            boolean clockInterrupt,
            int tau,
            List<String> algNames,
            String rawPageQueueString
    ) throws Exception {
        long totalStart = System.nanoTime();

        // dados do grafico
        DefaultCategoryDataset countDs = new DefaultCategoryDataset();
        DefaultCategoryDataset timeDs  = new DefaultCategoryDataset();

        Map<String, Map<String,Object>> stats = new LinkedHashMap<>();

        for (PageReplacementAlgorithm algo : algorithms) {
            if (!algNames.contains(algo.name())) continue;

            long t0 = System.nanoTime();
            int faults = algo.simulate(
                    refs,
                    memorySize,
                    pageQueueSize,
                    pageCount,
                    pageQueue,
                    actionQueue,
                    initialState,
                    clockInterrupt,
                    tau
            );
            double ms  = (System.nanoTime() - t0) / 1_000_000.0;
            int hits   = refs.size() - faults;

            stats.put(algo.name(), Map.of(
                    "faltas",  faults,
                    "acertos", hits,
                    "tempoMs", ms
            ));

            countDs.addValue(faults,    "Faltas",     algo.name());
            countDs.addValue(hits,      "Acertos",    algo.name());
            timeDs .addValue(ms,        "Tempo (ms)", algo.name());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Simulação de Substituição de Páginas",
                "Algoritmo",
                "Contagem",
                countDs
        );
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis axis2 = new NumberAxis("Tempo (ms)");
        plot.setRangeAxis(1, axis2);
        plot.setDataset(1, timeDs);
        plot.mapDatasetToRangeAxis(1, 1);

        BarRenderer r2 = new BarRenderer();
        r2.setSeriesPaint(0, new Color(61, 220, 145));
        plot.setRenderer(1, r2);

        String chartImage;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 700, 400);
            chartImage = Base64.getEncoder().encodeToString(baos.toByteArray());
        }

        double totalMs    = (System.nanoTime() - totalStart) / 1_000_000.0;
        String totalStr   = String.format("%.2f", totalMs);

        Map<String,Object> record = new LinkedHashMap<>();
        record.put("order",      history.size() + 1);
        record.put("refs",       rawPageQueueString);
        record.put("frames",     memorySize);
        record.put("tau",        tau);
        record.put("algResults", stats);
        record.put("duration",   totalStr);
        record.put("chartImage", chartImage);

        history.add(record);
        return record;
    }

    public List<Map<String,Object>> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public List<PageReplacementAlgorithm> getAlgorithms() {
        List<PageReplacementAlgorithm> list = new ArrayList<>();
        algorithms.forEach(list::add);
        return list;
    }
}