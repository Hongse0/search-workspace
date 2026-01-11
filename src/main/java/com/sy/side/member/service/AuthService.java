package com.sy.side.member.service;

import com.sy.side.member.dto.request.LoginRequest;
import com.sy.side.member.dto.request.SignupRequest;
import com.sy.side.member.dto.response.LoginResponse;
import com.sy.side.member.entity.Member;
import com.sy.side.member.repository.UserRepository;
import com.sy.side.member.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPw = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPw)
                .nickname(request.getNickname())
                .tag(null)
                .deleteYn("N")
                .build();

        userRepository.save(member);
    }


    public LoginResponse login(LoginRequest request) {
        Member user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(
                user.getMemberId(),
                user.getEmail(),
                user.getNickname()
        );

        return new LoginResponse(token, jwtTokenProvider.getExpirationMs());
    }


}
