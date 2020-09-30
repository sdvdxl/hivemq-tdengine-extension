package top.todu.hivemq.extensions.tdengine.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.dto.TdEngineHttpResponse;

/** @author du */
public class HttpUtil {
  private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
  private static final int HTTP_STATUS_200 = 200;
  private static final int HTTP_STATUS_300 = 300;
  private OkHttpClient okHttpClient;
  private URL url;
  private String authToken;

  private HttpUtil() {}

  public static HttpUtil newInstance(
      String url, String authToken, int poolSize, int timeoutOfSecs, int maxRequestsPerHost) {
    ConnectionPool connectionPool = new ConnectionPool(poolSize, 30, TimeUnit.SECONDS);
    Dispatcher dispatcher = new Dispatcher();
    dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
    dispatcher.setMaxRequests(maxRequestsPerHost);
    OkHttpClient client =
        new OkHttpClient()
            .newBuilder()
            // 读超时
            .readTimeout(Duration.ofSeconds(timeoutOfSecs))
            // 写超时
            .writeTimeout(Duration.ofSeconds(timeoutOfSecs))
            //
            .callTimeout(Duration.ofSeconds(timeoutOfSecs))
            // 连接池
            .connectionPool(connectionPool)
            // 连接超时
            .connectTimeout(Duration.ofSeconds(timeoutOfSecs))
            // 不重试
            .retryOnConnectionFailure(false)
            .dispatcher(dispatcher)
            .build();
    HttpUtil httpClientUtil = new HttpUtil();
    httpClientUtil.okHttpClient = client;
    try {
      httpClientUtil.url = new URL(url);
      httpClientUtil.authToken = authToken;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return httpClientUtil;
  }

  public static HttpUtil newInstance(String url, String authToken) {
    return newInstance(url, authToken, Runtime.getRuntime().availableProcessors(), 3, 100);
  }

  private static boolean is2xx(int code) {
    return code >= HTTP_STATUS_200 && code < HTTP_STATUS_300;
  }

  public void close() {
    okHttpClient.connectionPool().evictAll();
    okHttpClient = null;
  }

  public boolean post(String sql) {
    Request request =
        new Request.Builder()
            .url(url)
            .header("User-Agent", "http-hivemq-tdengine-extension")
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic " + authToken)
            .post(RequestBody.create(sql.getBytes(StandardCharsets.UTF_8)))
            .build();

    try (Response response = this.okHttpClient.newCall(request).execute()) {
      byte[] bytes = response.body().bytes();

      if (!HttpUtil.is2xx(response.code())) {
        String msg = new String(bytes);
        log.error("execute sql error, sql:{}, error:{}", sql, msg);
        return false;
      }

      TdEngineHttpResponse resp = JsonUtil.fromBytes(bytes, TdEngineHttpResponse.class);
      if (!resp.isSuccess()) {
        String msg = new String(bytes);
        log.error("execute sql error, sql:{}, error:{}", sql, msg);
        return false;
      }
      return true;
    } catch (Exception e) {
      log.error("execute sql error, sql:" + sql + ", error:" + e.getMessage(), e);
      return false;
    }
  }
}
