package journey.controller;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * class to read information from a CSV file about charging stations
 */
public class ReadCSV2
{
    public String line = "";
    public String splitBy = ",";
    public ArrayList<String[]> chargers = new ArrayList<String[]>();
    public String filename;

    /**
     * set the CSV file
     * @param file
     */
    ReadCSV2(String file) {
        filename = file;
    }

    /**
     * return a list of the chargers that are in the CSV
     * @return
     */
    public ArrayList<String[]> getStations() {

        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = 0;
            while ((line = br.readLine()) != null)  {
                String[] charger = line.split(splitBy);
                if (i != 0) {
                    chargers.add(charger);
                }
                i += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chargers;
    }

    /**
     * print info about the given charger in a textual format that is easily human readable
     * @param charger
     */
    public void printInfo(String[] charger) {

        //find address
        String address = "";
        int a = 6;
        String next = charger[a];
        while (next.charAt(next.length()-1) != '\"') {
            address = address + next + ",";
            a += 1;
            next = charger[a];
        }
        address = address + next;

        //find list of connectors
        String connectors = "";
        for (int i = a+11; i < charger.length -1; i++){
            connectors = connectors + charger[i];
        }

        //print in textual format
        System.out.println(charger[3] + "\nX = " + charger[0] + "\nY = " + charger[1] + "\nOBJ_ID = " + charger[2] + "\nOperator = " +
                charger[4] + "\nOwner = " + charger[5] + "\nAddress = " + address + "\nis24Hours = " + charger[a+1] + "\nCar Parks = " + charger[a+2] + "\nhasCarparkCost = " +
                charger[a+3] + "\nTimeLimit = " + charger[a+4] + "\nTouristAttraction = " + charger[a+5] + "\nlatitude = " +
                charger[a+6] + "\nlongitude = " + charger[a+7] + "\ncurrentType = " + charger[a+8] + "\ndateFirstOperational = " +
                charger[a+9] + "\nnumberOfConnectors = " + charger[a+10] + "\nconnectorsList = " + connectors + "\nhasChargingCost = " +
                charger[charger.length - 1]);
    }
    
    public static void main(String[] args)
    {
        String file = "/Users/ellacalder/Desktop/EV_Roam_charging_stations.csv";
        ReadCSV2 stations = new ReadCSV2(file);
        ArrayList<String[]> chargers = stations.getStations();
        stations.printInfo(chargers.get(8));

    }
}
