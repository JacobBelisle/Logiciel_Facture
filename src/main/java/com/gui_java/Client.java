package com.gui_java;

public class Client implements YmlWritable {
    private String numberClient;
    private String name;
    private String adresse;
    private String telephone;
    private String courriel;

    public Client() {
        this.numberClient = "-1";
        this.name = "NaN";
        this.adresse = "";
        this.telephone = "";
        this.courriel = "";
    }

    public Client(String numberClient, String name) {
        this.numberClient = numberClient;
        this.name = name;
        this.adresse = "";
        this.telephone = "";
        this.courriel = "";

    }

    public boolean readFile(String nameFile, String route) {
        String[] data = YamlHandler.readFile(nameFile, route);

        if (data.length != 0) {           //si le fichier n'existe pas, il renvoie un array vide
            for (int i = 0; i < data.length; i++) {
                String[] keyvalue = data[i].split(": ");
                switch (keyvalue[0]) {
                    case "number_client":
                        this.numberClient = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "name":
                        this.name = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "adresse":
                        this.adresse = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "telephone":
                        this.telephone = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "courriel":
                        this.courriel = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    default:
                        break;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toFile() {
        return "number_client: " + numberClient + "\n" +
                "name: " + name + "\n" +
                "adresse: " + adresse + '\n' +
                "telephone: " + telephone + '\n' +
                "courriel: " + courriel + '\n';
    }

    public String getNumberClient() {
        return numberClient;
    }

    public String getName() {
        return name;
    }

    public void setNumberClient(String numberClient) {
        this.numberClient = numberClient;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Client[] searchListClients() {
        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.readFile(".config", RouteCode.Root);

        Client[] listClients = null;

        int numberOfClients = yamlConfig.getNoClients();
        if (numberOfClients != 0) {
            listClients = new Client[numberOfClients];

            for (int i = 0; i < listClients.length; i++) {
                listClients[i] = new Client();
            }
        }

        for (int i = 0; i < numberOfClients; i++) {
            listClients[i].readFile("C" + String.format("%04d", i + 1), RouteCode.Clients);
        }
        return listClients;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }
}
