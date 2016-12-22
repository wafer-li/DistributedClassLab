package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * This is the AgendaInterface class
 * Please put some info here.
 *
 * @author Wafer Li
 * @since 16/12/22 20:33
 */
public interface AgendaInterface extends Remote {

    /**
     * Login into agenda system
     *
     * @param username The username
     * @param password The password
     * @return The <b>ERROR</b> message. Null if correct!
     * @throws RemoteException The rmi exception
     */
    String login(String username, String password) throws RemoteException;

    String register(String username, String password) throws RemoteException;

    String add(String userName, String password,
               String bookUser, String description, Date startTime, Date endTime) throws RemoteException;

    List<Meeting> query(String username, String password,
                        Date startTime, Date endTime) throws RemoteException;

    String remove(String username, String password, int meetingId) throws RemoteException;

    String clear(String username, String password) throws RemoteException;
}
