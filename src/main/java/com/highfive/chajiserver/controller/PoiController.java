package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.PoiDTO;
import com.highfive.chajiserver.service.PoiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
public class PoiController {

    private final PoiService service;

    public PoiController(PoiService service) {
        this.service = service;
    }

    @GetMapping("/autocomplete.map")
    public List<PoiDTO> autocomplete(
            @RequestParam(name = "query", defaultValue = "") String query
    ) throws Exception {
        return service.autocomplete(query);
    }
}
