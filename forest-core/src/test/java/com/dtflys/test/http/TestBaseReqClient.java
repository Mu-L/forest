package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.BaseReqClient;
import com.dtflys.test.http.client.BaseURLClient;
import com.dtflys.test.http.client.BaseURLVarClient;
import com.dtflys.test.mock.BaseUrlMockServer;
import com.dtflys.test.mock.GetMockServer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:12
 */
public class TestBaseReqClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    public final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final BaseReqClient baseReqClient;

    private final BaseURLClient baseURLClient;

    private final BaseURLVarClient baseURLVarClient;

    @Override
    public void afterRequests() {
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestBaseReqClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("baseURL", "http://localhost:5000/");
        configuration.setVariableValue("userAgent", USER_AGENT);
        configuration.setVariableValue("port", server.getPort());
        configuration.setVariableValue("baseURL", "http://localhost:" + server.getPort());
        baseReqClient = configuration.createInstance(BaseReqClient.class);
        baseURLClient = configuration.createInstance(BaseURLClient.class);
        baseURLVarClient = configuration.createInstance(BaseURLVarClient.class);
    }


    @Test
    public void testBaseGet() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseReqClient.simpleGet((data, request, response) -> {
            String userAgent = response.getRequest().getHeaderValue("User-Agent");
            assertNotNull(userAgent);
            assertEquals(BaseUrlMockServer.USER_AGENT, userAgent);
        }, "UTF-8"))
            .isNotNull()
            .isEqualTo(EXPECTED);
        mockRequest(server)
            .assertMethodEquals("GET")
            .assertPathEquals("/base/hello/user")
            .assertHeaderEquals("Accept-Charset", "UTF-8")
            .assertHeaderEquals("Accept", "text/plain")
            .assertHeaderEquals("User-Agent", USER_AGENT);
    }

    @Test
    public void testBaseGetWithoutBaseUrl() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseReqClient.simpleGetWithoutBaseUrl((data, request, response) -> {
            String userAgent = response.getRequest().getHeaderValue("User-Agent");
            assertNotNull(userAgent);
            assertEquals(BaseUrlMockServer.USER_AGENT, userAgent);
        }, "GBK"))
            .isNotNull()
            .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept-Charset", "GBK")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("User-Agent", USER_AGENT);
    }


    @Test
    public void testBaseGetWithEmptyUrl() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseReqClient.simpleGetWithEmptyPath("UTF-8"))
            .isNotNull()
            .extracting(ForestResponse::isSuccess, ForestResponse::getContent)
            .contains(true, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/base")
                .assertHeaderEquals("Accept-Charset", "UTF-8")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("User-Agent", USER_AGENT);
    }


    @Test
    public void testBaseURL() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseURLClient.simpleGet())
            .isNotNull()
            .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain");
    }

    @Test
    public void testBaseURLVar() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseURLVarClient.simpleGet())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain");
    }

}
