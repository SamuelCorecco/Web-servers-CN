package Server;
import java.io.IOException;

public class Main {
    static Server server;
    public static void main(String[] args) throws IOException, Exception {
       if (args.length > 1) {
          System.err.println("Bad Format: usage java <port-number>");
          return;
       }
       try {
           if(args.length == 1){
                server = new Server(Integer.parseInt(args[0]));
           }else{
                server =  new Server(80);
           }
       } catch (IOException e) {
          System.err.println("error while creating the server :(");
          e.printStackTrace();
       }
       if (server.setUp()) {
          server.start();
       } else {
          System.err.println("Could not setup Server to run; check that vhosts.conf file is present and well formatted");
       }
    }
 }