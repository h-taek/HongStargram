oracle 주소 : htaeky.iptime.org:8000/FREEPDB1
sqlplus sys/00000000@htaeky.iptime.org:8000/FREEPDB1 as sysdba
sqlplus user1/00000000@htaeky.iptime.org:8000/FREEPDB1

api 서버 주소 : 
    htaeky.iptime.org:8002
    htaeky.iptime.org:8003
    htaeky.iptime.org:8004



=== 추가 기능 ===
리포스트 기능
(?????????)



=== Database Schema (Oracle) ===

1. USERS
컬럼명        타입	           설명
USER_ID     VARCHAR2(50)	유저 고유 ID (PK)
PW	        VARCHAR2(100)	비밀번호
NICKNAME	VARCHAR2(100)	닉네임


2. USER_FRIENDS (친구 관계)
컬럼명	      타입	           설명
USER_ID	    VARCHAR2(50)	유저 ID (PK, FK → USERS)
FRIEND_ID	VARCHAR2(50)	친구 ID (PK, FK → USERS)


3. FRIEND_REQUESTS (친구 요청)
컬럼명	          타입	           설명
FROM_USER_ID	VARCHAR2(50)	보낸 사람 (FK)
TO_USER_ID	    VARCHAR2(50)	받은 사람 (FK)


4. POSTS (게시글)
컬럼명	      타입	           설명
POST_ID	    NUMBER	        게시글 ID (PK)
USER_ID	    VARCHAR2(50)	작성자 (FK)
CONTENT	    CLOB	        게시글 내용
IMAGE_PATH	VARCHAR2(4000)	이미지 경로
CREATED_AT	DATE	        작성 시간


5. POST_LIKES (좋아요)
컬럼명	      타입	           설명
POST_ID	    NUMBER	        게시글 ID (PK, FK)
USER_ID	    VARCHAR2(50)	좋아요 누른 유저 (PK, FK)


6. COMMENTS (댓글)
컬럼명	      타입	            설명
COMMENT_ID	NUMBER	         댓글 ID (PK)
POST_ID	    NUMBER	         게시글 ID (FK)
USER_ID	    VARCHAR2(50)	 작성자 ID (FK)
CONTENT	    VARCHAR2(2000)	 댓글 내용


7. USER_READABLE_POSTS (열람 가능한 게시글 목록)
컬럼명	    타입	        설명
USER_ID	  VARCHAR2(50)	 유저 ID (PK, FK)
POST_ID	  NUMBER	     게시글 ID (PK, FK)


8. CHAT_ROOMS (채팅방)
컬럼명	    타입	        설명
ROOM_ID	  VARCHAR2(200)	 채팅방 고유 ID (PK)


9. CHAT_ROOM_MEMBERS (채팅방 멤버)
컬럼명	    타입	        설명
ROOM_ID	  VARCHAR2(200)	 채팅방 ID (PK, FK)
USER_ID	  VARCHAR2(50)	 참여자 ID (PK, FK)


10. CHAT_MESSAGES (메시지)
컬럼명	      타입	           설명
MESSAGE_ID	NUMBER	        메시지 ID (PK)
ROOM_ID	    VARCHAR2(200)   채팅방 ID (FK)
SENDER_ID	VARCHAR2(50)	보낸 사람 (FK)
MESSAGE	    CLOB	        메시지 내용
SENT_AT	    DATE	        보낸 시간