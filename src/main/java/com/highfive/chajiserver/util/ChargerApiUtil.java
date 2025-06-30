package com.highfive.chajiserver.util;

import com.highfive.chajiserver.dto.StationDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChargerApiUtil {

    // 하용 서비스 키
//    private final static String urlEncoded = "Wq%2BLPbmdYSbixCNUPkPm%2B3vWdEP6EHCS%2Fx%2FUNPAejzZCAlbDERkA7NZG3aqfORfDOT9cc1Sa7KgaXrpIzaaNAQ%3D%3D";
    // 동현 서비스 키
//    private final static String urlEncoded = "fy5BXzZbOlT5f8Rm%2FvV7CSbFwdw2WNrfeE5oJcOl%2Ffj6QY3vOzoIhLzhftkVLtK415OcxJVXkpJPurYA%2F%2FWTwg%3D%3D";
    // 혜진 서비스 키
    private final static String urlEncoded = "5fh1iyaZ1J7cmI8j1rYxs8gqu38xTrq7tfhweTdERepTeyYpeyqRArG1Ja1re0szzkXY%2B%2Fu%2BeObGbhZ6f%2B41mg%3D%3D";
    // 현서 서비스 키
//    private final static String urlEncoded = "NUqg9iZg%2BR57kpL1qrF1tst%2BAG3VXF5LAecO%2BCNKVMPmo34670TTUOan29Sq5DgB6%2FUXYTHmJOsUHoUp0CuKQw%3D%3D";

    // 솔비 서비스 키
//    private final static String urlEncoded = "Fd9vStrV5WKcvb5kTCXeBBw1zyOOxNrOysX80lQ02PLaIWqI7PFfY7PlcJopX%2F3kd5FYkiHYt6QYbhItGuhIhQ%3D%3D";
    // 광주 서비스 키
//    private final static String urlEncoded = "68byioDNf5NjuT4jEQUjBX4P%2FLanyTVSOo0PUtfJC6MEAKVzgDplnUlHcmfxQFDR8UAPZUv9hfacQucB7Wr%2Ffg%3D%3D";


    public List<StationDTO> getStationsByZscode(String zscode) {
        List<StationDTO> result = new ArrayList<>();
        try {
            String urlStr = "http://apis.data.go.kr/B552584/EvCharger/getChargerInfo?serviceKey="
                    + urlEncoded + "&numOfRows=9999&pageNo=1&zscode="
                    + zscode + "&dataType=JSON";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
//// ✅ 응답 전체 로그 찍기
//            log.info("🚨 zscode={} | API 응답: {}", zscode, sb.toString());

            JSONObject root = new JSONObject(sb.toString());
            JSONArray items = root.getJSONObject("items").getJSONArray("item");
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                StationDTO dto = new StationDTO();
                dto.setStatNm(obj.getString("statNm"));
                dto.setStatId(obj.getString("statId"));
                dto.setChgerId(obj.getString("chgerId"));
                dto.setChgerType(obj.getString("chgerType"));
                dto.setAddr(obj.getString("addr"));
                dto.setAddrDetail(obj.getString("addrDetail"));
                dto.setLocation(obj.getString("location"));
                dto.setUseTime(obj.getString("useTime"));
                dto.setLat(obj.getDouble("lat"));
                dto.setLng(obj.getDouble("lng"));
                dto.setBusiId(obj.getString("busiId"));
                dto.setBnm(obj.getString("bnm"));
                dto.setBusiNm(obj.getString("busiNm"));
                dto.setBusiCall(obj.getString("busiCall"));
                dto.setStat(obj.getString("stat"));
                dto.setStatUpdDt(obj.getString("statUpdDt"));
                dto.setLastTsdt(obj.getString("lastTsdt"));
                dto.setLastTedt(obj.getString("lastTedt"));
                dto.setNowTsdt(obj.getString("nowTsdt"));
                dto.setPowerType(obj.getString("powerType"));
                dto.setOutput(obj.getString("output"));
                dto.setMethod(obj.getString("method"));
                dto.setZcode(obj.getString("zcode"));
                dto.setZscode(obj.getString("zscode"));
                dto.setKind(obj.getString("kind"));
                dto.setKindDetail(obj.getString("kindDetail"));
                dto.setParkingFree(obj.getString("parkingFree"));
                dto.setNote(obj.getString("note"));
                dto.setLimitYn(obj.getString("limitYn"));
                dto.setLimitDetail(obj.getString("limitDetail"));
                dto.setDelYn(obj.getString("delYn"));
                dto.setDelDetail(obj.getString("delDetail"));
                dto.setTrafficYn(obj.getString("trafficYn"));
                dto.setYear(obj.getString("year"));
                result.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}