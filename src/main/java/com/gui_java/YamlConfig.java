package com.gui_java;

public class YamlConfig implements YmlWritable {
    private int noFactures;
    private int noClients;
    private Client entreprise;

    public YamlConfig() {
        this.noFactures = -1;
        this.noClients = -1;
        this.entreprise = new Client();
    }

    public YamlConfig(int no_factures, int no_clients) {
        this.noFactures = no_factures;
        this.noClients = no_clients;
    }

    @Override
    public String toFile() {
        return "no_factures: " + noFactures + "\n" +
                "no_clients: " + noClients + "\n" +
                "name: " + entreprise.getName() + "\n" +
                "adresse: " + entreprise.getAdresse() + '\n' +
                "telephone: " + entreprise.getTelephone() + '\n' +
                "courriel: " + entreprise.getCourriel() + '\n';
    }

    public boolean readFile(String nameFile, String route) {
        String[] data = YamlHandler.readFile(nameFile, route);

        if (data.length != 0) {           //si le fichier n'existe pas, il renvoie un array vide
            for (int i = 0; i < data.length; i++) {
                String[] keyvalue = data[i].split(": ");
                switch (keyvalue[0]) {
                    case "no_factures":
                        this.noFactures = keyvalue.length < 2 ? 0 : Integer.parseInt(keyvalue[1]);
                        break;

                    case "no_clients":
                        this.noClients = keyvalue.length < 2 ? 0 : Integer.parseInt(keyvalue[1]);
                        break;

                    default:
                        break;
                }
            }
            if (this.entreprise.readFile(nameFile, route))
                return true;
            else
                return false;
        } else {
            return false;
        }
    }


    public int getNoFactures() {
        return noFactures;
    }

    public void setNoFactures(int no_factures) {
        this.noFactures = no_factures;
    }

    public int getNoClients() {
        return noClients;
    }

    public void setNoClients(int no_clients) {
        this.noClients = no_clients;
    }

    public Client getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Client entreprise) {
        this.entreprise = entreprise;
    }
}
