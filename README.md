### 개요

- 프로젝트 명칭: Board
- 개발 인원: 1명
- 주요 기능:
    - 회원 - 회원가입 및 JWT 로그인, 유효성 검사 및 중복검사, 프로필 이미지 저장
    - 게시물 - CRUD 기능
    - 댓글 - CRUD 기능
- 개발 언어 : Java 17
- 개발 환경: SpringBoot 3.0.2, Gradle 7.6, JPA
- 데이터베이스 : MariaDB
- 형상관리 툴 : GitHub
- 간단 소개 : 게시판 API 서버입니다.

---

### 요구사항 분석

1. 회원가입
    - 유효성 검사
        - 패스워드는 8자 이상 16자 이하의 숫자, 영문자, 특수문자를 포함
        - 로그인 아이디, 닉네임 길이 2 ~ 15자 제한
        - 로그인 아이디, 닉네임, 비밀번호, 비밀번호 확인이 비어있다면, “OOO를 입력하세요” 메세지 return
    - 중복 확인 및 비밀번호 확인
        - 데이터베이스에 이미 존재하는 아이디로 회원가입을 한 경우 “이미 존재하는 회원입니다.” 메세지 return
        - 비밀번호, 비밀번호 확인이 다를 경우 “비밀번호가 일치하지 않습니다.” 메세지 return
2. 로그인
    - 로그인 검사 
        - 아이디가 존재하지 않을 시 “존재하지 않는 아이디입니다.” 메세지가 return
        - 비밀번호가 일치하지 않을 시 “비밀번호가 틀렸습니다.”의 메세지 return
3. 게시물
    - 게시물 등록 / 수정 시 모든 항목 입력
        - 입력을 하지 않으면 “OOO을 입력하세요” 메세지 return
    - Admin 권한을 가진 경우에 모든 게시물 삭제 권한. (미구현)
    - 회원가입 시 프로필 이미지 등록하지 않으면 기본 프로필사진으로 표시
4. 댓글
    - 댓글 등록 / 수정 시 모든 항목 입력
        - 입력을 하지 않으면 “OOO을 입력하세요” 메세지 return
    - Admin 권한을 가진 경우에 모든 게시물 삭제 권한. (미구현)
    - 회원가입 시 프로필 이미지 등록하지 않으면 기본 프로필사진으로 표시

---

### DB 설계
        

---

### API 설계

