package top.todu.hivemq.extensions.tdengine.dto;

/**
 * TDengine http rest response <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/30 15:32 <br>
 */
public class TdEngineHttpResponse {
  public static final String SUCCESS = "succ";
  private String status;

  public static String getSUCCESS() {
    return SUCCESS;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isSuccess() {
    return SUCCESS.equals(status);
  }
}
