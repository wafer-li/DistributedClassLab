package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * This is the Server class
 * Please put some info here.
 *
 * @author Wafer Li
 * @since 16/12/22 21:41
 */
public class Server {
    public static void main(String[] args) {

        try {
            RemoteService remoteService = new RemoteService();

            LocateRegistry.createRegistry(23333);

            Naming.rebind("//localhost:23333/hehe", remoteService);
            System.out.println("RMI Server is running");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
