# 🚨 데이터베이스 테이블 생성 필요

## 문제 상황

루틴(Routine) 기능이 작동하지 않고 있습니다.  
데이터베이스에 필요한 테이블들이 생성되지 않아 아래와 같은 에러가 발생합니다.

### 에러 메시지
```
ORA-00942: 테이블 또는 뷰 "USER1"."ROUTINES"이(가) 존재하지 않습니다.
```

---

## 해결 방법

프로젝트 루트에 있는 **`database_schema.sql`** 파일을 데이터베이스에 실행해주세요.

### 📁 파일 위치
```
HongStargram/database_schema.sql
```

### 🔌 DB 연결 정보
- **Host**: `htaeky.iptime.org:8000`
- **Database**: `FREEPDB1`
- **User**: `user1`
- **Password**: `00000000`

---

## 생성해야 할 테이블 목록

### 1️⃣ ROUTINES
- 사용자의 루틴 정보를 저장하는 메인 테이블
- 컬럼: `ROUTINE_ID`, `USER_ID`, `TITLE`, `DESCRIPTION`, `COLOR`, `CREATED_AT`

### 2️⃣ ROUTINE_EVENTS
- 루틴의 실제 일정(이벤트)을 저장하는 테이블
- 컬럼: `EVENT_ID`, `ROUTINE_ID`, `EVENT_DATE`, `EVENT_TIME`, `IS_COMPLETED`, `COMPLETED_AT`

### 3️⃣ USER_LOCATIONS
- 사용자의 위치 정보를 저장하는 테이블
- 컬럼: `USER_ID`, `LATITUDE`, `LONGITUDE`, `LOCATION_NAME`, `UPDATED_AT`

### 4️⃣ LOCATION_ALERTS
- 위치 기반 알람 정보를 저장하는 테이블
- 컬럼: `ALERT_ID`, `USER_ID`, `TARGET_USER_ID`, `ALERT_DISTANCE`, `IS_ACTIVE`, `CREATED_AT`

### 📊 시퀀스 (Sequences)
- `ROUTINE_SEQ`
- `EVENT_SEQ`
- `ALERT_SEQ`

### 🔍 인덱스 (Indexes)
- `IDX_ROUTINES_USER`
- `IDX_EVENTS_ROUTINE`
- `IDX_EVENTS_DATE`
- `IDX_ALERTS_USER`
- `IDX_ALERTS_TARGET`

---

## 실행 방법

### 방법 1: SQL*Plus 사용
```bash
sqlplus user1/00000000@htaeky.iptime.org:8000/FREEPDB1 @database_schema.sql
```

### 방법 2: SQL Developer 사용
1. SQL Developer 실행
2. 위의 DB 연결 정보로 접속
3. `database_schema.sql` 파일 열기
4. 스크립트 실행 (F5)

### 방법 3: 수동 실행
SQL Developer나 다른 도구에서 `database_schema.sql` 파일의 내용을 복사해서 직접 실행

---

## 확인 사항

테이블 생성 후 아래 쿼리로 확인 가능합니다:

```sql
-- 테이블 목록 확인
SELECT table_name FROM user_tables 
WHERE table_name IN ('ROUTINES', 'ROUTINE_EVENTS', 'USER_LOCATIONS', 'LOCATION_ALERTS');

-- 시퀀스 확인
SELECT sequence_name FROM user_sequences;
```

---

## 참고 사항

- 프론트엔드 코드의 HTTP 응답 코드 버그는 이미 수정 완료되었습니다 (`RoutineServerClient.java`)
- 테이블만 생성되면 루틴 기능이 즉시 작동할 것으로 예상됩니다
- 서버 재시작은 필요하지 않습니다

---

## 문의

문제가 발생하거나 질문이 있으면 알려주세요! 🙏

**작성일**: 2025-12-09  
**작성자**: 프론트엔드 팀
