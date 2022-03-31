import java.util.HashMap;

public class HTTPResponse{
    // Status line
    private String version;
    private StatusCode status;
    // Headers
    private HashMap<String, String> headers;
    //Body
    private byte[] body;
    //the Server
    private final Server server;

}