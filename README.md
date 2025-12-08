## 1. 서버 주소 
### 1. DB 주소
htaeky.iptime.org:8000/FREEPDB1  
sqlplus sys/00000000@htaeky.iptime.org:8000/FREEPDB1 as sysdba  
sqlplus user1/00000000@htaeky.iptime.org:8000/FREEPDB1  

### 2. api 서버 주소
htaeky.iptime.org:8002  
htaeky.iptime.org:8003  
htaeky.iptime.org:8004  
htaeky.iptime.org:8005 (NEW - Routine)
htaeky.iptime.org:8006 (NEW - Location)


## 2. 추가 기능  
- ✅ 루틴 관리 달력
- ✅ 위치 기반 알람
- ✅ 모던 UI 개선 (인스타그램 스타일)


## 3. Database Schema (Oracle) 
### 1. USERS
| 컬럼명   | 타입           | 설명                |
|---------|----------------|---------------------|
| USER_ID | VARCHAR2(50)   | 유저 고유 ID (PK)  |
| PW      | VARCHAR2(100)  | 비밀번호           |
| NICKNAME| VARCHAR2(100)  | 닉네임             |

### 2. USER_FRIENDS (친구 관계)
| 컬럼명    | 타입           | 설명                      |
|-----------|----------------|---------------------------|
| USER_ID   | VARCHAR2(50)   | 유저 ID (PK, FK → USERS) |
| FRIEND_ID | VARCHAR2(50)   | 친구 ID (PK, FK → USERS) |

### 3. FRIEND_REQUESTS (친구 요청)
| 컬럼명       | 타입           | 설명            |
|--------------|----------------|-----------------|
| FROM_USER_ID | VARCHAR2(50)   | 보낸 사람 (FK) |
| TO_USER_ID   | VARCHAR2(50)   | 받은 사람 (FK) |

### 4. POSTS (게시글)
| 컬럼명     | 타입            | 설명          |
|------------|-----------------|---------------|
| POST_ID    | NUMBER          | 게시글 ID (PK) |
| USER_ID    | VARCHAR2(50)    | 작성자 (FK)    |
| CONTENT    | CLOB            | 게시글 내용    |
| IMAGE_PATH | VARCHAR2(4000)  | 이미지 경로    |
| CREATED_AT | DATE            | 작성 시간      |

### 5. POST_LIKES (좋아요)
| 컬럼명  | 타입          | 설명                       |
|---------|---------------|----------------------------|
| POST_ID | NUMBER        | 게시글 ID (PK, FK)         |
| USER_ID | VARCHAR2(50)  | 좋아요 누른 유저 (PK, FK) |

### 6. COMMENTS (댓글)
| 컬럼명     | 타입             | 설명             |
|------------|------------------|------------------|
| COMMENT_ID | NUMBER           | 댓글 ID (PK)     |
| POST_ID    | NUMBER           | 게시글 ID (FK)   |
| USER_ID    | VARCHAR2(50)     | 작성자 ID (FK)   |
| CONTENT    | VARCHAR2(2000)   | 댓글 내용        |

### 7. USER_READABLE_POSTS (열람 가능한 게시글)
| 컬럼명  | 타입          | 설명                   |
|---------|---------------|------------------------|
| USER_ID | VARCHAR2(50)  | 유저 ID (PK, FK)       |
| POST_ID | NUMBER        | 게시글 ID (PK, FK)     |

### 8. CHAT_ROOMS (채팅방)
| 컬럼명 | 타입            | 설명                |
|---------|-----------------|---------------------|
| ROOM_ID | NUMBER   | 채팅방 고유 ID (PK) |

### 9. CHAT_ROOM_MEMBERS (채팅방 멤버)
| 컬럼명  | 타입            | 설명                 |
|---------|-----------------|----------------------|
| ROOM_ID | NUMBER   | 채팅방 ID (PK, FK)   |
| USER_ID | VARCHAR2(50)    | 참여자 ID (PK, FK)   |

### 10. CHAT_MESSAGES (메시지)
| 컬럼명     | 타입            | 설명             |
|------------|-----------------|------------------|
| ROOM_ID    | NUMBER   | 채팅방 ID (FK)   |
| SENDER_ID  | VARCHAR2(50)    | 보낸 사람 (FK)   |
| MESSAGE    | CLOB            | 메시지 내용       |
| SENT_AT    | DATE            | 보낸 시간         |

### 11. ROUTINES (루틴) ✨ NEW
| 컬럼명      | 타입            | 설명                  |
|-------------|-----------------|----------------------|
| ROUTINE_ID  | NUMBER          | 루틴 ID (PK)         |
| USER_ID     | VARCHAR2(50)    | 유저 ID (FK)         |
| TITLE       | VARCHAR2(200)   | 루틴 제목            |
| DESCRIPTION | VARCHAR2(1000)  | 루틴 설명            |
| COLOR       | VARCHAR2(20)    | 색상                 |
| CREATED_AT  | DATE            | 생성 시간            |

### 12. ROUTINE_EVENTS (루틴 이벤트) ✨ NEW
| 컬럼명         | 타입            | 설명                  |
|----------------|-----------------|----------------------|
| EVENT_ID       | NUMBER          | 이벤트 ID (PK)       |
| ROUTINE_ID     | NUMBER          | 루틴 ID (FK)         |
| EVENT_DATE     | DATE            | 일정 날짜            |
| EVENT_TIME     | VARCHAR2(10)    | 일정 시간            |
| IS_COMPLETED   | CHAR(1)         | 완료 여부 (Y/N)      |
| COMPLETED_AT   | DATE            | 완료 시간            |

### 13. USER_LOCATIONS (사용자 위치) ✨ NEW
| 컬럼명         | 타입            | 설명                  |
|----------------|-----------------|----------------------|
| USER_ID        | VARCHAR2(50)    | 유저 ID (PK, FK)     |
| LATITUDE       | NUMBER(10,6)    | 위도                 |
| LONGITUDE      | NUMBER(10,6)    | 경도                 |
| LOCATION_NAME  | VARCHAR2(200)   | 위치 이름            |
| UPDATED_AT     | DATE            | 업데이트 시간        |

### 14. LOCATION_ALERTS (위치 알람) ✨ NEW
| 컬럼명           | 타입            | 설명                  |
|------------------|-----------------|----------------------|
| ALERT_ID         | NUMBER          | 알람 ID (PK)         |
| USER_ID          | VARCHAR2(50)    | 유저 ID (FK)         |
| TARGET_USER_ID   | VARCHAR2(50)    | 대상 유저 (FK)       |
| ALERT_DISTANCE   | NUMBER(10,2)    | 알람 거리 (미터)     |
| IS_ACTIVE        | CHAR(1)         | 활성 여부 (Y/N)      |
| CREATED_AT       | DATE            | 생성 시간            |