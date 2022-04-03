package Server;

import java.util.HashMap;
import static Server.Print.*;

public class Response {

    private enum ResponseHeader {
        STATUS(""),DATE("Date: "),SERVER("Server: "),CLEN("Content-Length: "),
        CTYPE("Content-Type: "),CLOC("Content-Location: "),BODY("");
        public final String s;
        private ResponseHeader(String s) {this.s = s;};
    }

    private enum StatusCode {
        OK("200 OK"),CREATED("201 Created"),BAD_REQUEST("400 Bad Request"),
        FORBIDDEN("403 Forbidden"),NOT_FOUND("404 Not Found"),
        METHOD_NOT_ALLOWED("405 Method Not Allowed"),NOT_IMPLEMENTED("501 Not Implemented"),
        BAD_GATEWAY("502 Bad Gateway");
        public final String s;
        private StatusCode(String s) {this.s = s;}
    }

    private HashMap<ResponseHeader, String> headers;
    private byte[] body;
    private final Request request;

    public Response(Request req) {
        this.request = req;
        headers = new HashMap<>();
        generate();

    }

    /**
     * Generates the appropiate response based on the request.
     */
    public void generate() {
        switch (this.request.getMethod()) {
            case "GET":
                answerGet();
                break;
            default:
                error("Unknown request method");
                break;
        }
    }

    /**
     * Creates the response status line based on the outcome of handling the request
     * @return status line with code and version
     */
    private String generateStatusLine() {
        //TODO
        return null;
    }

    /**
     * Generates the message body of the response.
     * Note: Only used when handling a GET request.
     * @return message body
     */
    private String generateBody() {
        //TODO
        return null;
    }

    private String generateDate() {
        //TODO
        return null;
    }

    private String generateContentLength() {
        //TODO
        return null;
    }

    private String generateContentType() {
        //TODO
        return null;
    }

    private String generateContentLocation() {
        //TODO
        return null;
    }

    private String getServerName() {
        //TODO
        return null;
    }

    // ===== Request Type Handling =====

    /**
     * Handles a GET request
     * @return response string
     */
    private String answerGet() {
        //TODO
        return null;
    }

    /**
     * Handles a PUT request.
     */
    private void answerPut() {
        //TODO
    }

    private void answerDelete() {
        //TODO
    }

    private void answerNTW22INFO() {
        //TODO
    }

    // Testing Purposes
    public String toString() {
        String response = "";
        for(ResponseHeader h : headers.keySet()) {
            String line = h.s +  headers.get(h) + "\n";
            if(h == ResponseHeader.BODY) {
                line = "\n" + line; // CRLF
            }
            response.concat(line);
        }
        return response;
    }

}
