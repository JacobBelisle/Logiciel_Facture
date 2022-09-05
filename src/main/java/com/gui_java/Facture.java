package com.gui_java;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Facture implements YmlWritable {
    private String numberFacture;
    private Boolean actif;
    private Client client;
    private String note;
    private String date;
    private Vector<Article> products;
    private Vector<Paiement> paiements;
    private Float grandTotal;
    private Float solde;

    public Facture() {
        numberFacture = "-1";
        actif = false;
        client = new Client();
        note = "";
        products = new Vector<>();
        paiements = new Vector<>();
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public Facture(String numberFacture, Boolean actif, Client client, String note, Article[] a) {
        this.numberFacture = numberFacture;
        this.actif = actif;
        this.client = client;
        this.note = note;

        for (int i = 0; i < a.length; i++) {
            this.products.add(a[i]);
        }
    }

    public Facture(Facture f) {
        this.numberFacture = f.getNumberFacture();
        this.actif = f.getActif();
        this.client = f.getClient();
        this.note = f.getNote();
        this.products = f.getProducts();
        this.paiements = f.getPaiements();
        this.date = f.getDate();
    }

    public Facture(String nameFile, String route) {
        numberFacture = "-1";
        actif = false;
        client = new Client();
        note = "";
        products = new Vector<>();
        paiements = new Vector<>();
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.readFile(nameFile, route);
    }

    public boolean readFile(String nameFile, String route) {
        String[] data = YamlHandler.readFile(nameFile, route);
        Article a = new Article();
        Paiement p = new Paiement();

        boolean writingArticle = false;
        boolean writingPaiement = false;

        if (data.length != 0) {           //si le fichier n'existe pas, il renvoie un array vide
            for (int i = 0; i < data.length; i++) {
                String[] keyvalue = data[i].split(": ");
                switch (keyvalue[0]) {
                    case "number_facture":
                        this.numberFacture = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "actif":
                        this.actif = keyvalue.length >= 2 && Boolean.parseBoolean(keyvalue[1]);
                        break;

                    case "number_client":
                        this.client = new Client();
                        this.client.readFile("C" + keyvalue[1], RouteCode.Clients);
                        break;

                    case "note":
                        this.note = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "date":
                        this.date = keyvalue.length < 2 ? "" : keyvalue[1];
                        break;

                    case "Product[":
                        writingArticle = true;
                        a = new Article();
                        break;

                    case "Product]":
                        writingArticle = false;
                        this.products.add(a);
                        break;

                    case "Paiement[":
                        writingPaiement = true;
                        p = new Paiement();
                        break;

                    case "Paiement]":
                        writingPaiement = false;
                        this.paiements.add(p);
                        break;

                    default:
                        break;
                }

                if (writingArticle) {
                    switch (keyvalue[0]) {
                        case "\tdescription":
                            a.setDescription(keyvalue[1]);
                            break;

                        case "\tquantite":
                            a.setQuantite(Integer.parseInt(keyvalue[1]));
                            break;

                        case "\ttaxes":
                            a.setTaxes(Boolean.parseBoolean(keyvalue[1]));
                            break;

                        case "\tprice":
                            a.setPrice(Float.parseFloat(keyvalue[1]));
                            break;

                        case "\ttotal":
                            a.setTotal(Float.parseFloat(keyvalue[1]));
                            break;

                        default:
                            break;
                    }
                }

                if (writingPaiement) {
                    switch (keyvalue[0]) {
                        case "\tdate":
                            p.setDate(keyvalue[1]);
                            break;

                        case "\tprice":
                            p.setPrice(Float.parseFloat(keyvalue[1]));
                            break;

                        default:
                            break;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        char[] c = new char[(25) + (25 - client.getName().length())];
        for (int i = 0; i < client.getName().length(); i++) {
            c[i] = client.getName().charAt(i);
        }
        for (int i = client.getName().length(); i < c.length; i++) {
            c[i] = ' ';
        }

        String s1 = "";
        for (int i = 0; i < c.length; i++) {
            s1 += c[i];
        }
        String s = numberFacture + "   " + s1 + note;

        return s;
    }

    @Override
    public String toFile() {
        String s = "number_facture: " + this.numberFacture + "\n" +
                "actif: " + actif + "\n" +
                "number_client: " + client.getNumberClient() + "\n" +
                "note: " + note + '\n' +
                "date: " + date + '\n';

        for (int i = 0; i < products.size(); i++) {
            s += "Product[\n";
            s += products.get(i).toFile() + '\n';
            s += "Product]\n\n";
        }

        for (int i = 0; i < paiements.size(); i++) {
            s += "Paiement[\n";
            s += paiements.get(i).toFile() + '\n';
            s += "Paiement]\n\n";
        }

        return s;
    }

    public void generatePDF() {
        Document document = new Document(PageSize.LETTER);

        Font title = new Font(Font.FontFamily.HELVETICA, 25);
        Font normal = new Font(Font.FontFamily.HELVETICA, 12);

        try {
            YamlConfig yamlConfig = new YamlConfig();
            yamlConfig.readFile(".config", RouteCode.Root);
            PdfWriter.getInstance(document, new FileOutputStream(new File(RouteCode.HOME + RouteCode.Generated + "F" + this.getNumberFacture() + ".pdf")));
            document.open();

            Paragraph p = new Paragraph("Facture # " + this.getNumberFacture(), title);
            p.setAlignment(Element.ALIGN_LEFT);

            p.setFont(normal);

            PdfPTable t1 = new PdfPTable(2);
            t1.setSpacingAfter(15);
            t1.setSpacingBefore(15);
            t1.setTotalWidth(PageSize.LETTER.getWidth() - 70);
            t1.setLockedWidth(true);
            t1.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            t1.setHorizontalAlignment(Element.ALIGN_CENTER);
            t1.getDefaultCell().setPadding(8);
            t1.addCell("Adresse de facturation:\n" + yamlConfig.getEntreprise().getName() +
                    "\n" + yamlConfig.getEntreprise().getAdresse() +
                    "\n" + yamlConfig.getEntreprise().getTelephone() +
                    "\n" + yamlConfig.getEntreprise().getCourriel());
            t1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            t1.addCell("Facturé à: " + this.client.getName() +
                    "\nDate: " + this.date);

            PdfPTable table = new PdfPTable(4);
            table.setTotalWidth(PageSize.LETTER.getWidth() - 70);
            table.setLockedWidth(true);
            table.getDefaultCell().setPadding(8);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            table.getDefaultCell().setBorder(Rectangle.BOTTOM + Rectangle.TOP + Rectangle.LEFT);
            table.addCell("Description");

            table.getDefaultCell().setBorder(Rectangle.BOTTOM + Rectangle.TOP);
            table.addCell("Quantité");

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell("Prix");

            table.getDefaultCell().setBorder(Rectangle.BOTTOM + Rectangle.TOP + Rectangle.RIGHT);
            table.addCell("Total");

            table.setHeaderRows(1);

            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setPaddingLeft(5);
            table.getDefaultCell().setPaddingRight(8);
            table.getDefaultCell().setPaddingBottom(10);
            table.getDefaultCell().setPaddingTop(10);

            for (int i = 0; i < this.products.size(); i++) {
                if (i % 2 == 1)
                    table.getDefaultCell().setBackgroundColor(WebColors.getRGBColor("#cccccc"));
                else
                    table.getDefaultCell().setBackgroundColor(WebColors.getRGBColor("#FFFFFF"));

                table.getDefaultCell().setBorder(Rectangle.LEFT);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(this.products.get(i).getDescription());

                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(String.format("%d", this.products.get(i).getQuantite()));

                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(String.format("%.2f", this.products.get(i).getPrice()));

                table.getDefaultCell().setBorder(Rectangle.RIGHT);
                table.addCell(String.format("%.2f", this.products.get(i).getTotal()));
            }

            table.getDefaultCell().setBorder(Rectangle.TOP);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            Paragraph prices = new Paragraph("Grand Total: " + String.format("%.2f", this.grandTotal) + "\n" +
                    "Solde: " + String.format("%.2f", this.solde));
            prices.setAlignment(Element.ALIGN_RIGHT);
            prices.setIndentationRight(7);

            PdfPTable tNote = new PdfPTable(1);
            tNote.setTotalWidth(PageSize.LETTER.getWidth() - 70);
            tNote.setSpacingAfter(15);
            tNote.setLockedWidth(true);
            tNote.getDefaultCell().setPadding(8);
            tNote.addCell("Note: \n" + this.note + "\n\n\n\n");

            document.add(p);
            document.add(t1);
            document.add(tNote);
            document.add(table);
            document.add(prices);
        } catch (DocumentException | FileNotFoundException e) {
            Log.print(e.getMessage());
        } finally {
            document.close();
        }
    }

    public String getNumberFacture() {
        return numberFacture;
    }

    public void setNumberFacture(String numberFacture) {
        this.numberFacture = numberFacture;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client numberClient) {
        this.client = numberClient;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Article[] getProductsToArray() {
        return products.toArray(new Article[products.size()]);
    }

    public Vector<Article> getProducts() {
        return products;
    }

    public void addProducts(Article[] articles) {
        for (int i = 0; i < articles.length; i++) {
            this.products.add(articles[i]);
        }
    }

    public void updateProducts(int index, Article article) {
        this.products.get(index).setDescription(article.getDescription());
        this.products.get(index).setQuantite(article.getQuantite());
        this.products.get(index).setPrice(article.getPrice());
        this.products.get(index).setTaxes(article.getTaxes());
        this.products.get(index).setTotal(article.getTotal());
    }

    public Vector<Paiement> getPaiements() {
        return paiements;
    }

    public void addPaiement(Paiement p) {
        this.paiements.add(p);
    }

    public Float getTotalPaiements() {
        Float total = 0f;
        for (int i = 0; i < this.paiements.size(); i++)
            total += this.paiements.get(i).getPrice();

        return total;
    }

    public Float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Float getSolde() {
        return solde;
    }

    public void setSolde(Float solde) {
        this.solde = solde;
    }
}
