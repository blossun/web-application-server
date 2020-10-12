package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        log.debug("request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }
        method = HttpMethod.valueOf(tokens[0]);
        if (method == HttpMethod.POST) {
            path = parseDefaultUrl(tokens);
            return;
        }

        int index = parseDefaultUrl(tokens).indexOf("?");
        if (index == -1) { //GET에 queryString이 있는 경우
            path = parseDefaultUrl(tokens);
            return;
        }

        //GET에 queryString이 없는 경우
        path = parseDefaultUrl(tokens).substring(0, index);
        params = HttpRequestUtils.parseQueryString(parseDefaultUrl(tokens).substring(index + 1));
    }

    private String parseDefaultUrl(String[] tokens) {
        String url = tokens[1];
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
