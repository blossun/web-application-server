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

    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
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

            processRequestLine(line);

            this.headers.put("Content-Length", "0");
            line = br.readLine();
            while (line != null && !line.equals("")) { //헤더 //line.equals("")만 있을 때, GET 메세지에서 NPE가 발생해서 조건 추가
                log.debug("header : {}", line);
                HttpRequestUtils.Pair header = parseHeader(line);
                if (header != null) {
                    headers.put(header.getKey(), header.getValue());
                }
                line = br.readLine();
            }

            if (this.method.equals("POST")) {
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                this.params = HttpRequestUtils.parseQueryString(body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processRequestLine(String requestLine) {
        log.debug("request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        this.method = tokens[0];
        // GET인우, POST인 경우
        if (this.method.equals("GET")) {
            this.path = parseDefaultUrl(tokens);
            if (path.contains("?")) {
                this.path = parseDefaultUrl(tokens).substring(0, tokens[1].indexOf("?"));
                this.params = HttpRequestUtils.parseQueryString(parseDefaultUrl(tokens).substring(tokens[1].indexOf("?") + 1));
            }
        } else {
            this.path = parseDefaultUrl(tokens);
        }
    }

    private String parseDefaultUrl(String[] tokens) {
        String url = tokens[1];
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }
}
