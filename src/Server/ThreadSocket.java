package Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadSocket implements Runnable{

    private final Socket socket;
    private final Server server;
    private Request request;

    public ThreadSocket(final Socket socket, final Server server) {
        this.socket = socket;
        this.server = server;
    }

    private void handleRequest() throws IOException {
        Response response;
        do {
            request = new Request(server);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            String requestLine = readByteStream(in);
            if (request.ParseRequestLine(requestLine)) {
                while(!(requestLine.equals(""))) {
                    request.ParseHeaderLines(requestLine);
                }
            }
            if (!request.isBadRequest() && request.isBodyRequired()) {
                byte[] body = in.readNBytes(request.getContentLength());
                request.setBody(body);
            }
            response = new Response(server,request, true); 
            out.write(response.toByteArray());
            out.flush();
            System.out.println(response.getStatusString() + " " + request.getMethod() + " " +request.getURL() + " " + request.getVersion());//TODO getStatus for respons
        } while (!response.getIsLast()); //TODO implement method isLast
        socket.close(); //
    }

    private String readByteStream(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int first = in.read();
        int second = in.read();
        while (first != '\r' || second != '\n') {
            baos.write(first);
            first = second;
            second = in.read();
        }
        return baos.toString();
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
