package CompositeWoT;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 * Démarre les services WoT
 */
public class VisiteurOn extends Visiteur<Boolean> {

    @Override
    public Boolean visite(Composant n) {
        try {
            System.out.println("Starting WoT (pièce " + n.getPiece() + ") :" + n.getNom());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    n.getServer().stop();
                }
            });
            Thread.sleep(5000);
            n.getServer().start(false);
        } catch (Exception e) {
            e.printStackTrace();
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
