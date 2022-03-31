//Read FILE 
import java.io.File; 
import java.util.Scanner;
//Mappa
import java.util.HashMap;
//Java Net
import java.net.ServerSocket;
import java.net.Socket;
//Thread
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
//Exeption
import java.io.IOException;
import java.io.FileNotFoundException;

public class Server {
    private final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() + 1;
    private final ThreadPoolExecutor threadPool;
    private HashMap<Integer,String[]> Domain;
    private ServerSocket serverSocket;
    private boolean isRun;
    private final int port;

    public Server(final int port) throws IOException {
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);
        this.isRun = false;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    private void readServerInfo(){
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
        }catch (FileNotFoundException e) {
            System.out.println("Erorr to scann file vhost :(");
            e.printStackTrace();
        }
    }


    public void start() {
        this.isRun = true;
        readServerInfo();
        while (this.isRun) {
            try {
                final Socket ClientSocket = this.serverSocket.accept();
                //inviare messaggio con la threadpull
            } catch (IOException e) {
            }
        }
    }

    public void stop() {
        this.isRun = false;
        threadPool.shutdown();
    }

    public String getEntrypoint(){
        //TODO
        return "ciao";
    }
}