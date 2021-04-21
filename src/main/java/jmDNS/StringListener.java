package jmDNS;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * Pour traces...
 */
public class StringListener implements ServiceListener {
    @Override
    public void serviceAdded(ServiceEvent event) {
        System.out.println("[StringListener ]Objet connecté ajouté   : " + event.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        System.out.println("[StringListener ]Objet connecté supprimée   : " + event.getName());
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        ServiceInfo serviceInfo = event.getInfo();
        String name = serviceInfo.getName(); // Filtered service name
        int port = serviceInfo.getPort();
        System.out.println("[StringListener] Service resolved: " + event.getInfo()+ port + "name" + name);
    }
}
