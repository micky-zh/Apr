package use.demo;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengfan on 2016/11/29 0029.
 */
public abstract class BaseDownLoad {

    final int timeout = 6000;

    protected final Map<String, String> header = new HashMap<>();

    protected Connection initConnection(String url) {
        return Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .timeout(timeout)
                .followRedirects(false)
                .ignoreContentType(true).validateTLSCertificates(false);

    }

    {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        header.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        header.put("Cache-Contro", "max-age=0");
        header.put("Accept-Encoding", "gzip");

    }

    protected Connection initConnectionWithHeader(String url){
        Connection c = initConnection(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            c.header(entry.getKey(),entry.getValue());
        }
        return c;
    }

    protected String regex(String regex, String body, int group) {
        Matcher m = Pattern.compile(regex).matcher(body);
        if (m.find()) {
            return m.group(group);
        }
        return null;
    }

    public HttpClient getHttpClient() {
        HttpParams params = new SyncBasicHttpParams();
        params.setParameter(HttpProtocolParams.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        params.setBooleanParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
                true);
        params.setBooleanParameter(HttpConnectionParams.STALE_CONNECTION_CHECK,
                true);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE,
                8 * 1024);
        params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 20000);
        params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);
        params.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 1000l); //该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
        params.setBooleanParameter(HttpConnectionParams.TCP_NODELAY, true);
        params.setBooleanParameter(HttpConnectionParams.SO_REUSEADDR, true);

        PoolingClientConnectionManager mgr = new PoolingClientConnectionManager();
        mgr.setMaxTotal(2000);
        mgr.setDefaultMaxPerRoute(500);

        DefaultHttpClient httpclient = new DefaultHttpClient(mgr, params);
        httpclient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        HttpClientParams.setCookiePolicy(httpclient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
        HttpProtocolParams.setUserAgent(httpclient.getParams(),
                "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.18) Gecko/20110628 Ubuntu/10.04 (lucid) Firefox/3"
                        + ".6.18");

        //创建SSLSocketFactory
        try {
            SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                    return true;
                }
            }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
        } catch (KeyManagementException e) {
            throw new SecurityException(e.getMessage(), e);
        } catch (KeyStoreException e) {
            throw new SecurityException(e.getMessage(), e);
        } catch (UnrecoverableKeyException e) {
            throw new SecurityException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage(), e);
        }

        return httpclient;
    }

    public class DynamicProxyRoutePlanner implements HttpRoutePlanner {

        private DefaultProxyRoutePlanner defaultProxyRoutePlanner = null;

        public DynamicProxyRoutePlanner(HttpHost host){
            defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(host);
        }

        public void setProxy(HttpHost host){
            defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(host);
        }

        public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context)
                throws HttpException {
            return defaultProxyRoutePlanner.determineRoute(target,request,context);
        }
    }

    static HttpHost proxy = new HttpHost("127.0.0.1", 80);
    DynamicProxyRoutePlanner routePlanner = new DynamicProxyRoutePlanner(proxy);

    private CloseableHttpClient getInstanceClient() {
        CloseableHttpClient httpClient;
        //StandardHttpRequestRetryHandler standardHandler = new StandardHttpRequestRetryHandler(2, true);
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {

            public boolean retryRequest(IOException arg0, int retryTimes, HttpContext arg2) {
                if (arg0 instanceof UnknownHostException || arg0 instanceof ConnectTimeoutException
                        || !(arg0 instanceof SSLException) || arg0 instanceof NoHttpResponseException) {
                    return true;
                }
                if (retryTimes > 1) {
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(arg2);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
                    return true;
                }
                return false;
            }
        };
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setSocketTimeout(1000)
                .setConnectionRequestTimeout(3000)
                .build();
        // 设置代理ip

        httpClient = HttpClients.custom().setRoutePlanner(routePlanner).setRetryHandler(handler)
                .setConnectionTimeToLive(1, TimeUnit.MINUTES).setDefaultRequestConfig(defaultRequestConfig)
                .build();

        return httpClient;
    }

}
