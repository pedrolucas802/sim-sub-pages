package org.unifor;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.unifor.service.SimulationService;
import org.unifor.util.TokenUitl;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/")
public class SimulatorResource {

    @Inject
    Template index;

    @Inject
    Template history;

    @Inject
    SimulationService service;

    @Inject
    TokenUitl util;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance ui() {
        return index
                .data("result",    null)
                .data("duration",  null)
                .data("chartImage", null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance simulate(
            @FormParam("memorySize")        int memorySize,
            @FormParam("pageQueueSize")     int pageQueueSize,
            @FormParam("pageCount")         int pageCount,
            @FormParam("refs")              String refs,
            @FormParam("pageQueue")         String pageQueue,
            @FormParam("actionQueue")       String actionQueue,
            @FormParam("initialState")      String initialState,
            @FormParam("clockInterruption") int clockInterruption,
            @FormParam("tau")               @DefaultValue("1") int tau,
            @FormParam("algs")              List<String> algs
    ) throws Exception {
        algs.retainAll(Set.of("FIFO","LRU","CLOCK","OPTIMAL","NRU","WS_CLOCK"));

        List<Integer> refsList    = util.parseTokenList(pageQueue);
        List<Integer> actionList  = util.parseTokenList(actionQueue);
        List<Integer> initialList = util.parseTokenList(initialState);
        boolean clockFlag         = clockInterruption > 0;

        Map<String,Object> record = service.simulateAndRecord(
                refsList,
                memorySize,
                pageQueueSize,
                pageCount,
                refsList,
                actionList,
                initialList,
                clockFlag,
                tau,
                algs,
                pageQueue
        );

        @SuppressWarnings("unchecked")
        Map<String, Map<String,Object>> stats = (Map<String, Map<String,Object>>) record.get("algResults");
        String duration   = (String)   record.get("duration");
        String chartImage = (String)   record.get("chartImage");

        return index
                .data("result",     stats)
                .data("duration",   duration)
                .data("chartImage", chartImage);
    }

    @GET
    @Path("history")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showHistory() {
        return history.data("records", service.getHistory());
    }
}