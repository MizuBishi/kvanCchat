import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class implements a very simple selftest of a UDP communicator.
 *
 * It is badly designed! Please figure out what bad styles regarding
 * the system resources and security can be found here!
 */
public class CliClient {

    /* First some constants */
    private static int VERSION_MAYOR = 1;
    private static int VERSION_MINOR = 0;
    private static int VERSION_BUILD = 0;
    private static String VERSION  = "" + VERSION_MAYOR + "." + VERSION_MINOR + "." + VERSION_BUILD;

    private static final String PINK = "\033[35m";
    private static final String BLUE = "\033[34m";
    private static final String NORMAL = "\033[m";



    /**
     * Prints a usage message on the stdout stream.
     */
    private static void usage() {
        System.out.println( "CliClient V"+VERSION );
        System.out.println( "" );
    }

    /**
     * Selftest listener and sender.
     */
    public static void main(String[] args) {
        /* Print usage stream */
        usage();

        /* starting UserTableThread */
        System.out.println(PINK + "Starting "+ NORMAL +" UserTableThread");
        UserTableThread userTableThread = new UserTableThread();

        /* starting listener */
        System.out.println(PINK + "Starting "+ NORMAL +" Listener");
        Listener listen = new Listener(userTableThread);

//read UserInput
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        System.out.println("Say something. Empty line to exit.");

        try {
            //Text eingelesen bis die zeile leer ist
            while ((s = in.readLine()) != null && s.length() != 0) {
                System.out.println("YOU said : " + BLUE + s + NORMAL);
                try{
                    if(Sender.send(InetAddress.getByName("255.255.255.255"),
                            Listener.DEFAULT_PORT,"!!CHAT!!" + System.getProperty("user.name") + "!" + s)) {
                        System.out.println("CHAT sent successfully");
                    } else {
                        System.out.println("CHAT NOT SENT");
                    }
                } catch(UnknownHostException uhe) {
                    uhe.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("We're done.");

  /* shutting down listener */
        System.out.println("Shutting down Listener & userTableThread");
        listen.shutdown();
        userTableThread.shutdown();
        System.out.println("CliClient has shutdown");

    }
}


