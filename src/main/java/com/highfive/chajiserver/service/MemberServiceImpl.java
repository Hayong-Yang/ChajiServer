//MemberServiceImpl
package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.MemberDTO;
import com.highfive.chajiserver.jwt.JwtUtil;
import com.highfive.chajiserver.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberMapper mapper;
    private final JwtUtil jwtUtil;

    @Override
    public void register(MemberDTO member) {
        String hashed = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        member.setPassword(hashed);
        mapper.save(member);
    }

    @Override
    public String login(String userId, String password) {
        MemberDTO member = mapper.findByUserId(userId);
        if(member != null && BCrypt.checkpw(password, member.getPassword())) {
            return jwtUtil.generateToken(member.getUserId(), member.getIdx());
        }
        return null;
    }

    @Override
    public MemberDTO getUserInfoFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        String userId = jwtUtil.getUserIdFromToken(jwt);
        MemberDTO member = mapper.findByUserId(userId);
        if(member != null) {
            member.setPassword(null);
        }
        return member;
    }

    @Override
    public void logout(String token) {

    }
}
