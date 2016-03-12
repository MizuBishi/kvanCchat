import java.io.IOException;
import java.net.*;

/**
 * Listener example class for UDP Messages.
 */
public class Listener extends Thread {

    /* The default port of the listener */
    public static int DEFAULT_PORT = 7777;

    /* The default buffer size of the listener */
    private static int BUFFER_SIZE  = 1024;

    /* the current port beeing used */
    private int port;
    private InetAddress addr;

    /* The handler for incomming commands */
    private CommandHandler commandHandler=null;

    /*flag to be set if system has to be shut down */
    private boolean shutdown = false;

    private static final String NORMAL = "\033[m";
    private static final String BLUE = "\033[34m";
    private static final String GREEN = "\033[32m";

    /***
     * Constructor starting a listener on the default port.
     */
    public Listener() {
        init(null,DEFAULT_PORT);
    }

    /***
     * Constructor starting a listener on a user defined socket.
     */
    public Listener(InetAddress addr,int port)  {
        init(addr,port);
    }

    /***
     * Contructor helper for setting up a listener on a specific port.
     *
     * @param port Port for the listener to start
     */
    private void init(InetAddress addr,int port) {
        this.port=port;
        this.addr=addr;
        this.start();
    }

    public void shutdown() {
        System.out.println("Shutting down listener");
        shutdown=true;
        try {
            Sender.send(InetAddress.getByName("127.0.0.1"), port, "!!NOP!!");
            join();
        } catch(UnknownHostException uhe) {
            // failed to get 127.0.0.1 ???
        } catch(InterruptedException ie) {
            // just ignore interrupted exception
        }
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port,addr);
            while (!shutdown) {
                try {
                    byte[] buf = new byte[BUFFER_SIZE];
                    DatagramPacket dp = new DatagramPacket(buf, BUFFER_SIZE);
                    socket.receive(dp);

                    String str = new String(dp.getData(), 0, dp.getLength());

                    // Dump message to screen
                    System.out.println("got message:" + str);

                    // Kommandoverarbeitung
                    if(str.startsWith("!!PING!!")) {
                        //TODO check must not answer broadcast
                        Sender.send(dp.getAddress(), port, "!!PONG!!" + str.substring(8));
                    } else if(str.startsWith("!!NOP!!")) {
                        // ignore NOPs
                    } else if(str.startsWith("!!DISCOVERY!")) {
                        Sender.send(dp.getAddress(), port, "!!DISCOVERYREPLY!!" + System.getProperty("user.name"));
                    } else if(str.startsWith("!!CHAT!!")) {
                        String nameText = str.substring(8);
                        String name = nameText.substring(0, nameText.indexOf("!"));
                        String text = nameText.substring(nameText.indexOf("!") +1);
                        //TODO do not dislplay own message
                        System.out.println(
                                BLUE + "YAY got message! " + NORMAL +
                                        " name: " + name +
                                        " address: " + dp.getAddress() +
                                        " message: " + text);
                    } else {
                        if(commandHandler!=null) {
                            commandHandler.processCommand(str);
                        } else {
                            System.out.println("ignored message:" + str);
                        }

                    }

                } catch (IOException ie) {
                    ie.printStackTrace();
                    System.exit(100);
                }
            }
        } catch(SocketException se) {
            System.out.println( "unable to bind socket on port "+port);
            se.printStackTrace();
            System.exit(100);
        }
        System.out.println("Listener has shut down");

    }

}
