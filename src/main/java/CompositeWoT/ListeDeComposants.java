package CompositeWoT;

import org.mozilla.iot.webthing.WebThingServer;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 */
public class ListeDeComposants extends Composite {

    public ListeDeComposants(String nom) {
        super(nom);
    }

    @Override
    public WebThingServer getServer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T accepter(Visiteur<T> visiteur) {
        return visiteur.visite(this);
    }
}
