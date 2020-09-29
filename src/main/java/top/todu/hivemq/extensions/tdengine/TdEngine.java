package top.todu.hivemq.extensions.tdengine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/28 16:17 <br>
 */
public class TdEngine {

  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    Class.forName("com.taosdata.jdbc.TSDBDriver");
    String jdbcUrl = "jdbc:TAOS://127.0.0.1:6030/test?user=root&password=taosdata&timezone=Asia/Shanghai&locale=CN_zh";
    Connection conn = DriverManager.getConnection(jdbcUrl);
    Statement stmt = conn.createStatement();
    int affectedRows = stmt.executeUpdate("insert into test values(now, '23, 10.3') (now + 1s, '20, 9.3')");
    System.out.println(affectedRows);
    stmt.close();
  }
}
