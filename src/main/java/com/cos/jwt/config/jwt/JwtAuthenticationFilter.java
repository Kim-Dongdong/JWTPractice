package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 존재하는 UsernamePasswordAuthenticationFilter를 커스텀한다
// 클라이언트가 "/login" 요청해서 username, password를 POST 방식으로 전송하면
// UsernamePasswordAuthenticationFilter가 동작한다
// 물론 addFilter로 등록해줘야한다
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // "/login" 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도");

        // 1. username, password를 받아서
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

            // 로그인 시도를 위해선 UsernamePasswordAuthenticationToken을 담은
            // Authentication을 반환하는것이 필수이다.
            // Authentication은 이 토큰을 가지고 loadByUsername 호출
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

            // PrincipalDetailsService의 loadByUsername() 함수가 실행된다.
            // DB에 해당 로그인 정보에 맞는 유저가 존재하면 Authentication에 authenticationToken이 잘 저장된것이다.
            Authentication authentication
                    = authenticationManager.authenticate(authenticationToken);

            // authentication 객체가 session 영역에 저장된것을 확인
            // 객체 정보가 잘 나오면 객체가 session 영역에 저장됨 => 로그인이 완료되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("principalDetails.getUser().getUsername() = " + principalDetails.getUser().getUsername());

            // JWT 토큰을 담아 리턴해주면 된다.
            return authentication;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("JwtAuthenticationFilter : " + loginRequestDto);

        return null;
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수 실행
    // 이 함수에서 JWT 토큰을 만들어서 request요청한 사용자에게 JWT 토큰을 response해주면 된다.
    // 해당 방식은 RSA 방식이 아닌, Hash암호 방식이다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행 : 인증 완료");

        // 아래 PrincipalDetails 객체를 통해 JWT 토큰을 생성한다
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername()) // 별 의미 없음
                .withExpiresAt(new Date(System.currentTimeMillis() + (600000 * 10))) // 만료 시간
                .withClaim("id", principalDetails.getUser().getId()) // id
                .withClaim("username", principalDetails.getUser().getUsername()) // username
                .sign(Algorithm.HMAC512("kds")); // HMAC512 방식의 알고리즘으로 암호화한다

        // header에 jwtToken을 저장해 사용자에게 response한다.
        response.addHeader("Authorization", "Bearer "+jwtToken);
    }
}
