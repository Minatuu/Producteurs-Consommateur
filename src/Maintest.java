

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Maintest {

    public static void main(String[] args) {
        System.out.println("\n-------------- BIENVENU --------------");
        System.out.println("\nVeuillez  saisir le nombre de machine ");
        Scanner sc = new Scanner(System.in);
        String nb = sc.nextLine();
        int numsite = Integer.parseInt(nb);
        System.out.println("\nCreation de " + nb + " machines");
        System.out.println("\n-------------- ELECTION --------------");
        Machinee t = null;
        try {
            LocateRegistry.createRegistry(2001);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        /*crearion de nb machine 
         *
         */
        Thread[] ma = new Thread[numsite];
        for (int i = 0; i < numsite; i++) {

            try {
                t = new Machinee();
                ma[i] = new Thread(t);
                String myIP = InetAddress.getLocalHost().getHostAddress();
                String url = "rmi://" + myIP + ":2001/machine" + t.id;
                Naming.rebind(url, t);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        /*
         * lancement des nb Thread 
         * 
         */
        for (int i = 0; i < numsite; i++) {
            ma[i].start();
        }
        for (int i = 0; i < numsite; i++) {
            try {
                ma[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();

            }
        }
        /*
         * creation du consomteur et de nb-1 producteur
         */
        Consomateur c = null;
        try {
            c = new Consomateur(Machinee.leader);
            //System.out.println("Consommateur");
            String myIP = InetAddress.getLocalHost().getHostAddress();
            String url = "rmi://" + myIP + ":2001/Consomateur";
            Naming.rebind(url, c);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("\n-------------- Production & Consommation --------------");
        Thread l = new Thread(c);

        //System.out.println("start cons");
        Thread[] tab = new Thread[10];
        int i = 0;
        for (Machinee m : Machinee.ListeM) {
            if (Machinee.leader != m.id) {
                Producteur p = null;
                try {
                    p = new Producteur(m.id);
                    System.out.println("je suis le Producteur " + m.id);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String myIP;
                try {
                    myIP = InetAddress.getLocalHost().getHostAddress();
                    String url = "rmi://" + myIP + ":2001/Producteur" + p.id;
                    Naming.rebind(url, p);
                    Thread d = new Thread(p);
                    tab[i] = d;
                    i++;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        l.start();
        for (int k = 0; k < i; k++) {
            tab[k].start();
        }
        for (int k = 0; k < i; k++) {
            try {
                tab[k].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            l.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
