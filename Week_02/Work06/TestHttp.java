import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
// 使用httpClient和OkHttp访问gateway-server-0.0.1-SNAPSHOT.jar
public class TestHttp {
    private String url = "http://localhost:8088/api/hello";

    public static void main(String[] args) {
        TestHttpRequest testHttpRequest = new TestHttpRequest();
        testHttpRequest.testHttpClient();
        testHttpRequest.testOkHttp();
    }

    // HttpClient
    public void testHttpClient() {
        //创建CloseableHttpClient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //创建Get请求
        HttpGet httpGet = new HttpGet(url);
        //响应
        CloseableHttpResponse response = null;
        try {
            //由客户端发送Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态码:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容:" + EntityUtils.toString(responseEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    // 测试OkHttp
    public void testOkHttp() {
        //得到OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //构造Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            //同步方法
            Response response = okHttpClient.newCall(request).execute();
            System.out.println("响应状态码：" + response.code());
            System.out.println("响应内容:" + new String(response.body().bytes()));
            //异步方法
            okHttpClient.newCall(request).enqueue(new Callback() {
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("---------Failure-----------");
                }

                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("响应状态码：" + response.code());
                    System.out.println("响应内容:" + new String(response.body().bytes()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}