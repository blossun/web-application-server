package webserver;

import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String url = request.getPath();
            if ("/user/create".equals(url)) {
                new CreateUserController().service(request, response);
            } else if ("/user/login".equals(url)) {
                new LoginController().service(request, response);
            } else if ("/user/list".equals(url)) {
                new ListUserController().service(request, response);
            }
            response.forward(url);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
