# HongStargram 📸

> 홍익대학교 데이터베이스 과목 프로젝트 - 자바로 구현한 인스타그램 스타일 SNS 플랫폼

모던한 인스타그램 스타일의 UI와 실시간 채팅, 게시물 공유, 루틴 관리, 위치 기반 알람 기능을 제공하는 소셜 네트워크 서비스입니다.

## ✨ 주요 기능

### 📱 핵심 기능
- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **게시물 공유**: 이미지와 텍스트 게시, 좋아요, 댓글
- **실시간 채팅**: 1:1 채팅 및 그룹 채팅
- **친구 관리**: 친구 요청, 수락/거절, 친구 목록

### 🎯 추가 기능 (NEW!)
- **루틴 관리 달력**: 일정을 등록하고 관리할 수 있는 캘린더 기능
- **위치 기반 알람**: 설정한 사용자와의 거리가 가까워지면 알림

## 🎨 UI 개선

### 모던한 인스타그램 스타일
- ✅ 깔끔한 흰색 배경
- ✅ 인스타그램 스타일 파란색 액센트 (`#0095F6`)
- ✅ 연한 회색 테두리와 구분선
- ✅ 읽기 쉬운 폰트와 여백

| 이전 (다크 테마) | 이후 (라이트 테마) |
|-----------------|-------------------|
| 어두운 회색 배경 | 깔끔한 흰색 배경 |
| 흰색 텍스트 | 어두운 회색 텍스트 |
| 단조로운 UI | 세련된 인스타그램 스타일 |

## 🏗️ 기술 스택

### Backend
- **언어**: Java
- **데이터베이스**: Oracle Database
- **서버**: HttpServer (Java)
- **통신**: HTTP REST API

### Frontend
- **UI**: Java Swing
- **이미지 처리**: ImageIO
- **JSON 처리**: Gson

### API 서버 구조
- **Port 8002**: 채팅 서버
- **Port 8003**: 사용자 정보 서버
- **Port 8004**: 게시물 서버
- **Port 8005**: 루틴 관리 서버 (NEW)
- **Port 8006**: 위치 기반 알람 서버 (NEW)

## 🚀 시작하기

### 사전 요구사항
- Java JDK 11 이상
- Oracle Database
- 필요한 라이브러리:
  - `gson-2.x.jar`
  - `ojdbc8.jar` (Oracle JDBC 드라이버)

### 설치 및 실행

#### 1. 데이터베이스 설정

Oracle DB에 접속하여 스키마를 생성합니다:

```bash
sqlplus user1/00000000@htaeky.iptime.org:8000/FREEPDB1
```

`database_schema.sql` 파일 실행:

```sql
@database_schema.sql
```

#### 2. 백엔드 서버 실행

```powershell
# 프로젝트 디렉토리로 이동
cd HongStargram

# 컴파일
javac -encoding UTF-8 -cp ".;lib\*" -d . Back\API\*.java Back\DB\*.java

# 실행
java -cp ".;lib\*" Back.API.Server
```

**서버가 정상적으로 시작되면 다음과 같이 출력됩니다:**
```
Server starting on local ip: 192.168.x.x
Info server starting on htaeky.iptime.org : 8003
Chat server starting on htaeky.iptime.org : 8002
Post server starting on htaeky.iptime.org : 8004
Routine server starting on htaeky.iptime.org : 8005
Location server starting on htaeky.iptime.org : 8006
```

#### 3. 프론트엔드 실행

새 터미널 창에서:

```powershell
# 컴파일
javac -encoding UTF-8 -cp ".;lib\*" -d . Front\App\*.java Front\View\*.java Front\Server\*.java Front\Resize\*.java

# 실행
java -cp ".;lib\*" Front.App.App
```

## 📁 프로젝트 구조

```
HongStargram/
├── Back/                           # 백엔드
│   ├── API/                        # API 서버
│   │   ├── Server.java            # 메인 서버
│   │   ├── InfoServerHost.java    # 사용자 정보 API
│   │   ├── ChatServerHost.java    # 채팅 API
│   │   ├── PostServerHost.java    # 게시물 API
│   │   ├── RoutineServerHost.java # 루틴 API (NEW)
│   │   └── LocationServerHost.java # 위치 API (NEW)
│   └── DB/                         # 데이터베이스 레이어
│       ├── DBManager.java         # DB 연결 관리
│       ├── UsersDB.java           # 사용자 DB
│       ├── PostDB.java            # 게시물 DB
│       ├── ChatDB.java            # 채팅 DB
│       ├── FriendDB.java          # 친구 DB
│       ├── RoutineDB.java         # 루틴 DB (NEW)
│       └── LocationDB.java        # 위치 DB (NEW)
│
├── Front/                          # 프론트엔드
│   ├── App/                        # 애플리케이션 진입점
│   │   ├── App.java               # 메인 앱
│   │   └── Navigator.java         # 화면 네비게이션
│   ├── View/                       # UI 뷰
│   │   ├── LoginView.java         # 로그인 화면
│   │   ├── SignupView.java        # 회원가입 화면
│   │   ├── MainView.java          # 메인 피드 화면
│   │   ├── FriendView.java        # 친구 목록 화면
│   │   ├── TotMessageView.java    # 채팅방 목록
│   │   ├── UserMessageView.java   # 1:1 채팅 화면
│   │   └── CommentsView.java      # 댓글 화면
│   ├── Server/                     # 서버 통신 클라이언트
│   │   ├── InfoServerClient.java
│   │   ├── PostServerClient.java
│   │   ├── ChatServerClient.java
│   │   ├── RoutineServerClient.java   # (NEW)
│   │   └── LocationServerClient.java  # (NEW)
│   └── Resize/                     # 이미지 처리
│
├── lib/                            # 외부 라이브러리
│   ├── gson-2.x.jar
│   └── ojdbc8.jar
│
├── database_schema.sql             # DB 스키마 생성 스크립트
├── 사용_가이드.md                   # 상세 사용 가이드
└── README.md                       # 프로젝트 소개 (이 파일)
```

## 🔧 설정

### 서버 IP 설정

로컬 테스트와 배포 환경을 전환하려면 `Front/Server/IP.java` 파일을 수정하세요:

```java
public class IP {
    // 로컬 테스트용
    public static String ip = "localhost";
    
    // 또는 배포 서버용
    // public static String ip = "htaeky.iptime.org";
}
```

### 데이터베이스 연결 설정

`Back/DB/DBManager.java`에서 DB 연결 정보를 확인/수정하세요:

```java
String url = "jdbc:oracle:thin:@htaeky.iptime.org:8000/FREEPDB1";
String user = "user1";
String password = "00000000";
```

## 📝 주요 API

### 루틴 관리 API (Port 8005)

| 엔드포인트 | 메서드 | 설명 |
|-----------|--------|------|
| `/AddRoutine` | POST | 루틴 추가 |
| `/GetRoutines` | POST | 루틴 목록 조회 |
| `/DeleteRoutine` | POST | 루틴 삭제 |
| `/AddRoutineEvent` | POST | 루틴 일정 추가 |
| `/GetRoutineEvents` | POST | 루틴 일정 조회 |

### 위치 기반 알람 API (Port 8006)

| 엔드포인트 | 메서드 | 설명 |
|-----------|--------|------|
| `/UpdateLocation` | POST | 위치 업데이트 |
| `/GetUserLocation` | POST | 사용자 위치 조회 |
| `/AddLocationAlert` | POST | 알람 추가 |
| `/CheckNearbyUsers` | POST | 근처 사용자 확인 |
| `/CheckAlertedNearbyUsers` | POST | 알람 대상 근처 확인 |

## 🎓 주요 기술 구현

### Haversine 공식을 이용한 거리 계산

두 지점 간의 거리를 계산하기 위해 Haversine 공식을 사용합니다:

```sql
(6371 * ACOS(COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * 
COS(RADIANS(lon2) - RADIANS(lon1)) + 
SIN(RADIANS(lat1)) * SIN(RADIANS(lat2)))) * 1000
```

결과는 미터 단위로 반환됩니다.

### MERGE 쿼리를 활용한 위치 정보 관리

INSERT 또는 UPDATE를 하나의 쿼리로 처리:

```sql
MERGE INTO USER_LOCATIONS ul
USING (SELECT ? AS USER_ID, ? AS LATITUDE, ? AS LONGITUDE FROM DUAL) src
ON (ul.USER_ID = src.USER_ID)
WHEN MATCHED THEN UPDATE SET ul.LATITUDE = src.LATITUDE, ...
WHEN NOT MATCHED THEN INSERT (USER_ID, LATITUDE, LONGITUDE) VALUES (...)
```

## 🐛 트러블슈팅

### 컴파일 오류
- `lib/` 폴더에 모든 필요한 `.jar` 파일이 있는지 확인
- Java 버전이 11 이상인지 확인

### 서버 연결 오류
- 서버가 실행 중인지 확인
- `IP.java`에서 올바른 서버 주소 설정 확인
- 방화벽 설정 확인

### 데이터베이스 연결 오류
- Oracle DB가 실행 중인지 확인
- `DBManager.java`의 연결 정보 확인
- 네트워크 연결 확인

## 🤝 기여

이 프로젝트는 학교 과제 프로젝트입니다. 개선 사항이나 버그를 발견하시면 Issue를 열어주세요!

## 👥 팀원

- **임형택** - 백엔드 개발, DB 설계
- **정동욱** - 추가 기능 개발

## 📄 라이선스

이 프로젝트는 교육 목적으로 만들어졌습니다.

## 🙏 감사의 말

- 홍익대학교 데이터베이스 과목
- Oracle Database
- Java Swing GUI Framework

---

**Made with ❤️ by HongStargram Team**

*2025년 12월 업데이트*
