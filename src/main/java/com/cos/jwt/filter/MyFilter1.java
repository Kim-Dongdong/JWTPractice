//package com.cos.jwt.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Bean;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//public class MyFilter1 implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) servletRequest;
//        HttpServletResponse res = (HttpServletResponse) servletResponse;
//
//        // 토큰: cos(토큰이 "cos"값이여야지만 인증해준다)
//        // 그럼 토큰을 언제 왜 만들어서 클라이언트에 전송해줘야할까?
//        // -> 클라이언트로부터 id, password 및 키값이 정상적으로 들어와서 로그인이 완료되면 토큰 생성 후 응답해준다.
//        // 요청할 때 마다 header에 Authorization의 value값으로 토큰을 가져옴
//        // 그 때 토큰이 넘어오면 그 토큰이 내가 만든 토큰이 맞는지 검증만 하면 된다.(RSA, HS256)
//        if (req.getMethod().equals("POST")) {
//            System.out.println("POST 요청됨");
//            String headerAuth = req.getHeader("Authorization");
//            System.out.println("headerAuth = " + headerAuth);
//
//            if (headerAuth.equals("cos")) {
//                filterChain.doFilter(req, res); // 다음 필터로 넘김
//            } else {
//                PrintWriter out = res.getWriter();
//                out.println("인증안됨");
//            }
//        }
//    }
//}
