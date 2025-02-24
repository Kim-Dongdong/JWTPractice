package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// 시큐리티가 filter를 가지고있는데, 그 필터 중에 BasicAuthenticationFilter가 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 BasicAuthenticationFilter를 무조건 거친다.
// 만약에 권한이나 인증이 필요한 주소가 아니라면 BasicAuthenticationFilter필터를 거치지 않는다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    // JwtAuthorizationFilter 생성자
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // 인증이나 권한이 필요한 주소요청이 있체을 때 JwtAuthorizationFilter로 들어와 마지막으로 doFilterInternal을 호출해 다음 필터인으로 위임한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilterInternal(request, response, chain);
        System.out.println("인증이나 권한이 필요한 주소 요청이 들어옴");

        // 클라이언트가 Header에 저장한 JWT 토큰을 받아서 출력하는 테스트
        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader = " + jwtHeader);

        // header가 존재하는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰 검증으로 정상적인 사용자인지 확인한다
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

        // JWT 토큰 확인
        String username = JWT.require(Algorithm.HMAC512("kds")) // "kds"라는 secret값을 HMAC512 알고리즘으로 암호화
                .build() // 빌드
                .verify(jwtToken) // 사용자가 보낸 JWT 토큰 정보를 verify(일치하는지 확인)함
                .getClaim("username").asString(); // 일치하면 username값을 String값으로 가져옴

        // 서명이 정상적으로 되면
        if (username != null) {
            User userEntity = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            // 임의로 authentication을 생성한다. 원래 로그인을 통한 생성은 authenticationManager.authenticate(authenticationToken)을 통해 생성해야한다.
            // username이 null이 아니라는 가정 하에 principalDetails를 가져와 authentication 객체를 생성해준다.
            // JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어주는것이다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities()); // 임의 생성이므로 credentials = null

            // 강제로 SecurityContext에 접근해 Authentication 객체를 저장한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("권한 인증 완료, username: " + principalDetails.getUsername());
        }

        chain.doFilter(request, response);
    }
}
