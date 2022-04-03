package Server;

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
    private String method;
    private String URL;
    private String version;
    private HashMap<String, String> headerLines;
    private byte[] body;

    public Request(Server server) {
        this.server = server;
        this.headerLines = new HashMap<>();
    }

    public boolean ParseRequestLine(final String requestLine) {
        String[] requestFields = requestLine.split(" ");
        if (requestFields.length != 3) {
            return false;
        } else {
            this.method = requestFields[0];
            this.URL = requestFields[1];
            this.version = requestFields[2];
            return true;
        }
    }

    public boolean ParseHeaderLines(final String headerLine) {
        String[] headerFields = headerLine.split(": ");
        if (headerFields.length < 2) {
            return false;
        } else {
            if(isValidHeader(headerFields[0])) {
                headerLines.computeIfAbsent(headerFields[0], k -> headerFields[1]);
            } else {
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
//            headerLines.put("Host", server.setDefaultHost());
//            Questa linea e' da scommenater quando nel server verra' aggiunto un default host da passare all'HTTP 1.0 in caso di null Host.
        }
    }



    public String getMethod() {
        return method;
    }

    public String getURL() {
        return URL;
    }

    public String getVersion() {
        return version;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
