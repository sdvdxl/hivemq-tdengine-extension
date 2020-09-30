package top.todu.hivemq.extensions.tdengine.config;

/**
 * rest config <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 00:10 <br>
 * @since 1.0.0
 */
public class RestConfig {

  private boolean enable;
  private String url;
  private String database;
  private String table;
  private String username;
  private String password;

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  @Override
  public String toString() {
    return "RestConfig{"
        + "enable="
        + enable
        + ", url='"
        + url
        + '\''
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
