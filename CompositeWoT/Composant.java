package CompositeWoT;

import org.mozilla.iot.webthing.WebThingServer;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 */
public abstract class Composant {

    private String title;
    private String piece;

    public Composant(String title) {
        this.title = title;
    }

    public String getNom() {
        return this.title;
    }

    public abstract WebThingServer getServer();

    public abstract int nombreDeIoT();

    public abstract <T> T accepter(Visiteur<T> visiteur);

    protected void setPieceMere(String nom) {
        this.piece = nom;
    }

    public String getPiece() {
        return piece;
    }
}
