package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.PoiDTO;
import com.highfive.chajiserver.service.PoiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PoiServiceImpl implements PoiService {

    private static final String TMAP_APP_KEY = "vlxDMNvK4Q3NY3i9Rm7e24E2twBIgIeT7H6nOHQE";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<PoiDTO> autocomplete(String keyword) throws Exception {
        if (keyword == null || keyword.trim().length() < 2) {
            return Collections.emptyList();
        }

        // 1) Build v1 POI Autocomplete URL
        UriComponents comps = UriComponentsBuilder
                .fromHttpUrl("https://apis.openapi.sk.com/tmap/pois")
                .queryParam("version",        2)               // POI Autocomplete
                .queryParam("format",         "json")
                .queryParam("appKey",         TMAP_APP_KEY)
                .queryParam("searchKeyword",  keyword)
                .queryParam("count",          10)
                .queryParam("resCoordType",   "WGS84GEO")
                .queryParam("reqCoordType",   "WGS84GEO")
                .encode(StandardCharsets.UTF_8)
                .build();
        String url = comps.toUriString();
        System.out.println("▶ Autocomplete URL = " + url);

        // 2) Execute GET request
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.err.println("▶ Tmap POI 호출 실패: " + response.getStatusCode());
            return Collections.emptyList();
        }

        String body = response.getBody();
        if (body == null || body.isEmpty()) {
            System.err.println("▶ Tmap POI 응답 바디가 비어 있습니다.");
            return Collections.emptyList();
        }
        System.out.println("▶ RAW BODY = " + body);

        // 3) Parse JSON
        JsonNode root = objectMapper.readTree(body);
        List<PoiDTO> list = new ArrayList<>();

        // 3a) Try POI autocomplete results first
        JsonNode poiArray = root.path("searchPoiInfo").path("pois").path("poi");
        if (poiArray.isArray() && poiArray.size() > 0) {
            for (JsonNode poi : poiArray) {
                list.add(parseSearchPoi(poi));
            }
            return list;
        }

        // 3b) Fallback to address autocomplete results
        JsonNode addrArray = root.path("newAddressList").path("newAddress");
        if (addrArray.isArray()) {
            for (JsonNode addr : addrArray) {
                list.add(parseNewAddress(addr));
            }
            System.out.println(">>> keyword = " + keyword);
        }
        return list;
    }

    /** Parse POI node from searchPoiInfo.pois.poi */
    private PoiDTO parseSearchPoi(JsonNode poi) {
        String name       = poi.path("name").asText("");
        double lat        = poi.path("frontLat").asDouble();
        double lon        = poi.path("frontLon").asDouble();

        String addr       = poi.path("jibunAddress").asText("");
        if (addr.isEmpty()) {
            String upper  = poi.path("upperAddrName").asText("");
            String lower  = poi.path("lowerAddrName").asText("");
            addr = (upper + " " + lower).trim();
        }

        String telNo     = poi.path("firstTelNo").asText("");
        return new PoiDTO(name, lat, lon, addr, telNo);
    }

    /** Parse address node from newAddressList.newAddress */
    private PoiDTO parseNewAddress(JsonNode addr) {
        String jibun      = addr.path("jibunAddress").asText("");
        String roadName   = addr.path("roadName").asText("");
        String b1         = addr.path("bldNo1").asText("");
        String b2         = addr.path("bldNo2").asText("");

        // Use jibun first, fallback to roadName + building no
        String name = !jibun.isEmpty()
                ? jibun
                : (roadName
                + (b1.isEmpty() ? "" : " " + b1)
                + (b2.isEmpty() ? "" : "-" + b2));

        double lat       = addr.path("frontLat").asDouble();
        double lon       = addr.path("frontLon").asDouble();
        String telNo     = addr.path("telNo").asText("");
        return new PoiDTO(name, lat, lon, name, telNo);
    }
}
