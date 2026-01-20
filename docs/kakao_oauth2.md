제공해주신 문서를 바탕으로 카카오 로그인(OAuth 2.0) 구현을 위한 단계별 정보와 API 명세를 정리해 드립니다.

### 1. 인가 코드 요청 (로그인 페이지 리다이렉트)

사용자를 카카오 로그인 페이지로 이동시켜야 합니다. 아래 URL에 필수 파라미터를 포함하여 **GET** 방식으로 리다이렉트합니다.

* 
**URL:** `https://kauth.kakao.com/oauth/authorize` 


* 
**필수 파라미터:** 


* `client_id`: 앱 REST API 키
* `redirect_uri`: 인가 코드를 전달받을 서비스 서버의 리다이렉트 URI (카카오 디벨로퍼스에 등록된 값)
* `response_type`: `code`로 고정



**예시 URL:**

```http
https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}

```



---

### 2. Redirect URI에서 받는 정보

사용자가 로그인을 완료하면, 설정한 **Redirect URI**로 이동하며 쿼리 파라미터(Query String)로 **인가 코드(Authorization Code)**가 전달됩니다.

* 
**전달받는 파라미터:** `code` 


* 
**형식:** `${REDIRECT_URI}?code=${AUTHORIZE_CODE}` 



서비스 서버는 이 `code` 값을 파싱하여 다음 단계인 토큰 요청에 사용해야 합니다.

---

### 3. 토큰 요청 (액세스 토큰 및 ID 토큰 발급)

받아온 인가 코드를 사용하여 액세스 토큰과 ID 토큰을 요청합니다.

* 
**URL:** `https://kauth.kakao.com/oauth/token` 


* 
**Method:** **POST** 


* 
**Header:** `Content-Type: application/x-www-form-urlencoded; charset=utf-8` 


* 
**Body 파라미터:** 


* `grant_type`: `authorization_code` (고정)
* `client_id`: 앱 REST API 키
* `redirect_uri`: 인가 코드를 전달받은 리다이렉트 URI (앞 단계와 동일해야 함)
* `code`: 리다이렉트 시 전달받은 인가 코드
* 
`client_secret`: (선택) 보안 강화를 위해 설정한 경우 필수 포함 





**CURL 요청 예시:**

```bash
curl -v -X POST "https://kauth.kakao.com/oauth/token" \
 -H "Content-Type: application/x-www-form-urlencoded; charset=utf-8" \
 -d "grant_type=authorization_code" \
 -d "client_id=${REST_API_KEY}" \
 -d "redirect_uri=${REDIRECT_URI}" \
 -d "code=${AUTHORIZE_CODE}"

```



---

### 4. ID 토큰으로 사용자 정보 확인하기

OpenID Connect를 활성화한 앱의 경우, 토큰 발급 응답에 `id_token`이 포함됩니다. 

**방법 1: ID 토큰 디코딩 (추천)**
ID 토큰은 JWT(JSON Web Token) 형식이므로, 별도의 API 호출 없이 **Base64 디코딩**하여 페이로드(Payload)에서 사용자 정보를 바로 확인할 수 있습니다.

* 
**포함 정보(Payload):** 


* `sub`: 회원번호 (User ID)
* `nickname`: 닉네임
* `picture`: 프로필 사진 URL
* `email`: 이메일 (유효하고 인증된 경우만 제공)



**방법 2: OIDC 사용자 정보 조회 API**
액세스 토큰을 사용하여 OIDC 표준 규격에 따른 사용자 정보를 조회할 수도 있습니다.

* 
**URL:** `https://kapi.kakao.com/v1/oidc/userinfo` 


* 
**Header:** `Authorization: Bearer ${ACCESS_TOKEN}` 



**방법 3: 일반 사용자 정보 조회 API (가장 상세함)**
액세스 토큰을 사용하여 카카오의 일반 사용자 정보 조회 API를 호출하면 더 상세한 정보(추가 동의 항목 등)를 얻을 수 있습니다.

* 
**URL:** `https://kapi.kakao.com/v2/user/me` 


* 
**Header:** `Authorization: Bearer ${ACCESS_TOKEN}` 



추가적으로 궁금한 점이 있으시다면 말씀해 주세요.