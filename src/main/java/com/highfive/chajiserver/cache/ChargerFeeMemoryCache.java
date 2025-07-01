package com.highfive.chajiserver.cache;

import com.highfive.chajiserver.dto.ChargerFeeDTO;
import com.highfive.chajiserver.service.ChargerFeeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargerFeeMemoryCache {

    private final ChargerFeeService chargerFeeService;

    private final Map<String, ChargerFeeDTO> feeCache = new HashMap<>();

    @PostConstruct
    public void loadAllFees() {
        log.info("🚀 모든 요금 정보 로딩 시작");

        List<ChargerFeeDTO> fees = chargerFeeService.getAllFees();
        Map<String, ChargerFeeDTO> feeMap = new HashMap<>();

        for (ChargerFeeDTO fee : fees) {
            if (fee.getBusiId() != null) {
                feeMap.put(fee.getBusiId().trim().toUpperCase(), fee);
            }
        }

        feeCache.putAll(feeMap);
        log.info("✅ 요금 정보 캐시 완료: {}개 사업자", feeMap.size());
    }

    public void put(String busiId, ChargerFeeDTO fee) {
        if (busiId != null && fee != null) {
            feeCache.put(busiId.trim().toUpperCase(), fee);
        }
    }

    public void putAll(Map<String, ChargerFeeDTO> map) {
        for (Map.Entry<String, ChargerFeeDTO> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                feeCache.put(entry.getKey().trim().toUpperCase(), entry.getValue());
            }
        }
    }

    public ChargerFeeDTO get(String busiId) {
        return feeCache.get(busiId != null ? busiId.trim().toUpperCase() : null);
    }

    public Map<String, ChargerFeeDTO> getAll() {
        return feeCache;
    }

    public void clear() {
        feeCache.clear();
    }
}
