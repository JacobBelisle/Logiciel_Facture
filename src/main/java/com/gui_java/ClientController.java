package com.gui_java;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ClientController implements Initializable, DataPassing {

    private Client _client;

    @FXML
    private TextField textNbClient;

    @FXML
    private TextField textName;

    @FXML
    private TextField textAdresse;

    @FXML
    private TextField textTelephone;

    @FXML
    private TextField textCourriel;

    @FXML
    private Button buttonSave;

    @FXML
    private Button goback;

    @FXML
    private Text resultText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _client = new Client();
    }

    @Override
    public void receiveData(Object o) {
        _client = (Client) o;

        textNbClient.setText(_client.getNumberClient());
        textNbClient.setDisable(true);

        textName.setText(_client.getName());

        textAdresse.setText(_client.getAdresse());

        textTelephone.setText(_client.getTelephone());

        textCourriel.setText(_client.getCourriel());
    }

    @Override
    public void receiveArrayData(Object[] o) {

    }

    public void SaveInfo() {
        if (!textName.getText().equals("")) {
            if (textNbClient.getText() == "") {
                YamlConfig config = new YamlConfig();
                config.readFile(".config", RouteCode.Root);

                config.setNoClients(config.getNoClients() + 1);

                textNbClient.setText(String.format("%04d", config.getNoClients()));
                textNbClient.setDisable(true);

                YamlHandler.writeFile(config, ".config", RouteCode.Root);
            }

            _client.setNumberClient(textNbClient.getText());
            _client.setName(textName.getText());
            _client.setAdresse(textAdresse.getText());
            _client.setTelephone(textTelephone.getText());
            _client.setCourriel(textCourriel.getText());

            YamlHandler.writeFile(_client, "C" + _client.getNumberClient(), RouteCode.Clients);


            resultText.setText("Le fichier a bien été sauvegardé");
            resultText.setVisible(true);

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    resultText.setVisible(false);
                }
            }, 2000);
        }
    }

    public void closeWindow() {
        Stage stage = (Stage) goback.getScene().getWindow();
        stage.close();
    }

}
