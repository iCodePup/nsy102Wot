package CompositeWoT;

import org.json.JSONObject;
import org.mozilla.iot.webthing.Property;
import org.mozilla.iot.webthing.Thing;
import org.mozilla.iot.webthing.Value;
import org.mozilla.iot.webthing.WebThingServer;

import java.io.IOException;
import java.util.UUID;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 */
public class CapteurTemperature extends Composant {

    private WebThingServer server;
    private Thing thing;
    private String id;

    public CapteurTemperature(String title, int port) throws IOException {
        super(title);

        id = UUID.randomUUID().toString();

        thing = new Thing(id,
                title,
                null,
                "Capteur connecté pour mesurer la température");

        server = new WebThingServer(new WebThingServer.SingleThing(thing), port);

    }

    protected void setPieceMere(String nom) {
        super.setPieceMere(nom);
        JSONObject onDescription = new JSONObject();
        onDescription.put("@type", "Piece");
        onDescription.put("nom", nom);
        onDescription.put("description", "La pièce dans laquelle l'objet se trouve");
        thing.addProperty(new Property(thing,
                "piece",
                new Value(nom),
                onDescription));
    }


    @Override
    public WebThingServer getServer() {
        return server;
    }

    @Override
    public int nombreDeIoT() {
        return 1;
    }

    @Override
    public <T> T accepter(Visiteur<T> visiteur) {
        return visiteur.visite(this);
    }

}
