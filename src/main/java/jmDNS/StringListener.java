package jmDNS;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
 * Pour traces...
 */
public class StringListener implements ServiceListener {
    @Override
    public void serviceAdded(ServiceEvent event) {
        //System.out.println("[StringListener ]Objet connecté ajouté   : " + event.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        //System.out.println("[StringListener ]Objet connecté supprimée   : " + event.getName());
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        //System.out.println("[StringListener] Service resolved: " + event.getInfo());
    }
}
