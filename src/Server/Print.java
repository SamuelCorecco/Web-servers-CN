package src.Server;

public class Print {
    
    /**
     * Print string to terminal
     * @param msg string
     */
    public static void print(String msg) {
        System.out.println(msg);
    }

    /**
     * Print number to terminal
     * @param num number
     */
    public static void print(int num) {
        System.out.println(num);
    }

    /**
     * Print error to terminal
     * @param msg error message
     */
    public static void error(String msg) {
        System.err.println(msg);
    }

}
