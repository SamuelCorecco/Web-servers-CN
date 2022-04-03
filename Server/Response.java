package Server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;

import static Server.Print.*;

public class Response {

    private enum ResponseHeader {
        STATUS(""),DATE("Date: "),SERVER("Server: "),CLEN("Content-Length: "),
        CTYPE("Content-Type: "),CLOC("Content-Location: "),BODY("");
        private final String s;
        private ResponseHeader(String s) {this.s = s;};
        public String getHeader() {return this.s;}
    }

    private enum StatusCode {
        OK("200 OK"),CREATED("201 Created"),NO_CONTENT("204 No Content"),
        BAD_REQUEST("400 Bad Request"),FORBIDDEN("403 Forbidden"),NOT_FOUND("404 Not Found"),
        METHOD_NOT_ALLOWED("405 Method Not Allowed"),NOT_IMPLEMENTED("501 Not Implemented"),
        BAD_GATEWAY("502 Bad Gateway");
        private final String s;
        private StatusCode(String s) {this.s = s;}
        public String getStatus() {return this.s;}
    }

    private LinkedHashMap<ResponseHeader, String> headers;
    private final Request request;

    // TODO: Delete
    // Dummy constructor to test responses
    public Response() throws Exception {
        Request q = new Request(new Server(8080));
        q.setVersion("HTTP/1.0");
        q.setMethod("GET");
        this.request = q;
        headers = new LinkedHashMap<>();
        addStatus(StatusCode.OK);
        addDate();
        addServer();
        String body = "The administrator of guyincognito.ch is Guy Incognito.\n" + 
                        "You can contact him at guy.incognito@usi.ch.";
        addBody(body);
    }

    public Response(Request req) {
        this.request = req;
        headers = new LinkedHashMap<>();
        handle();
    }

    /**
     * Get current time to include in "Date" header.
     * Code taken from: https://stackoverflow.com/questions/7707555/getting-date-in-http-format-in-java
     * @return current date and time in the HTTP format
     */
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    // ===== Response populating (Auxiliary) =====

    /**
     * Add status-line header
     * Used: All methods
     * @param status status code
     */
    private void addStatus(StatusCode status) {
        headers.put(ResponseHeader.STATUS, this.request.getVersion() + " " + status.getStatus());
    }

    /**
     * Add current date and time to response
     * Used: GET, DELETE, NTW22INFO
     */
    private void addDate() {
        headers.put(ResponseHeader.DATE, getCurrentTime());
    }

    /**
     * Add server header
     * Used: All methods
     */
    private void addServer() {
        headers.put(ResponseHeader.SERVER, "Up in the Clouds");
    }

    /**
     * Add Content-Length, Type, and Body to response
     * Used: GET
     * @param body message to answer to client
     */
    private void addBody(String body) {
        String len = Integer.toString(body.length());
        headers.put(ResponseHeader.CLEN, len);
        headers.put(ResponseHeader.CTYPE, "text/plain");
        headers.put(ResponseHeader.BODY, body);
    }

    /**
     * Add content location header
     * Used: PUT
     */
    private void addContentLocation() {
        // TODO
    }
        

    /**
     * Populates the response object with the appropiate headers and information
     * depending on the request.
     */
    private void handle() {
        switch (this.request.getMethod()) {
            case "GET":
                answerGet();
                break;
            default:
                // TODO: Give correct status code.
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

    /**
     * Convert the String representation of the response into a byte array.
     * @return byte array of HTTP response
     */
    public byte[] toByteArray() {
        return responseToString().getBytes();
    }

    /**
     * Prints formatted response to terminal.
     */
    public void printResponseInfo() {
        print(this.responseToString());
    }

    /**
     * Convert the Response object to a well formatted string.
     * @return response in string format
     */
    private String responseToString() {
        String response = "";
        for(ResponseHeader h : headers.keySet()) {
            String line = h.getHeader() +  headers.get(h) + "\r\n";
            if(h == ResponseHeader.BODY) {
                line = "\r\n" + line; // CRLF
            }
            response += line;
        }
        return response;
    }
}