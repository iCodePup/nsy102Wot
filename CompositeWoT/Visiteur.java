package CompositeWoT;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 */
public abstract class Visiteur<T> {
    public abstract T visite(Composant n);

    public abstract T visite(ListeDeComposants n);
}