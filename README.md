# Projet NSY102 - Composite WEB OF THING- jmDNS

#### _28/04/2021_


| Nom	 | Prénom          | Matricule|
| :--------------- |:---------------:| -----:|
|  VALENTIN   |   Fabien   |  0g5drtfg8y8 |



## Introduction

### En m'inspirant de votre idée proposée sur le forum j'ai développé une mini application permettant d'imaginer une maison domotisée avec des pièces contenant des services d'objets connectés WebOfThing et de les détecter via jmDNS.

### Exécuter le jeu d'essai du projet :

- Lancer la classe src/main/java/jmDNS/MainDiscoverServices

- Lancer la classe src/main/java/MainWoT

_Info DNS "webthing" dans le terminal :_

    dns-sd -B _webthing

## Questions 

### Q1) Création grâce au patron composite d'une maison avec des pièces (cuisine, salle de bain..) avec des objets connectés WebOfThing dans chaques pièces. (1 objet = 1 serveur API WebOfThing)


L'ensemble des classes constituantes du patron composite des objets WoT se trouvent dans le package src/main/java/CompositeWot


#### Patron Composite :

**Classe composite :**

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


**Classe Liste de composants :**

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

**Classe Composant :**

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


**Une feuille composant WoT :**

A chaque composant est associé sa pièce maitresse (_ex : ampoule est dans le salon, interrupteur est dans le salle de bain..._)

    public class Ampoule extends Composant {

        private WebThingServer server;
        private Thing thing;
        private String id;
    
        public Ampoule(String title, int port) throws IOException {
            super(title);
            id = UUID.randomUUID().toString();
            thing = new Thing(id,
                    title,
                    new JSONArray(Arrays.asList(
                            title)),
                    "Une ampoule connectée");
    
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


### Q2) Création d'un visiteur permettant de créer et démarrer/éteindre les services d'objets connectés

La patron composite va nous permettre de "visiter" chaque composant mais aussi plusieurs composants.
C'est pourquoi, grâce aux deux visiteurs VisiteurOn et VisiteurOff, nous allons pouvoir démarrer ou éteindre le serveur
d'un seul composant WoT mais aussi d'un sous ensemble de composant WoT appartenant à une pièce (_salon, cuisine, salle de bain...)_

**Visiteur de démarrage  :**

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

**Visiteur pour éteindre :**


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

### Q3) Le jeu d'essai MainWot

Ci-dessous un petit jeu d'essai pour simuler une maison avec des pièces constituées de composants WoT :

| Pièce	 | Objets          |  Port          |
| :--------------- |:---------------:| :---------------:|
|  Maison   |   Salon   | _  | 
|     |   Cuisine   | _ | 
|     |   Salle de bain   |  _| 
|   Salon  |   Ampoule   | 8060 | 
|     |   Capteur Temperature   | 8070  | 
|     |   Interrupteur   | 8080 | 
|   Cuisine  |   Ampoule   |8061 | 
|     |   Interrupteur   | 8081 | 
|   Salle de bain  |   Capteur Temperature   | 8071| 


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

### Q4) Découverte des services DNS WebOfThing via jmDNS. (éteints et démarrés par les visiteurs respectifs).

La classe suivante permet de découvrir les services mDNS grâce à la librairie jmDNS.

Plus spécifiquement ici, ce sont les services **_webthing** qui nous interesses.

Nous allons installer deux listenners dans l'attente de la découverte de services **_webthing** :

- StringListener  : Se contente d'afficher un retour console.

- IHMListener : Voir Q5


**La classe MainDiscoverServices:**


    public class MainDiscoverServices {

        private static final String serviceDNS = "_webthing._tcp.local.";
    
        /**
         * @param args the command line arguments
         */
        public static void main(String[] args) {
    
            Logger logger = Logger.getLogger(JmDNS.class.getName());
            ConsoleHandler handler = new ConsoleHandler();
            logger.addHandler(handler);
            logger.setLevel(Level.FINER);
            handler.setLevel(Level.FINER);
            try {
                JmDNS jmdns = null;
    
                InetAddress addr = InetAddress.getLocalHost();
                String hostname = InetAddress.getByName(addr.getHostName()).toString();
                try {
                    System.out.println("Listening on " + hostname);
                    jmdns = JmDNS.create(addr, hostname); // throws IOException
                } catch (IOException e) {
                    e.printStackTrace(); // handle this if you want
                }
                StringListener strListener = new StringListener();
                IHMListener ihmListener = new IHMListener(jmdns);
                //jmdns.addServiceListener(serviceDNS, strListener);
                jmdns.addServiceListener(serviceDNS, ihmListener);
    
                JmDNS finalJmdns = jmdns;
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            finalJmdns.unregisterAllServices();
                            finalJmdns.removeServiceListener(serviceDNS, strListener);
                            finalJmdns.removeServiceListener(serviceDNS, ihmListener);
                            finalJmdns.close();
                        } catch (Exception e) {
                        }
                    }
                });
                System.out.println("Apputer sur la touche q et Entrer, pour terminer le programme" + jmdns.getInetAddress());
    
                int b;
                while ((b = System.in.read()) != -1 && (char) b != 'q') {
                    /* en attente utilisateur */
                }
                jmdns.unregisterAllServices();
                jmdns.removeServiceListener(serviceDNS, strListener);
                jmdns.removeServiceListener(serviceDNS, ihmListener);
                jmdns.close();
    
                System.out.println("Terminé");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

### Q5) IHM pour visualiser le démarrage/arrêt des objets WoT via jmDNS

Ce listener permet d'ecouter les evenements jmDNS à travers une IHM.

Dans cette IHM j'ai ajouté un bouton permettant de faire une requête sur un service WoT.
Le but de cette requête est de faire une demande concernant la pièce dans laquelle se trouve l'objet connecté.
Cette information est disponible dans le JSON du service jmDNS.

Le bouton va donc déclencher l'appel au Thread **ThreadURLRequest**.
Ce thread est chargé d'executer la requete HTTP ainsi que de parser le JSON de retour de cette requête.


    public class IHMListener implements ServiceListener {

        //JMdns
        private JmDNS jmdns;
    
        //UI
        private JFrame frame;
        private final DefaultTableModel dm;
    
        //Json data from WoT object...
        private Vector<Vector<Object>> data;
    
        public IHMListener(JmDNS jmdns) {
            this.data = new Vector<>();
            this.jmdns = jmdns;
            frame = new JFrame("Ma maison connectée");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 300);
    
            Vector<Object> columnNames = new Vector<>();
            columnNames.add("Objet WoT");
            columnNames.add("API URL");
            columnNames.add("Pièce");
    
            dm = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(dm);
    
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(36, 37, 407, 79);
    
            Action request = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
    
                    JTable table = (JTable) e.getSource();
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    Vector<String> data = dtm.getDataVector().get(table.getSelectedRow());
                    Thread threadURLRequest = new Thread(new ThreadURLRequest(data.get(1), data.get(0)));
                    threadURLRequest.start();
                }
            };
    
            ButtonColumn buttonColumn = new ButtonColumn(table, request, 2);
            buttonColumn.setMnemonic(KeyEvent.VK_D);
            frame.add(table);
            frame.setVisible(true);
        }
    
        @Override
        public void serviceAdded(ServiceEvent event) {
    
            ServiceInfo info = jmdns.getServiceInfo(event.getType(), event.getName());
            if (info != null) {
                Vector<Object> aData = new Vector<Object>();
                String url =
                        String.format("http://%s:%s",
                                info.getHostAddresses()[0],
                                info.getPort()
                        );
    
                aData.add(event.getName());
                aData.add(url);
                aData.add("Pièce ? (Requête HTTP)");
                data.add(aData);
                dm.fireTableDataChanged();
            }
        }
    
        @Override
        public void serviceRemoved(ServiceEvent event) {
            try {
                Vector<Vector> foundRow = new Vector<>();
                for (Vector<Object> row : data) {
                    if (row.get(0).equals(event.getName())) {
                        foundRow.add(row);
                    }
                }
                data.removeAll(foundRow);
                dm.fireTableDataChanged();
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        @Override
        public void serviceResolved(ServiceEvent event) { }
    
        /**
         * Ce thread permet de faire une requete  HTTP pour obtenir le nom de la pièce
         */
        public class ThreadURLRequest implements Runnable {
    
            private String url;
            private String name;
    
            public ThreadURLRequest(String url, String name) {
                this.url = url;
                this.name = name;
            }
    
            @Override
            public void run() {
    
                JSONObject json = getJSONFromUrl(url + "/properties/piece");
                String piece = new String("Requete échouée");
                if (json != null) {
                    piece = json.getString("piece");
                    System.out.println("[IHMListener]Requete http de la pièce...(url:" + url + ", nom :" + name + ")" + piece);
                }
                Vector<Vector> foundRow = new Vector<>();
                for (Vector<Object> row : data) {
                    if (row.get(0).equals(name)) {
                        foundRow.add(row);
                    }
                }
                data.removeAll(foundRow);
                Vector<Object> aData = new Vector<Object>();
                aData.add(name);
                aData.add(url);
                aData.add(piece);
                data.add(aData);
                dm.fireTableDataChanged();
    
            }
    
            /**
             * URL to Json Object
             *
             * @param urlStr
             * @return
             */
            public JSONObject getJSONFromUrl(String urlStr) {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
    
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());/**/
                    }
                    JSONObject jsonObject = new JSONObject(
                            "" + response.toString() + ""
                    );
                    return jsonObject;
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                return null;
            }
        }
    }