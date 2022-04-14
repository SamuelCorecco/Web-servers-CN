package src.Server;

import java.util.HashMap;
import java.util.Optional;
import static src.Server.Print.*;

public class Request {
    private final String[] validHeaders = {
            "Connection",
            "Content-length",
            "Content-type",
            "Host",
            "Date"
    };
    private final Server server;
    private boolean badRequest;
    private HashMap<String, String> requestLine;
    private HashMap<String, String> headerLines;
    private byte[] body;

    /**
     * Request constructor
     * @param server server to which this request belongs
     */
    public Request(Server server) {
        this.server = server;
        this.headerLines = new HashMap<>();
        this.requestLine = new HashMap<>();
    }

    /**
     * It parses the first line of the request, assigning the information retrieved to the relative request fields.
     * @param requestLine is the line containing the main information about the request (method, version and URL)
     * @return true if the line is parsed correctly, false otherwise
     */

    public boolean ParseRequestLine(final String requestLine) {
        String[] requestFields = requestLine.split(" ");
        if (requestFields.length != 3) {
            this.badRequest = true;
            return false;
        } else {
            this.requestLine.put("Method", requestFields[0]);
            this.requestLine.put("URL", requestFields[1]);
            this.requestLine.put("Version", requestFields[2]);
            return true;
        }
    }

    /**
     * It parses the header lines of the request, assigning the information retrieved to the relative header fields.
     * @param headerLine is the line containing the header lines information (e.g. Content-Length)
     * @return true if the header lines are parsed correctly, false otherwise
     */

    public boolean ParseHeaderLines(final String headerLine) {
        String[] headerFields = headerLine.split(": ");
        if (headerFields.length != 2) {
            this.badRequest = true;
            return false;
        } else {
            if(isValidHeader(headerFields[0])) {
                if(headerFields[0].equals("Host")) {
                    headerLines.put("Host", headerFields[1].split(":")[0]);
                } else {
                    headerLines.computeIfAbsent(headerFields[0], k -> headerFields[1]);
                }
            } else {
                //TODO: ERROR
                // headerLine = Accept: image/avif,image/webp,*/*
                // headerFields[0] = Accept
                //this.badRequest = true;
                //NO bad request se non conosci l'header
            }
        }
        return true;
    }

    /**
     * Checks if the header field is valid or not.
     * @param header is the header field to check
     * @return true if the header field is valid, false otherwise
     */

    public boolean isValidHeader(String header) {
        for (int i = 0; i < validHeaders.length; ++i) {
            String validHeader = validHeaders[i];
            if (validHeader.equals(header)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It retrieves the content of a header field.
     * @param headerField is the header field whose content has to be retrieved
     * @return an Optional.empty() if the specified header field does not have content mapped to it, the header content associated with it otherwise
     */

    public Optional<String> getHeaderContent(final String headerField) {
        if (headerLines.get(headerField) == null) {
            return Optional.empty();
        } else {
            return Optional.of(headerLines.get(headerField));
        }
    }

    /**
     * It checks if the Host header field is not null, if it is it sets it as the default Host (only for HTTP/1.0).
     * Useful for the HTTP/1.0 version, in which a default host has to be set in case the Host header is null.
     */

    public void checkHostNotNull() {
        if (headerLines.get("Host") == null) {
            headerLines.put("Host", server.getDefaultHost());
        }
    }

    /**
     * It checks if the method specified in the request needs to have a body.
     * @return true if the method needs a body (PUT), false otherwise
     */

    public boolean isBodyRequired() {
        return requestLine.get("Method").equals("PUT");
    }

    /**
     * It checks if the request violates some constraints, such as:
     * -The Host is null and the version is not HTTP/1.0
     * -The method is PUT and there is not a body (in HTTP/1.0)
     * -The specified host doesn't exist
     * @return true if the request does not violate the constraints, false otherwise
     */

    public boolean checkRequest() {
        if (badRequest) {
            return false;
        }
        if (this.requestLine.get("Version").equals("HTTP/1.1") && headerLines.get("Host").isEmpty()) {
            this.badRequest = true;
            return false;
        }
        if (this.requestLine.get("Method").equals("PUT") && this.requestLine.get("Version").equals("HTTP/1.0") && getHeaderContent("Content-Length").isEmpty()) {
            this.badRequest = true;
            return false;
        }
         if (getHeaderContent("Host").isPresent() && !server.validDomain(getHeaderContent("Host").get())) {
                badRequest = true;
             return false;
         }
        return true;
    }

    /**
     * It checks if the request is not valid.
     * @return true if the request is a bad one
     */

    public boolean isBadRequest() {
        return this.badRequest;
    }

    /**
     * It retrieves the Content-Length header value.
     * @return -1 if there is not a value mapped to Content-Length header, the Content-Length header value otherwise
     */

    public int getContentLength() {
        if (headerLines.get("Content-Length") == null) {
            return -1;
        } else {
            int len = Integer.parseInt(headerLines.get("Content-Length"));
            return len;
        }
    }

    /**
     * It retrieves the value associated with the request field method.
     * @return the String associated with the method request field
     */

    public String getMethod() {
        return requestLine.get("Method");
    }

    /**
     * It retrieves the value associated with the request field URL.
     * @return the String associated with the URL request field
     */

    public String getURL() {
        return requestLine.get("URL");
    }

    /**
     * It retrieves the value associated with the request field version.
     * @return the String associated with the version request field
     */

    public String getVersion() {
        return requestLine.get("Version");
    }

    /**
     * It retrieves the body of the request.
     * @return the byte[] which represents the body of the request
     */

    public byte[] getBody() {
        return body;
    }

    /**
     * It sets the body content.
     * @param body is the byte[] representing the body content
     */

    public void setBody(byte[] body) {
        // TODO: Delete everything except last line
        this.headerLines.put("Content-Type", "text/html"); // TODO: handle other types
        String len = Integer.toString(new String(body).length());
        this.headerLines.put("Content-Length", len);
        this.body = body;
    }

    /**
     * It sets the URL request field.
     * Needed to change the URL on the fly (e.g. in case of "/" being requested.
     * @param URL the new URL to assign to the method request field
     */

    public void setURL(String URL) {
        this.requestLine.put("URL", URL);
    }

    /**
     * It maps the header field with the header value.
     * @param headerField the header field we want to assign the value to
     * @param headerContent the header content we want to assign to the header field
     */

    public void setHeaderField(String headerField, String headerContent) {
        this.headerLines.put(headerField, headerContent);
    }

    /**
     * It retrieves the Host header value, if the HTTP version is 1.0, we set the default Host value if there is not a current value.
     * @return the String representing the Host header value
     */

    public String getHost() {
        if (getVersion().equals("HTTP/1.0")) {
            checkHostNotNull();
        }
        return headerLines.get("Host");
    }

    /**
     * Check whether request is bad
     * @return if request is bad (true) or not (false)
     */
    public boolean getBadRequest() {
        return badRequest;
    }

}
