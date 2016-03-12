import java.net.InetAddress;
import java.util.Date;

/**
 * Helper class to save one row of table with user data.
 */
public class UserStatus {
    private String userName;
    private InetAddress ip;
    private Date update;

    public UserStatus(String userName, InetAddress ip, Date update) {
        this.userName = userName;
        this.ip = ip;
        this.update = update;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "userName=" + userName
                + " ip=" + ip
                + " update=" + update;
    }
}
