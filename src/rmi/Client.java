package rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This is the Client class
 * Please put some info here.
 *
 * @author Wafer Li
 * @since 16/12/22 21:59
 */
public class Client {
    private AgendaInterface agendaService;

    private String username = null;
    private String password = null;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private Client(AgendaInterface agendaService) {
        this.agendaService = agendaService;
    }

    private int getOption() throws IOException {
        System.out.println("Please select your option");

        System.out.println();
        System.out.println("1) register");
        System.out.println("2) login");
        System.out.println("3) add");
        System.out.println("4) query");
        System.out.println("5) remove");
        System.out.println("6) clear");
        System.out.println("7) exit");

        try {
            return Integer.parseInt(reader.readLine());
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    public static void main(String[] args) {
        try {

            AgendaInterface agendaServices = (AgendaInterface) Naming.lookup("rmi://localhost:23333/hehe");

            Client client = new Client(agendaServices);

            while (true) {

                int option = client.getOption();

                client.proceedOption(option);
            }
        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void proceedOption(int option) throws IOException {
        switch (option) {
            case 1:
                proceedRegister();
                break;
            case 2:
                proceedLogin();
                break;
            case 3:
                proceedAdd();
                break;
            case 4:
                proceedQuery();
                break;
            case 5:
                proceedRemove();
                break;
            case 6:
                proceedClear();
                break;
            case 7:
                System.exit(0);
                break;
            case -1:
                System.out.println("Please input number!");
                break;
        }
    }

    private void proceedClear() throws RemoteException {
        String errorMsg = agendaService.clear(username, password);

        if (errorMsg != null) {
            System.out.println(errorMsg);
        }
    }

    private void proceedRemove() throws IOException {
        int meetingId;
        while (true) {
            System.out.println("Please input the meeting id to be remove");

            try {
                meetingId = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException ignored) {
                continue;
            }

            String errorMsg = agendaService.remove(username, password, meetingId);

            if (errorMsg != null) {
                System.out.println(errorMsg);
            } else {
                System.out.println("Remove successful!");
                break;
            }
        }
    }

    private void proceedQuery() throws IOException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;

        List<Meeting> meetings;
        while (true) {
            while (true) {
                System.out.println("Please input the start date");

                try {
                    startDate = dateFormat.parse(reader.readLine());
                    break;
                } catch (ParseException ignored) {
                    // ignore
                }
            }

            while (true) {
                System.out.println("Please input the end date");

                try {
                    endDate = dateFormat.parse(reader.readLine());
                    break;
                } catch (ParseException e) {
                    // ignore
                }
            }

            meetings = agendaService.query(username, password, startDate, endDate);

            if (meetings == null) {
                System.out.println("Please Login!");
            } else {
                System.out.println("Query success!");
                break;
            }
        }
        System.out.println();
        for (Meeting meeting : meetings) {
            System.out.println(meeting.toString());
        }
    }


    private void proceedAdd() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;

        while (true) {
            System.out.println("Please input book username");
            String bookUsername = reader.readLine();

            System.out.println("Please input the description of the agenda");
            String description = reader.readLine();


            while (true) {
                System.out.println("Please input the start time");
                System.out.println("The example is yyyy-MM-dd");

                try {
                    startDate = dateFormat.parse(reader.readLine());

                    break;
                } catch (ParseException ignored) {
                    // ignore
                }
            }

            while (true) {
                System.out.println("Please input the end time");
                System.out.println("The example is yyyy-MM-dd");

                try {
                    endDate = dateFormat.parse(reader.readLine());
                    break;
                } catch (ParseException e) {
                    // ignore
                }
            }

            String errorMsg = agendaService.add(username, password, bookUsername, description, startDate, endDate);

            if (errorMsg != null) {
                System.out.println(errorMsg);
            } else {
                System.out.println("Add agenda success!");
                break;
            }
        }
    }

    private void proceedLogin() throws IOException {

        if (username != null || password != null) {
            System.out.println("Your are already login!");
            return;
        }

        while (true) {
            System.out.println("Please input your username");
            username = reader.readLine();

            System.out.println("Please input your password");
            password = reader.readLine();

            String errorMsg = agendaService.login(username, password);

            if (errorMsg != null) {
                System.out.println(errorMsg);
            } else {
                System.out.println("Login success!");
                break;
            }
        }
    }

    private void proceedRegister() throws IOException {
        while (true) {
            System.out.println("Please input your username");
            username = reader.readLine();

            System.out.println("Please input your password");
            password = reader.readLine();

            String errorMsg = agendaService.register(username, password);

            if (errorMsg != null) {
                System.out.println(errorMsg);
            } else {
                System.out.println("Register success!");
                System.out.println("You are automatically login!");
                break;
            }
        }
    }

}
