package src.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import static src.Server.Print.*;

public class ThreadSocket implements Runnable{

    private final Socket socket;
    private final Server server;
    private Request request;

    public ThreadSocket(final Socket socket, final Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Print a line of the request to the terminal. For testing purposes
     * @param input input stream of request
     * @return line of the request
     * @throws IOException
     */
    private String readLineOfBytes(InputStream input) throws IOException {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        int b = input.read();
        int c = input.read();
        while (!(b == '\r' && c == '\n')) {
            bStream.write(b);
            b = c;
            c = input.read();
                 }
        print(bStream.toString());
        return bStream.toString();
    }

    /**
     * Handles a request
     * @throws IOException
     */
    private void handleRequest() throws IOException {
        Response response;
        do {
            request = new Request(server);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            print("");
            String requestLine = readLineOfBytes(in);
            if (request.ParseRequestLine(requestLine)) {
                while(!(requestLine = readLineOfBytes(in)).equals("")) {
                    request.ParseHeaderLines(requestLine);
                }
            }
            if (!request.isBadRequest() && request.isBodyRequired()) {
                byte[] body = in.readNBytes(request.getContentLength());
                request.setBody(body);
            }
            response = new Response(server,request, true);
            //print(response.getStatusString());
            response.printResponseInfo();
            out.write(response.toByteArray());
            out.flush();
        } while (!response.getIsLast() || request.getVersion().equals("HTTP/1.0")); 
        socket.close(); 
    }

    @Override
    public void run() {
        try{
            handleRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
