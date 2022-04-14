package src.Server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;

import static src.Server.Print.*;

public class Response {

    private enum ResponseHeader {
        STATUS(""),DATE("Date: "),SERVER("Server: "),CLEN("Content-Length: "),
        CTYPE("Content-Type: "),CLOC("Content-Location: ");
        private final String s;
        private ResponseHeader(String s) {this.s = s;};
        public String getHeader() {return this.s;}
    }

    private enum StatusCode {
        OK("200 OK"),CREATED("201 Created"),NO_CONTENT("204 No Content"),
        BAD_REQUEST("400 Bad Request"),FORBIDDEN("403 Forbidden"),NOT_FOUND("404 Not Found"),
        METHOD_NOT_ALLOWED("405 Method Not Allowed"),INTERNAL_SERVER_ERROR("500 Internal Server Error"),NOT_IMPLEMENTED("501 Not Implemented"),
        HTTP_VERSION_NOT_SUPPORTED("505 HTTP Version Not Supported");
        private final String s;
        private StatusCode(String s) {this.s = s;}
        public String getStatus() {return this.s;}
    }

    private LinkedHashMap<ResponseHeader, String> headers;
    private byte[] body;
    private final Request request;
    private final Server server;
    private boolean isLast; 

    //  // TODO: DELETE Dummy constructor to test responses
    // public Response() throws Exception {
    //     this.isLast = true;
    //     this.server = new Server(8080);
    //     Request req = new Request(this.server);
    //     req.setVersion("HTTP/1.0");
    //     req.setMethod("PUT");
    //     req.setURL("/newfile.html");
    //     req.setHost("samuelcorecco.ch");
    //     String reqbodystring = new String("<html><body><h1>Hello!</h1></body></html>");
    //     req.setBody(reqbodystring.getBytes());
    //     this.request = req;
    //     headers = new LinkedHashMap<>();
    //     handle();
    // }

    /**
     * Creates a new Response for the given Server and Request, AND handles it, ie, it is
     * populated with the correct information to then turn it into a byte array.
     * @param s server
     * @param req request
     */
    public Response(Server s, Request req, boolean isLast) {
        this.server = s;
        this.request = req;
        this.isLast = isLast;
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
    private void addBody(byte[] body, String ctype) {
        String len = Integer.toString(new String(body).length());
        headers.put(ResponseHeader.CLEN, len);
        headers.put(ResponseHeader.CTYPE, ctype);
        this.body = body;
    }

    /**
     * Add content location header
     * Used: PUT
     */
    private void addContentLocation(String path) {
        headers.put(ResponseHeader.CLOC, path);
    }
        

    /**
     * Populates the response object with the appropiate headers and information
     * depending on the request.
     */
    private void handle() {
        if(!errorCheck()) {
            return;
        }
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
        String request_host = this.request.getHost();
        String request_resource = this.request.getURL();
        if(request_resource.equals("/")) {
            request_resource =  server.getEntryPoint(request_host);
        }
        String filePath = request_host + "/" + request_resource;
        // print("filepath is: " + filePath);

        if(FileHandler.checkFileExists(request_host, request_resource)) {
            try {
                byte[] content = FileHandler.getFileContent(request_host, request_resource);
                addStatus(StatusCode.OK);
                addDate();
                addServer();
                String ctype = FileHandler.getMimeType(request_resource);
                addBody(content, ctype);
            } catch (IOException e) {
                // Something went wrong when reading the file
                print("Something went wrong when reading the file");
                addStatus(StatusCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            print("Cannot serve " + filePath + " to client because it DNE");
            addStatus(StatusCode.NOT_FOUND);
        }
        print("");
    }

    /**
     * Handles a PUT request.
     */
    private void answerPut() {
        String request_host = this.request.getHost();
        String request_resource = this.request.getURL();
        if(request_resource == "/") {
            request_resource =  server.getEntryPoint(request_host);
        }

        // get body up to character # specified in length and read
        byte[] body = Arrays.copyOfRange(this.request.getBody(), 0, this.request.getContentLength());

        if(FileHandler.checkFileExists(request_host, request_resource)) {
            // update (ie delete + add)
            try {
                FileHandler.deleteFile(request_host, request_resource);
                FileHandler.createFile(request_host, request_resource, body);
                addStatus(StatusCode.NO_CONTENT); // 204
            } catch (IOException e) {
                print("Something went wrong when updating the file " + request_resource);
                addStatus(StatusCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            // create new file
            try {
                FileHandler.createFile(request_host, request_resource, body);
                addStatus(StatusCode.CREATED); // 201
                addContentLocation(request_resource); 
            } catch (IOException e) {
                print("Something went wrong when creating the file " + request_resource);
                addStatus(StatusCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Handles a DELETE request
     */
    private void answerDelete() {
       if (FileHandler.checkFileExists(request.getHost(), request.getURL())) {
           try {
               FileHandler.deleteFile(request.getHost(), request.getURL());
               addStatus(StatusCode.NO_CONTENT);
           } catch (FileNotFoundException e) {
               print("The file has not been found");
               addStatus(StatusCode.NOT_FOUND);
           }   
       };
    }

    /**
     * Handles the NTW22INFO request
     */
    private void answerNTW22INFO() {
        if (server.getDomains().checkHostExists(request.getHost()) != -1) {
            addStatus(StatusCode.OK);
            addDate();
            addServer();
            String bodyContent = "The administrator of " + request.getHost() + "is " + server.getDomains().getMemberName(request.getHost()) + ".\n"
            + "You can contact him at " + server.getDomains().getMemberEmail(request.getHost()) + ".";
            byte[] body = bodyContent.getBytes();
            addBody(body, "text/plain");
        } else {
            addStatus(StatusCode.NOT_FOUND);
        }
    }

    /**
     * Convert the String representation of the response into a byte array.
     * @return byte array of HTTP response
     */
    public byte[] toByteArray() {
        String str = responseToString() + "\r\n";
        byte[] content = str.getBytes();
        // array concat: https://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
        byte[] both = content;
        if(this.body != null) {
            both = Arrays.copyOf(content, content.length + this.body.length);
            System.arraycopy(body, 0, both, content.length, body.length);
        }
        return both;   
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
            response += line;
        }
        String ctype = headers.get(ResponseHeader.CTYPE);
        if(ctype != null && !ctype.startsWith("image")) {
            response += "\r\n" + new String(this.body);
        }
        return response;
    }

    /**
     * Checks whether this response is the last one
     * @return if response is the last or not
     */
    public boolean getIsLast() {
        return this.isLast;
    }
    
    /**
     * Get the response's status (version + status code)
     * @return response status as a string
     */
    public String getStatusString() {
        return headers.get(ResponseHeader.STATUS);
    }

    /**
     * General check method for setting error statuses
     * @return whether errors were found (false) or the request is good (true)
     */
    public boolean errorCheck() {
        boolean isGood = false;
        if (request.isBadRequest()) {
            addStatus(StatusCode.BAD_REQUEST); // 400
        } else if (!request.getVersion().equals("HTTP/1.0") && !request.getVersion().equals("HTTP/1.1")) {
            addStatus(StatusCode.HTTP_VERSION_NOT_SUPPORTED); // 505
        } else if (!FileHandler.validPermission(request.getHost(), request.getURL())) {
            addStatus(StatusCode.FORBIDDEN); // 403
            addDate();
        } else {
            isGood = true;
        }
        this.isLast = !isGood;
        return isGood;
    }
}