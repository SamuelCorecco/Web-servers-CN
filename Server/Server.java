// Read FILE 
package Server;

import static Server.Print.*;
import java.net.*;
import java.io.File; 
import java.util.Scanner;
//Mappa
import java.util.HashMap;
//Thread
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
//Exeption
import java.io.IOException;
import java.io.FileNotFoundException;

public class Server {
    private final ThreadPoolExecutor threadPool;
    private HashMap<Integer,String[]> Domain;
    private ServerSocket serverSocket;
    private boolean isRun;
    private int port;

    /**
     * Constructor for Server
     * @param port the int to rapresent the server's port
     */
    public Server(int port) throws IOException{
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.port = port;
        this.isRun = false;
    }

    /**
     * This function setup the server, add the ServerSocket and read the vhost.conf
     * @return bollean, true if the server is setUp else false.
     * @throws IOException for the ServerSocket.
     */
    public boolean setUp() throws Exception {
        System.out.println("Port is " + port);
        System.out.println("Setting up server socket");
        serverSocket = new ServerSocket(port);
        readServerHost();
        System.out.println("Finished setup");
        return true;
    }
    /**
     * This function read the vhost.conf file and store the info in the server's map.
     */
    private void readServerHost(){
        try {
            File VhostsFile = new File("../vhost.conf");
            Scanner Reader = new Scanner(VhostsFile);
            int key = 0;
            while (Reader.hasNextLine()) {
                String line = Reader.nextLine();
                String[] line_split = line.split(" ");
                this.Domain.put(key, line_split);
                key++;
                }
            Reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erorr to scann file vhost :(");
            e.printStackTrace();
        }
    }


    /**
     * this function start the server on the server's port number.
     * @throws IOException
     */
    public void start() throws IOException{
        this.isRun = true;
        print("Server started");
        while(this.isRun) {
            print("Waiting for connection...");
            serverSocket.accept();
            try{
                Socket ClientSocket = this.serverSocket.accept();
                //this.threadPool.submit();
            }catch (IOException e) {
                System.out.println("Socket error");
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
     * This function return the info stored in the hashmap.
     * @param key is the key value to get the array
     * key = 0 return all domain.
     * key = 1 return all entry point file.
     * key = 3 return all member fullname.
     * key = 4 return all member email.
     * @return
     */
    public String[] getInfo(int key){
        if(0 > key || key > 4){
            return null;
        }
        String returnArray[] = new String[this.Domain.size()]; 
        for(int i = 0; i < this.Domain.size(); ++i){
            returnArray[i] = (this.Domain.get(i))[key];
        }
        return this.Domain.get(key);
    }
}