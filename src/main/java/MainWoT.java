import CompositeWoT.*;

import java.io.IOException;

/**
 * NSY102- Fabien VALENTIN
 * 2021/03
 * Exemple de maison connectée...
 */
public class MainWoT {
    public static void main(String[] args) {

        //Les pièces de la maison :
        ListeDeComposants maison = new ListeDeComposants("Maison");
        ListeDeComposants salon = new ListeDeComposants("Salon");
        ListeDeComposants cuisine = new ListeDeComposants("Cuisine");
        ListeDeComposants sdb = new ListeDeComposants("Salle de bain");

        try {
            //3 objets dans le salon...
            Composant iotSalon1 = new Ampoule("Ampoule", 8060);
            Composant iotSalon2 = new CapteurTemperature("Capteur Temperature", 8070);
            Composant iotSalon3 = new Interrupteur("Interrupteur", 8080);

            salon.ajouter(iotSalon1).ajouter(iotSalon2).ajouter(iotSalon3);
            maison.ajouter(salon);

            //visiteurs string+start
            Visiteur<String> visiteurStr = new VisiteurString();
            System.out.println(visiteurStr.visite(maison));
            Visiteur<Boolean> visiteurOn = new VisiteurOn();
            boolean startStatus = visiteurOn.visite(maison); //on démarre tous les objets de la maison...
            System.out.println("Starting WoT component of house : " + startStatus);

            Thread.sleep(15000);

            Visiteur<Boolean> visiteurOff = new VisiteurOff();
            boolean stopStatus = visiteurOff.visite(maison); //on éteint tous les objets de la maison...
            System.out.println("Stopping all WoT component of house : " + stopStatus);

            Thread.sleep(15000);
            //2 objets dans la cuisine...
            Composant iotCuisine1 = new Ampoule("Ampoule", 8061);
            Composant iotCuisine2 = new CapteurTemperature("Interrupteur", 8081);

            cuisine.ajouter(iotCuisine1).ajouter(iotCuisine2);
            maison.ajouter(cuisine);
            //visiteurs string+start
            System.out.println(visiteurStr.visite(maison));
            startStatus = visiteurOn.visite(maison); //on démarre tous les objets de la maison...(salon+cuisine donc..)
            System.out.println("Starting WoT component of house : " + startStatus);

            Thread.sleep(15000);
            //1 objet dans la salle de bain...
            Composant iotSdb2 = new CapteurTemperature("Capteur Temperature", 8071);
            sdb.ajouter(iotSdb2);
            //visiteurs string+start
            System.out.println(visiteurStr.visite(sdb));
            startStatus = visiteurOn.visite(sdb); //on démarre tous les objets de la salle de bain uniquement...
            System.out.println("Starting WoT component of bathroom only : " + startStatus);

        } catch (IOException e) {
            System.out.println("Erreur lors du deploiement d'un WoT");
        } catch (InterruptedException e) {
            System.out.println("Thead.sleep InterruptedException");
        }
    }
}
