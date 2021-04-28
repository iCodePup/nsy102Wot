package jmDNS;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * NSY102 - Fabien VALENTIN
 */
public class MainDiscoverServices {

    private static final String serviceDNS = "_webthing._tcp.local.";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Logger logger = Logger.getLogger(JmDNS.class.getName());
        ConsoleHandler handler = new ConsoleHandler();
        logger.addHandler(handler);
        logger.setLevel(Level.FINER);
        handler.setLevel(Level.FINER);
        try {
            JmDNS jmdns = null;

            InetAddress addr = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(addr.getHostName()).toString();
            try {
                System.out.println("Listening on " + hostname);
                jmdns = JmDNS.create(addr, hostname); // throws IOException
            } catch (IOException e) {
                e.printStackTrace(); // handle this if you want
            }
            StringListener strListener = new StringListener();
            IHMListener ihmListener = new IHMListener(jmdns);
            jmdns.addServiceListener(serviceDNS, strListener);
            jmdns.addServiceListener(serviceDNS, ihmListener);

            JmDNS finalJmdns = jmdns;
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        finalJmdns.unregisterAllServices();
                        finalJmdns.removeServiceListener(serviceDNS, strListener);
                        finalJmdns.removeServiceListener(serviceDNS, ihmListener);
                        finalJmdns.close();
                    } catch (Exception e) {
                    }
                }
            });
            System.out.println("Apputer sur la touche q et Entrer, pour terminer le programme" + jmdns.getInetAddress());

            int b;
            while ((b = System.in.read()) != -1 && (char) b != 'q') {
                /* en attente utilisateur */
            }
            jmdns.unregisterAllServices();
            jmdns.removeServiceListener(serviceDNS, strListener);
            jmdns.removeServiceListener(serviceDNS, ihmListener);
            jmdns.close();

            System.out.println("Terminé");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
