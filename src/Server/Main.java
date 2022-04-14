package src.Server;
import static src.Server.Print.*;

public class Main {
    
    public static void main(String[] args) throws Exception {
        
        // Parse arguments
        int port = 8080;
        
        if(args.length > 1) {
            error("Wrong arguments bitch");
        }

        if(args.length == 1) {
            String command = args[0];
            if(command.startsWith("-port=")) {
                try {
                    port = Integer.parseInt(args[0].substring(6));
                    print(port);
                } catch (NumberFormatException e) {
                    error("No port number specified");
                }

            } else if(command.equals("-help")) {
                // TODO: Print help
                print("== Server Settings ==");
                print("To set a port, run with -port=<port_number>");
                print("you can serch site write 'namesurname.ch' example 'andreaprato.ch:8080'");
                print("If the server doesn't work, try running 'sudo nano /etc/hosts' and write '127.0.0.1 namesurname.ch' under localhost");
                print("");
                print(" == Supported HTTP methods == ");
                print("GET, PUT, DELETE, NTW22INFO");

            } else {
                error("Unknown command");
            }
        }

        // Create server
        Server server = new Server(port);
        server.setUp();
        if(server.isCorrectSetup()) {
            server.start();
        } else {
            error("Something went wrong when setting up server.");
        }

    }

}
