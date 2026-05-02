package Back.DB;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    private static final String url = "jdbc:oracle:thin:@...";
    private static final String user = "";
    private static final String pw = "";   

    static{
        try {
            // 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return  DriverManager.getConnection(url, user, pw);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
