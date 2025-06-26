package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.MemberDTO;

public interface MemberService {
    void register(MemberDTO member);
    String login(String username, String password);
    void logout(String token);
}