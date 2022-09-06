package com.gui_java;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class HelloController implements Initializable {

    private Facture _facture;

    private Client _client;

    private Client[] ClientList;

    @FXML
    private TextField codeInput;

    @FXML
    private Text error;

    @FXML
    private ListView listSearch;

    @FXML
    private SearchableComboBox listClients;

    @FXML
    private ComboBox whichSearch;

    @FXML
    private CheckBox btnNonActif;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getInfos();
    }

    private void getInfos() {
        ClientList = Client.searchListClients();

        String[] names = new String[ClientList.length + 1];

        for (int i = 0; i < ClientList.length; i++) {
            names[i + 1] = ClientList[i].getName();
        }
        names[0] = "";
        listClients.setItems(FXCollections.observableArrayList(names));

        String[] a = {"Facture", "Client"};
        whichSearch.setItems(FXCollections.observableArrayList(a));
        whichSearch.getSelectionModel().selectFirst();
    }

    @FXML
    protected void onButtonClick() {
        switch (whichSearch.getValue().toString()) {
            case "Facture":
                SearchFacture();
                break;

            case "Client":
                SearchClient();
                break;

            default:
                break;
        }

        listSearch.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                switch (whichSearch.getValue().toString()) {
                    case "Facture":
                        openFacture();
                        break;

                    case "Client":
                        openClient();
                        break;
                }

            }
        });

        listSearch.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (whichSearch.getValue().toString()) {
                    case "Facture":
                        openFacture();
                        break;

                    case "Client":
                        openClient();
                        break;
                }
            }
        });

    }

    private void SearchFacture() {
        _facture = new Facture();

        String name = "";
        name = listClients.getValue() != null ? listClients.getValue().toString() : "";


        try {
            if (_facture.readFile("F" + String.format("%04d", Integer.parseInt(codeInput.getText())), RouteCode.Factures)) {
                Facture[] list = {_facture};

                if (_facture.getClient().getName().equals(name) || name == "") {
                    listSearch.setItems(FXCollections.observableArrayList(list));
                    listSearch.requestFocus();
                } else {
                    listSearch.getItems().clear();
                }
                error.setVisible(false);
            } else {
                listSearch.getItems().clear();

                Log.print("Cette facture n'existe pas.");
                error.setText("Cette facture n'existe pas");
                error.setVisible(true);

                Timer timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        error.setVisible(false);
                    }
                }, 2000);

            }
        } catch (NumberFormatException e) {
            listSearch.getItems().clear();

            if (codeInput.getText().isEmpty()) {
                if (name != "") {
                    Facture[] allFacture = listAllFacture();

                    Vector<Facture> fine = new Vector<>();

                    for (int i = 0; i < allFacture.length; i++) {
                        if (allFacture[i].getClient().getName().equals(name))
                            fine.add(allFacture[i]);
                    }

                    listSearch.setItems(FXCollections.observableArrayList(fine.toArray()));
                    listSearch.requestFocus();
                } else {
                    listSearch.setItems(FXCollections.observableArrayList(listAllFacture()));
                    listSearch.requestFocus();
                }
            } else {
                error.setText("Le texte doit être un chiffre");
                error.setVisible(true);
                Timer timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        error.setVisible(false);
                    }
                }, 2000);
            }
        }
    }

    private void SearchClient() {
        _client = new Client();

        String name = "";
        name = listClients.getValue() != null ? listClients.getValue().toString() : "";

        Client c = new Client();

        if (!name.equals("")) {
            for (Client i : ClientList) {
                if (i.getName().equals(listClients.getValue().toString()))
                    c = i;
            }
            _client.readFile("C" + c.getNumberClient(), RouteCode.Clients);

            listSearch.setItems(FXCollections.observableArrayList(c));
        } else {
            listSearch.getItems().clear();
            listSearch.setItems(FXCollections.observableArrayList(ClientList));
        }
        listSearch.requestFocus();
    }

    @FXML
    protected void addFacture() {
        Window w = new Window("Nouvelle facture", HelloApplication.class.getResource("info-view.fxml"),
                960, 540, Modality.APPLICATION_MODAL);

        w.showWindow();
    }

    @FXML
    private void addClient() {
        Window w = new Window("Nouveau client", ClientController.class.getResource("client-view.fxml"),
                960, 540, Modality.APPLICATION_MODAL);

        w.showWindow();
    }

    private void openFacture() {
        Facture currentItem = (Facture) listSearch.getSelectionModel().getSelectedItem();

        Window w = new Window("Facture # " + currentItem.getNumberFacture(), HelloApplication.class.getResource("info-view.fxml"),
                960, 540, Modality.APPLICATION_MODAL);

        w.sendData(currentItem);

        w.showWindow();
    }

    private void openClient() {
        Client currentItem = (Client) listSearch.getSelectionModel().getSelectedItem();

        Window w = new Window("Client # " + currentItem.getNumberClient(), ClientController.class.getResource("client-view.fxml"),
                960, 540, Modality.APPLICATION_MODAL);

        w.sendData(currentItem);

        w.showWindow();
    }

    public Facture[] listAllFacture() {
        // TODO rajouter un bouton pour lister ceux non actives aussi
        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.readFile(".config", RouteCode.Root);

        Facture[] listofFacture = null;

        int numberOfFacture = yamlConfig.getNoFactures();
        if (numberOfFacture != 0) {
            listofFacture = new Facture[numberOfFacture];

            for (int i = 0; i < listofFacture.length; i++) {
                listofFacture[i] = new Facture("F" + String.format("%04d", i + 1), RouteCode.Factures);
            }
        }

        Vector<Facture> list = new Vector<>();
        for (int i = 0; i < listofFacture.length; i++) {
            if (listofFacture[i].getActif() || (listofFacture[i].getActif() == !btnNonActif.isSelected()))
                list.add(listofFacture[i]);
        }

        return list.toArray(new Facture[list.size()]);
    }

    public void refresh() {
        getInfos();
    }
}