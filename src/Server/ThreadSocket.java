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
            response = new Response(server); //TODO constructor of Response should take a server
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            String requestLine = readByteStream(input);
            if (request.ParseRequestLine(requestLine)) {
                while(!(requestLine.equals(""))) {
                    request.ParseHeaderLines(requestLine);
                }
            }
            if (!request.isBadRequest() && request.isBodyRequired()) {
                byte[] body = in.readNBytes(request.getContentLength());
                request.setBody(body);
            }
            response = new Response(server).handleRequest(request); //TODO implement a handlerequest method in Repsonse class
            out.write(response.toStringMod()); //TODO implement a toString function that returns a byte[] in Response class
            out.flush();
            System.out.println(response.getStatus() + " " + request.getMethod() + " " +request.getURL() + " " + request.getVersion());//TODO getStatus for respons
        } while (!response.isLast()); //TODO implement method isLast
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
