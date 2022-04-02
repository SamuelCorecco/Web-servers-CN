import Server.Server;
import static Server.Print.*;

public class Main {
    
    public static void main(String[] args) throws Exception {
        
        // TODO: Setup server and start it
        
        // Parse arguments
        int port = 8080;
        
        if(args.length > 1) {
            error("Wrong arguments bitch");
        }

        if(args.length == 1) {
            String command = args[0];
            if(command.startsWith("-port=")) {
                // TODO: Set new port
                try {
                    port = Integer.parseInt(args[0].substring(6));
                    print(port);
                } catch (NumberFormatException e) {
                    //TODO: handle exception
                    error("No port number specified");
                }

            } else if(command.equals("-help")) {
                // TODO: Print help
                print("Here's some help!");

            } else {
                error("Unknown command");
            }
        }

        // Create server
        Server server = new Server(port);
        if(server.setUp()) {
            server.start();
        } else {
            error("Something went wrong when setting up server.");
        }

    }

}
