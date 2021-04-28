package CompositeWoT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 */
public abstract class Composite extends Composant implements Iterable<Composant> {
    private List<Composant> list;

    public Composite(String title) {
        super(title);
        this.list = new ArrayList<>();
    }

    public Composite ajouter(Composant composant) {
        composant.setPieceMere(this.getNom());
        list.add(composant);
        return this;
    }

    public int nombreDeIoT() {
        int nombre = 0;
        for (Composant c : this) {
            nombre += c.nombreDeIoT();
        }
        return nombre;
    }

    @Override
    public Iterator<Composant> iterator() {
        return list.iterator();
    }
}