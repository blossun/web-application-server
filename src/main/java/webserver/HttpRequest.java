package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.parseHeader;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            requestLine = new RequestLine(line);

            this.headers.put("Content-Length", "0");
            line = br.readLine();
            while (!line.equals("")) { //헤더 //line.equals("")만 있을 때, GET 메세지에서 NPE가 발생해서 조건 추가
                log.debug("header : {}", line);
                HttpRequestUtils.Pair header = parseHeader(line);
                if (header != null) {
                    headers.put(header.getKey(), header.getValue());
                }
                line = br.readLine();
            }

            if ("POST".equals(getMethod())) { //POST 경우, params는 body값으로 파싱
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
                return ;
            }
            //GET 경우, params는 RequestLine의 QueryString값으로 파싱
            params = requestLine.getParams();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

}
