import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.message.BasicHeader
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.json.JSONObject

import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.script.GTest
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread


import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import org.ngrinder.http.HTTPRequest
import org.ngrinder.http.HTTPRequestControl
import org.ngrinder.http.HTTPResponse
import org.ngrinder.http.cookie.Cookie
import org.ngrinder.http.cookie.CookieManager

@RunWith(GrinderRunner)
class TestRunner {

    public static GTest test
    public static HTTPRequest request
    public static Map<String, String> headers = [:]
    public static Map<String, Object> params = [:]
    public static List<Cookie> cookies = []

    @BeforeProcess
    public static void beforeProcess() {
        // System.setProperty("javax.net.debug", "ssl:handshake");
        System.setProperty("jsse.enableSNIExtension", "true");
        HTTPRequestControl.setConnectionTimeout(300000)
        test = new GTest(1, "instance-ip")
        request = new HTTPRequest()
        grinder.logger.info("before process.")
    }

    @BeforeThread
    public void beforeThread() {
        test.record(this, "test")
        grinder.statistics.delayReports = true
        grinder.logger.info("before thread.")

        // login 처리
        def loginRequest = new HTTPRequest()
        int userId = (grinder.threadNumber.intdiv(100)) + 1
        String password = "1234567890"
        String email = "user" + userId + "@te.st";
        String body = "{\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"password\": \"" + password + "\"\n" +
                "}"

        loginRequest.setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
        HTTPResponse loginResponse = loginRequest.POST("http://instance-ip/login", body.getBytes(), getPostHeader())

        String accessToken = new JSONObject(loginResponse.bodyText).getString("token")
        headers.put("Authorization", "Bearer " + accessToken)
    }

    @Before
    public void before() {
        request.setHeaders(headers)
        CookieManager.addCookies(cookies)
        grinder.logger.info("before. init headers and cookies")
    }

    @Test
    public void test() {
        HTTPResponse response = request.GET("http://*****/friends")
        if (response.statusCode == 301 || response.statusCode == 302) {
            grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
        } else {
            assertThat(response.statusCode, is(200))
        }
    }

    private static List<Header> getPostHeader(){
        List<Header> headers = new LinkedList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"))
        headers.add(new BasicHeader("Accept", "application/json"))
        return headers
    }
}