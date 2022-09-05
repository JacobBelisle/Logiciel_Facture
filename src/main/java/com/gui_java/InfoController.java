package com.gui_java;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class InfoController implements Initializable, DataPassing {

    @FXML
    private Button button1;

    private Facture _facture;

    private Client[] clients;

    @FXML
    private TextField textNbFacture;

    @FXML
    private TextField addDescription;

    @FXML
    private TextField addQuantite;

    @FXML
    private CheckBox addTaxes;

    @FXML
    private TextField addPrice;

    @FXML
    private CheckBox checkboxisActif;

    @FXML
    private SearchableComboBox listClients;

    @FXML
    private TextArea note;

    @FXML
    private TableView table;

    @FXML
    private Text resultText;

    @FXML
    private TextField grandTotal;

    @FXML
    private TextField solde;

    @FXML
    private Button paiementDone;

    @FXML
    private DatePicker dateFacture;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _facture = new Facture();
        clients = Client.searchListClients();

        String[] names = new String[clients.length + 1];

        for (int i = 0; i < clients.length; i++) {
            names[i + 1] = clients[i].getName();
        }
        names[0] = "";

        listClients.setItems(FXCollections.observableArrayList(names));

        this.setTableView();
    }

    @FXML
    protected void closeWindow() {
        Stage stage = (Stage) button1.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void SaveInfo() {
        if (textNbFacture.getText() == "") {

            YamlConfig config = new YamlConfig();
            config.readFile(".config", RouteCode.Root);

            config.setNoFactures(config.getNoFactures() + 1);

            textNbFacture.setText(String.format("%04d", config.getNoFactures()));
            textNbFacture.setDisable(true);

            YamlHandler.writeFile(config, ".config", RouteCode.Root);
        }

        _facture.setNumberFacture(textNbFacture.getText());

        _facture.setActif(checkboxisActif.isSelected());

        _facture.setNote(note.getText());
        _facture.setDate(dateFacture.getValue().toString());

        Article[] a = new Article[table.getItems().size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = (Article) table.getItems().get(i);
        }

        _facture.getProducts().clear();
        _facture.addProducts(a);

        for (int i = 0; i < this.clients.length; i++) {
            if (this.clients[i].getName() == listClients.getValue().toString())
                _facture.setClient(this.clients[i]);
        }

        YamlHandler.writeFile(_facture, "F" + _facture.getNumberFacture(), RouteCode.Factures);


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

    private void updateShit() {
        YamlConfig config = new YamlConfig();
        config.readFile(".config", RouteCode.Root);

        _facture.setNumberFacture(textNbFacture.getText() != "" ? textNbFacture.getText() :
                String.format("%04d", config.getNoFactures()));

        _facture.setActif(checkboxisActif.isSelected());

        _facture.setNote(note.getText());

        Article[] a = new Article[table.getItems().size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = (Article) table.getItems().get(i);
        }

        _facture.getProducts().clear();
        _facture.addProducts(a);

        for (int i = 0; i < this.clients.length; i++) {
            if (this.clients[i].getName() == listClients.getValue().toString())
                _facture.setClient(this.clients[i]);
        }
    }

    @Override
    public void receiveData(Object o) {
        _facture = (Facture) o;
        textNbFacture.setText(_facture.getNumberFacture());
        textNbFacture.setDisable(true);
        checkboxisActif.setSelected(_facture.getActif());
        note.setText(_facture.getNote());
        dateFacture.setValue(LocalDate.parse(_facture.getDate()));

        listClients.getSelectionModel().select(_facture.getClient().getName());

        this.table.setItems(FXCollections.observableArrayList(_facture.getProductsToArray()));

        this.updateSousTotal();
    }

    public void receiveArrayData(Object[] o) {
    }

    public void setTableView() {
        this.table.setEditable(true);

        TableColumn descriptionColumn = new TableColumn<>("Description");
        TableColumn priceColumn = new TableColumn<>("Prix");
        TableColumn quantiteColumn = new TableColumn<>("Quantité");
        TableColumn totalColumn = new TableColumn<>("Total");
        TableColumn taxesColumn = new TableColumn<>("Taxes");
        totalColumn.setEditable(false);

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Article, String>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Article, Float>("price"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<Article, Float>("quantite"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<Article, Float>("total"));
        taxesColumn.setCellValueFactory(new PropertyValueFactory<Article, Boolean>("taxes"));


        //to edit
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        quantiteColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        totalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        taxesColumn.setCellFactory((Callback<TableColumn<Article, Boolean>, TableCell<Article, Boolean>>) p -> {
            TableCellCheckBox c = new TableCellCheckBox();
            c.getCheckBox().setOnAction((event) -> {
                Article a = c.getTableRow().getItem();
                a.setTaxes(c.getCheckBox().isSelected());
                a.updateTotal();
                c.getTableRow().commitEdit(a);
                table.refresh();
                this.updateSousTotal();
            });
            return c;
        });


        descriptionColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Article, String>>) cellEditEvent -> {
            Article article = cellEditEvent.getRowValue();
            article.setDescription(cellEditEvent.getNewValue());
        });


        quantiteColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent>) cellEditEvent -> {
            Article a = (Article) cellEditEvent.getRowValue();
            a.setQuantite((Integer) cellEditEvent.getNewValue());
            a.updateTotal();
            this.updateSousTotal();
            table.refresh();
        });


        priceColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent>) cellEditEvent -> {
            Article a = (Article) cellEditEvent.getRowValue();
            a.setPrice((Float) cellEditEvent.getNewValue());
            a.updateTotal();
            table.refresh();
            this.updateSousTotal();
        });

        table.getColumns().addAll(descriptionColumn, quantiteColumn, taxesColumn, priceColumn, totalColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    @FXML
    public void addData() {
        Article a = new Article(addDescription.getText(),
                Integer.parseInt(addQuantite.getText()),
                Float.parseFloat(addPrice.getText()),
                addTaxes.isSelected());

        table.getItems().add(a);
        addDescription.clear();
        addQuantite.clear();
        addPrice.clear();
        this.updateSousTotal();

    }

    @FXML
    public void deleteRow() {
        Article a = (Article) table.getSelectionModel().getSelectedItem();
        table.getItems().remove(a);
        this.updateSousTotal();
    }

    @FXML
    public void confirmPaiement() {
        Alert alert = new Alert(Alert.AlertType.NONE, "Êtes-vous sûr de vouloir effectuer le paiement?",
                ButtonType.CANCEL, ButtonType.OK);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            _facture.addPaiement(new Paiement(date, Float.parseFloat(solde.getText())));
            updateSousTotal();
            SaveInfo();
        }
    }

    public void updateSousTotal() {
        Float grandtotal = 0f;

        Article[] a = new Article[table.getItems().size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = (Article) table.getItems().get(i);
            grandtotal += a[i].getTotal();
        }

        grandTotal.setText(String.format("%.2f", grandtotal));
        _facture.setGrandTotal(grandtotal);
        String soldeText = !this._facture.getPaiements().isEmpty() ?
                String.format("%.2f", Float.parseFloat(this.grandTotal.getText()) - this._facture.getTotalPaiements()) :
                String.format("%.2f", Float.parseFloat(this.grandTotal.getText()));

        this.solde.setText(soldeText);
        _facture.setSolde(Float.parseFloat(this.solde.getText()));

        this.paiementDone.setVisible(!this.solde.getText().equals("0.00"));
    }

    @FXML
    public void generatePDF() {
        this.updateShit();
        _facture.generatePDF();
    }
}
