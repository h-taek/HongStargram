package Back.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public static void main(String[] args) {
        try {
            // 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // DB 접속 정보
            String url = "jdbc:oracle:thin:@htaeky.iptime.org:8000/FREEPDB1";  
            String user = "user1";
            String pw = "00000000";

            // 연결
            Connection conn = DriverManager.getConnection(url, user, pw);
            System.out.println("Oracle DB 연결 성공");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}