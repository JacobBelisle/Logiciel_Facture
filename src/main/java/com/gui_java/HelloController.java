package com.gui_java;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    Facture _facture;

    @FXML
    private TextField codeInput;

    @FXML
    private Text error;

    @FXML
    private ListView listFacture;

    @FXML
    private SearchableComboBox listClients;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Log.print(String.format("%04d", 44));
        Client[] list = Client.searchListClients();

        String[] names = new String[list.length + 1];

        for (int i = 0; i < list.length; i++) {
            names[i + 1] = list[i].getName();
        }
        names[0] = "";
        listClients.setItems(FXCollections.observableArrayList(names));
    }

    @FXML
    protected void onButtonClick() {
        SearchFacture();
    }

    private void SearchFacture() {
        _facture = new Facture();

        String name = "";
        name = listClients.getValue() != null ? listClients.getValue().toString() : "";


        try {
            if (_facture.readFile("F" + String.format("%04d", Integer.parseInt(codeInput.getText())), RouteCode.Factures)) {
                Facture[] list = {_facture};

                if (_facture.getClient().getName().equals(name) || name == "") {
                    listFacture.setItems(FXCollections.observableArrayList(list));
                    listFacture.requestFocus();
                } else {
                    listFacture.getItems().clear();
                }
                error.setVisible(false);
            } else {
                listFacture.getItems().clear();

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
            listFacture.getItems().clear();

            if (codeInput.getText().isEmpty()) {
                if (name != "") {
                    Facture[] allFacture = listAllFacture();

                    Vector<Facture> fine = new Vector<>();

                    for (int i = 0; i < allFacture.length; i++) {
                        if (allFacture[i].getClient().getName().equals(name) && allFacture[i].getActif())
                            fine.add(allFacture[i]);
                    }

                    listFacture.setItems(FXCollections.observableArrayList(fine.toArray()));
                    listFacture.requestFocus();
                } else {
                    listFacture.setItems(FXCollections.observableArrayList(listAllFacture()));
                    listFacture.requestFocus();
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

        listFacture.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                openFacture();
            }
        });

        listFacture.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                openFacture();
            }
        });
    }

    @FXML
    protected void addFacture() {
        Window w = new Window("Nouvelle facture", HelloApplication.class.getResource("info-view.fxml"),
                960, 540, Modality.APPLICATION_MODAL);

        w.showWindow();
    }

    private void openFacture() {
        Facture currentItem = (Facture) listFacture.getSelectionModel().getSelectedItem();

        Window w = new Window("Facture # " + currentItem.getNumberFacture(), HelloApplication.class.getResource("info-view.fxml"),
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
            if (listofFacture[i].getActif())
                list.add(listofFacture[i]);
        }

        return list.toArray(new Facture[list.size()]);
    }
}