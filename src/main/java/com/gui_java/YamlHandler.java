package com.gui_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class YamlHandler {

    public static String[] readFile(String nameFile, String route) {
        Scanner myReader = new Scanner("");
        Vector<String> v = new Vector<>();
        try {
            String userHomeDir = System.getProperty("user.home");
            File file = new File(userHomeDir + route + nameFile + ".yml");
            myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data != "") {
                    v.add(data);
                }
            }
        } catch (FileNotFoundException e) {
            Log.print(e.getMessage());
        } finally {
            myReader.close();
        }
        return v.toArray(new String[v.size()]);
    }

    public static void writeFile(YmlWritable o, String nameFile, String route) {
        FileWriter writer = null;
        try {
            String userHomeDir = System.getProperty("user.home");
            File file = new File(userHomeDir + route + nameFile + ".yml");
            writer = new FileWriter(file);

            writer.write(o.toFile());
        } catch (IOException e) {
            Log.print(e.getMessage());
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                Log.print("Cannot close what is null");
            }
        }
    }
}
