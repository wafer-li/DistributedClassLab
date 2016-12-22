package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the RemoteService class
 * Please put some info here.
 *
 * @author Wafer Li
 * @since 16/12/22 20:51
 */
public class RemoteService extends UnicastRemoteObject implements AgendaInterface {

    private HashMap<String, String> userDB = new HashMap<>();
    private List<Meeting> meetings = new LinkedList<>();

    protected RemoteService() throws RemoteException {
    }

    private boolean isCertificateCorrect(String username, String password) {
        return password.equals(userDB.get(username));
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        if (!userDB.containsKey(username)) {
            return "Username doesn't exist!";
        }

        if (!password.equals(userDB.get(username))) {
            return "Password not correct!";
        }

        return null;
    }

    @Override
    public String register(String username, String password) throws RemoteException {
        if (userDB.containsKey(username)) {
            return "Username already exist!";
        }

        userDB.put(username, password);
        return null;
    }

    @Override
    public String add(String username, String password, String bookUser, String description, Date startTime, Date endTime) throws RemoteException {
        if (!isCertificateCorrect(username, password)) {
            return "Please login!";
        }

        if (!userDB.containsKey(bookUser)) {
            return "Book user doesn't exist!";
        }

        if (bookUser.equals(username)) {
            return "You are booking with yourself!";
        }

        Meeting meeting = new Meeting();

        meeting.setFirstUser(username);
        meeting.setSecondUser(bookUser);

        meeting.setDescription(description);
        meeting.setStartTime(startTime);
        meeting.setEndTime(endTime);

        meeting.setId(meetings.size());

        meetings.add(meeting);

        return String.valueOf(meetings.size());
    }

    @Override
    public List<Meeting> query(String username, String password, Date startTime, Date endTime) throws RemoteException {
        if (!isCertificateCorrect(username, password)) {
            return null;
        }

        List<Meeting> list = new ArrayList<>();

        for (Meeting meeting : meetings) {
            if (meeting.getFirstUser().equals(username) ||
                    meeting.getSecondUser().equals(username)) {

                if (meeting.getStartTime().equals(startTime) &&
                        meeting.getEndTime().equals(endTime)) {
                    list.add(meeting);
                }
            }
        }

        return list;
    }

    @Override
    public String remove(String username, String password, int meetingId) throws RemoteException {
        if (!isCertificateCorrect(username, password)) {
            return "Please Login!";
        }

        if (meetings.isEmpty()) {
            return "Meeting table is empty!";
        }

        meetings.remove(meetingId);

        return null;
    }

    @Override
    public String clear(String username, String password) throws RemoteException {
        if (!isCertificateCorrect(username, password)) {
            return "Please Login!";
        }

        for (int i = 0; i < meetings.size(); i ++) {
            Meeting meeting = meetings.get(i);

            if (meeting.getFirstUser().equals(username)) {
                meetings.remove(i);
            }
        }

        return null;
    }
}
