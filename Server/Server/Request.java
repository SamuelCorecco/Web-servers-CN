package Server.Server;

import java.util.HashMap;
import java.util.Optional;

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

    public Request(Server server) {
        this.server = server;
        this.headerLines = new HashMap<>();
        this.requestLine = new HashMap<>();
    }

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

    public boolean ParseHeaderLines(final String headerLine) {
        String[] headerFields = headerLine.split(": ");
        if (headerFields.length < 2) {
            this.badRequest = true;
            return false;
        } else {
            if(isValidHeader(headerFields[0])) {
                headerLines.computeIfAbsent(headerFields[0], k -> headerFields[1]);
            } else {
                this.badRequest = true;
                return false;
            }
        }
        return true;
    }

    public boolean isValidHeader(String header) {
        for (String validHeader: validHeaders) {
            if (validHeader.equals(header)) {
                return true;
            }
        }
        return false;
    }

    public Optional<String> getHeaderContent(final String headerField) {
        if (headerLines.get(headerField) == null) {
            return Optional.empty();
        } else {
            return Optional.of(headerLines.get(headerField));
        }
    }

    public void checkHostNotNull() {
        if (headerLines.get("Host") == null) {
            headerLines.put("Host", server.getDefaultHost());
        }
    }

    public boolean isBodyRequired() {
        return requestLine.get("Method").equals("PUT");
    }

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
        // if (getHeaderContent("Host").isPresent() && !server.validDomain(getHeaderContent("Host").get())) {
        //     badRequest = true;
        //     return false;
        // }
        return true;
    }

    public boolean isBadRequest() {
        return this.badRequest;
    }

    public int getContentLength() {
        if (headerLines.get("Content-Length") == null) {
            return -1;
        } else {
            return Integer.parseInt(headerLines.get("Content-Length"));
        }
    }

    public String getMethod() {
        return requestLine.get("Method");
    }

    public String getURL() {
        return requestLine.get("URL");
    }

    public String getVersion() {
        return requestLine.get("Version");
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setURL(String URL) {
        this.requestLine.put("URL", URL);
    }

    public void setHeaderField(String headerField, String headerContent) {
        this.headerLines.put(headerField, headerContent);
    }

    // TODO: Delete
    public void setVersion(String v) {
        this.requestLine.put("Version",v);
    }

    // TODO: Delete
    public void setMethod(String m) {
        this.requestLine.put("Method",m);
    }

    // TODO: Delete
    public void setHost(String h) {
        this.headerLines.put("Host",h);
    }

    public String getHost() {
        checkHostNotNull();
        return headerLines.get("Host");
    }

}
