package CompositeWoT;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 */
public class VisiteurString extends Visiteur<String> {

    @Override
    public String visite(Composant n) {
        return "[" + n.getNom() + " (" + n.getPiece() + ")]";
    }

    @Override
    public String visite(ListeDeComposants n) {
        String s = new String();
        s = "Objets connectés de :" + n.getNom() + "[";
        for (Composant c : n) {
            s += c.accepter(this);
        }
        s += "]";
        return s;
    }
}
