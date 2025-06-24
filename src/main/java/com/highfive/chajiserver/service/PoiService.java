package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.PoiDTO;
import java.util.List;

public interface PoiService {
    List<PoiDTO> autocomplete(String keyword) throws Exception;
}
