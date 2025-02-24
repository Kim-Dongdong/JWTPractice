<h2>JWT Practice</h2>

<h3>JWT란?</h3>
- JWT는 JSON 객체를 사용해 양 당사자 간 정보를 안전하게 전송하는 데 사용하는 개방형 표준입니다. 크게 header, payload, signature로 나뉘며, signature값을 암호화 복호화 하며 올바른 요청인지 판단합니다.

<h3>JWT 인증 과정 요약.</h3>
1. username, password가 정상이면 </br>
2. 서버 쪽에서 JWT 토큰을 생성한다. </br>
• 클라이언트가"/login" 요청해서username, password를POST 방식으로 전송하면 UsernamePasswordAuthenticationFilter가 동작한다. 그러므로 UsernamePasswordAuthenticationFilter를 상속받은 JwtAuthenticationFilter에서 생성 및 응답 로직을 구현한다. </br>
3. 클라이언트 쪽으로 JWT 토큰을 포함해 응답한다. </br>
4. 클라이언트는 요청할 때마다 JWT 토큰을 가지고 요청한다. </br>
5. 서버는 JWT 토큰이 유효한지를 판단한다. </br>
•. 권한이나 인증이 필요한 특정 주소를 요청했을 때BasicAuthenticationFilter를 무조건 거친다. 그러므로 JWT 인증 과정 또한 BasicAuthenticationFilter를 상속받은 JwtAuthorizationFilter에서 진행한다. </br>

<h3>공부 후기</h3>
JWT방식을 처음 공부해봤으며, 별도의 세션 관리 없이 인증 처리가 가능하므로 다양한 활용성이 장짐인것같습니다. </br>
JwtAuthenticationFilter와 JwtAuthorizationFilter를 구현하는것에 집중하였고, 인증 및 인가 처리 방식은 생각보다 만만하지 않다고 느꼈습니다.
