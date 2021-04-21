package jmDNS;


import jmDNS.ihm.ButtonColumn;
import org.json.JSONObject;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

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