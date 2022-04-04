package Server;

import java.io.IOException;
import java.nio.file.*;
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
    private final Server server;

    // TODO: Delete
    // Dummy constructor to test responses
    public Response() throws Exception {
        this.server = new Server(8080);
        Request q = new Request(this.server);
        q.setVersion("HTTP/1.0");
        q.setMethod("GET");
        q.setURL("/");
        q.setHost("samuelcorecco.ch");
        this.request = q;
        headers = new LinkedHashMap<>();
        addStatus(StatusCode.OK);
        addDate();
        addServer();
        String body = "The administrator of guyincognito.ch is Guy Incognito.\n" + 
                        "You can contact him at guy.incognito@usi.ch.";
        addBody(body, "text/plain");

        handle();
    }

    public Response(Request req, Server s) {
        this.server = s;
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
    private void addBody(String body, String ctype) {
        String len = Integer.toString(body.length());
        headers.put(ResponseHeader.CLEN, len);
        headers.put(ResponseHeader.CTYPE, ctype);
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
            case "PUT":
                answerPut();
                break;
            case "DELETE":
                answerDelete();
                break;
            case "NTW22INFO":
                answerNTW22INFO();
                break;
            default:
                // Unknown request method
                addStatus(StatusCode.NOT_IMPLEMENTED);
                break;
        }
    }

    // ===== Request Type Handling =====

    /**
     * Handles a GET request
     */
    private void answerGet() {
        //TODO
        String request_host = this.request.getHost();
        //print("Host is " + request_host);
        String request_resource = this.request.getURL();
        if(request_resource == "/") {
            request_resource =  server.getEntryPoint(request_host);
        }
        String filePath = request_host + "/" + request_resource;
        //print("filepath is: " + filePath);

        //print(filePath);

        Path path = Paths.get(filePath);
        if(Files.exists(path)) {
            if(isValidGetResource(path)) {
                addStatus(StatusCode.OK);
                addDate();
                addServer();
                String ext = getFileExtension(request_resource);
                addBody(getFileContent(path), ext);
            } else {
                // TODO: not a valid resource to return
            }
        } else {
            print("Cannot serve " + filePath + " to client");
            addStatus(StatusCode.NOT_FOUND);
        }
        print("");
    }

    /**
     * Checks whether a given path contains a resource that's allowed to be returned by
     * a GET request.
     */
    private boolean isValidGetResource(Path path) {
        // TODO: Should check if resource is valid
        return true;
    }

    /**
     * Returns the file's extension and its corresponding HTTP content-type.
     * @return a file's extension (0) and content-type (1)
     */
    private String getFileExtension(String fileName) {
        final String ext = fileName.split("\\.")[1];
        String ctype;
        switch (ext) {
            case "html":
                ctype = "text/html";
                break;
            case "css":
                ctype = "text/css";
                break;
            case "jpg":
            case "jpeg":
                ctype = "image/jpeg";
                break;
            case "png":
                ctype = "image/png";
            default:
                ctype = "text/plain";
                break;
        }
        return ctype;
    }

    /**
     * Returns the contents of an HTML file in String format.
     * @return text inside HTML file
     */
    private String getFileContent(Path path){
        String body = "";
        try {
            body = Files.readString(path);
        } catch (IOException e) {
            error("Something went wrong reading " + path.getFileName());
        }
        return body;
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