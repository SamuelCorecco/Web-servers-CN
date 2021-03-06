// Read FILE 
package src.Server;

import static src.Server.Print.*;
import java.net.*;
import java.io.File; 
import java.util.Scanner;
//Thread
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
//Exeption
import java.io.*;

public class Server {
    private final ThreadPoolExecutor threadPool;
    private DomainList domains;
    private ServerSocket serverSocket;
    private boolean isRun;
    private int port;
    private boolean isSetup;

    /**
     * Constructor for Server
     * @param port the int to rapresent the server's port
     */
    public Server(int port) throws IOException{
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.port = port;
        this.isRun = false;
        this.domains = new DomainList();
    }

    /**
     * This function setup the server, add the ServerSocket and read the vhosts.conf
     * @throws IOException for the ServerSocket.
     */
    public void setUp() throws Exception {
        System.out.println("Port is " + port);
        System.out.println("Setting up server socket");
        serverSocket = new ServerSocket(port);
        readServerHost();
        System.out.println("Finished setup");
        isSetup = true;
    }

    /**
     * Check if the setup is done.
     * @return the state of setup.
     */
    public boolean isCorrectSetup(){
        return this.isSetup;
    }

    /**
     * This function read the vhosts.conf file and store the info in the server's map.
     */
    private void readServerHost(){
        try {
            File VhostsFile = new File("vhosts.conf");
            Scanner Reader = new Scanner(VhostsFile);
            while (Reader.hasNextLine()) {
                String line = Reader.nextLine();
                String[] line_split = line.split(",");
                domains.addDomain(line_split[0], line_split[1], line_split[2], line_split[3]);
            }
            Reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // /**
    //  * Print request to terminal
    //  * @param s_input input stream of request
    //  */
    // public void printRequestToTerminal(InputStream s_input) throws IOException {
    //     print("");
    //     String line;
    //     do {
    //         line = readLineOfBytes(s_input);
    //     } while (!line.equals(""));
    // }

    /**
     * this function start the server on the server's port number.
     * @throws IOException
     */
    public void start() throws Exception {
        this.isRun = true;
        print("Server started");
        while(this.isRun) {
            print("Waiting for connection...");
            try{
                
                Socket ClientSocket = this.serverSocket.accept();
                print("Connection from: " + ClientSocket.getInetAddress() + " established.");
                this.threadPool.submit(new ThreadSocket(ClientSocket, this));

            } catch (IOException e) {
                error("Socket error");
                e.printStackTrace();
            }
            
        }
    }

    /**
     * this function stop the server
     */
    public void stop() {
        this.isRun = false;
        threadPool.shutdown();
    }

    /**
     * Get entry point file for the given host
     * @param hostname host of which we want to get the entry file
     * @return name of the entry point file, inside the corresponding <member_name> folder
     */
    public String getEntryPoint(String hostname) {
        return domains.getEntryPoint(hostname);
    }

    /**
     * Get default hostname in case the Host header is not specified in a request.
     */
    public String getDefaultHost() {
        return domains.getDefaultHostname();
    }


    /**
     * Get default hostname in case the Host header is not specified in a request.
     */
    public Boolean validDomain(String checkDomain) {
        return (0 <= domains.checkHostExists(checkDomain));
    }


    /**
     * Get the DomainList of the server.
     */
    public DomainList getDomains() {
        return domains;
    }
}

