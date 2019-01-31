

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Consomateur extends java.rmi.server.UnicastRemoteObject implements Runnable, InterfaceConsomateur {

    static int N;
    int id;
    Object m;
    private int Nc;
    private Object[] Tc;
    private int inc;
    private int outc;
    private int nbmessC;
    private int nbcellC;
    static int val;
    public static Object obj=new Object();
    public Consomateur(int id) throws RemoteException {
        inc = 0;
        this.nbcellC = 0;
        this.id = id;
        outc = 0;
        nbmessC = 0;
        this.Nc = 5;
        Tc = new Object[Nc];
        val=this.Nc;
    }

    //Reception d'un message
    @Override
    public  void receptionMess(Object m) throws RemoteException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Consommateur: reception du message " + m);
        synchronized(obj) {
        Tc[inc] = m;
        inc = (inc + 1) % Nc;
        nbmessC++;}
    }

    //Consommation
    public  void consommer() {

    	 while (nbmessC <= 0) {
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
         synchronized (obj) {
         Object m = Tc[outc];
         System.out.println("Consommateur: consommation de " + m);
         outc = (outc + 1) % Nc;
         nbmessC--;
         nbcellC++;
         }
    }

    //Reception Jeton 
    @Override
    public void receptionJeton(int val) throws RemoteException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("Consommateur reception de jeton: " + val);
        val += nbcellC;
        nbcellC = 0;
        String myIP;

        try {
            myIP = InetAddress.getLocalHost().getHostAddress();
            String url = "rmi://" + myIP + ":2001/Producteur" + Producteur.listeP.get(Producteur.FirstP()).id;
            InterfaceProducteur m = (InterfaceProducteur) Naming.lookup(url);
            m.ReceptionJeton(val);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
       // System.out.println("je suis le consommateur ");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String myIP;
                    myIP = InetAddress.getLocalHost().getHostAddress();
                    String url = "rmi://" + myIP + ":2001/Producteur" + Producteur.listeP.get(0).id;
                    InterfaceProducteur m = (InterfaceProducteur) Naming.lookup(url);
                    m.ReceptionJeton(val);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
        t.start();

        // TODO Auto-generated method stub
        while (true) {
            this.consommer();
        }

    }

}
