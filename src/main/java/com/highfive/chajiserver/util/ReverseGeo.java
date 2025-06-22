package com.highfive.chajiserver.util;

import com.highfive.chajiserver.dto.ZscodeMappingDTO;
import com.highfive.chajiserver.mapper.MapMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReverseGeo {
    private static final String appKey = "j9wH5jYjPV32N7ZqIwfgC2f1SQ46vlAX7haPSv8E";
    private final MapMapper mapper;

    public String getZscode(double lat, double lng) {
        try {
            String urlStr = "https://apis.openapi.sk.com/tmap/geo/reversegeocoding?version=1"
                    + "&lon=" + lng
                    + "&lat=" + lat
                    + "&coordType=WGS84GEO"
                    + "&addressType=A00"
                    + "&appKey=" + appKey;
            System.out.println("보내는주소: " +urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 응답 처리
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            // JSON 파싱
            JSONObject json = new JSONObject(sb.toString());
            JSONObject addressInfo = json.getJSONObject("addressInfo");

            //zscode 추출
            String adminDongCode = addressInfo.getString("adminDongCode");
            String zcode = adminDongCode.substring(0,2);
            String sigungu_name = addressInfo.getString("gu_gun").trim();
            // 띄어쓰기가 있을 경우 첫 번째 단어만 사용
            if (sigungu_name.contains(" ")) {
                sigungu_name = sigungu_name.split(" ")[0];  // "성남시 수정구" → "성남시"
            }
            System.out.println("시군구명: " + sigungu_name);
            System.out.println("zcode: " + zcode);

            ZscodeMappingDTO zscodeDTO = new ZscodeMappingDTO();
            zscodeDTO.setZcode(zcode);
            zscodeDTO.setSigunguName(sigungu_name);
            String zscode =mapper.getZscode(zscodeDTO);

            System.out.println("구한 zscode: " + zscode);
            return zscode;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 또는 "00" 등 기본값
        }
    }

} //class
