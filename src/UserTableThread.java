import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Listener example class for UDP Messages.
 */
public class UserTableThread extends Thread {

    /*flag to be set if system has to be shut down */
    private boolean shutdown = false;
    public static int DEFAULT_PORT = 7777;

    private static final String NORMAL = "\033[m";
    private static final String BLUE = "\033[34m";
    private static final String GREEN = "\033[32m";

    private List<UserStatus> userTable = new ArrayList<>();

    /***
     * Constructor starting thread
     */
    public UserTableThread() {
        this.start();
    }

    public void updateUserStatus(String userName, InetAddress ip) {
        for (UserStatus userStatus : userTable) {
            if (userStatus.getUserName().equals(userName)
                    && userStatus.getIp().equals(ip)) {
                userStatus.setUpdate(new Date());
                return;
            }
        }
        // Add new user to list.
        userTable.add(new UserStatus(userName, ip, new Date()));
    }

    public void shutdown() {
        System.out.println("Shutting down UserTableThread");
        shutdown = true;
        try {
            join();
        } catch (InterruptedException ie) {
            // just ignore interrupted exception
        }
    }

    public void run() {
        while (!shutdown) {
            try {
                Sender.send(InetAddress.getByName("255.255.255.255"), DEFAULT_PORT, "!!DISCOVERY!");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\nUsers:");
            for (UserStatus userStatus : userTable) {
                boolean online = new Date().getTime() - userStatus.getUpdate().getTime() < 6000;
                if (online) {
                    System.out.println(userStatus + " : " + GREEN + "online" + NORMAL);
                } else {
                    System.out.println(userStatus + " : offline");
                }
            }
        }
        System.out.println("Listener has shut down");
    }
}
