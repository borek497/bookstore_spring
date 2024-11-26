package pl.borek497.bookstore.helpers;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TextRequestContextHelper {

    static void setRequestContext(String requestUri, String serverName, int port, String protocol) {
        var requestMock = new MockHttpServletRequest("POST", requestUri);
        requestMock.setServerName(serverName);
        requestMock.setServerPort(port);
        requestMock.setScheme(protocol);

        var attributes = new ServletRequestAttributes(requestMock);
        RequestContextHolder.setRequestAttributes(attributes);
    }
}