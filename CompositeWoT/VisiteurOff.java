package CompositeWoT;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 * Stop les services WoT
 */
public class VisiteurOff extends Visiteur<Boolean> {

    @Override
    public Boolean visite(Composant n) {
        try {
            System.out.println("Stopping WoT :" + n.getNom());
            n.getServer().stop();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean visite(ListeDeComposants n) {
        boolean result = true;
        for (Composant c : n) {
            result &= c.accepter(this);
        }
        return result;
    }
}
