package pt.amc.nameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/nameserver")
public class NameServerService {

    /**
     * Name Server Webservice
     *
     * @param nick
     * @param pin
     * @return 
     * @authors David Arco - 30005194, Diego Soares - 30005066
     */
    @GET
    @Path("/login")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String Login(@QueryParam("nick") String nick, @QueryParam("pin") String pin) {

        // Creates a key that contains "nick" and "pin"
        String nickPin = nick + "," + pin;

        // Create a file inside cluster - database
        // Compare nickPin with string in database
        Boolean foundNickPin = false;

        BufferedReader fileRead;

        // Directory /srv/nameserver/
        String fileName = "/srv/nameserver/bindNick.txt";
        File bindNickFile = new File(fileName);
        System.out.println("File " + fileName + " is located in: " + bindNickFile.getAbsolutePath());

        try {
            fileRead = new BufferedReader(new FileReader(bindNickFile));
            String readFileLine = fileRead.readLine();

            while (readFileLine != null && foundNickPin == false) {
                if (readFileLine.equalsIgnoreCase(nickPin)) {
                    foundNickPin = true;
                    break;
                }
                readFileLine = fileRead.readLine();
            }
            fileRead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (foundNickPin == true) {
            return "OK";
        } else {
            return "Error. Nickname or PIN do not exist. Please, register first.";
        }
    }

    @GET
    @Path("/register")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String Register(@QueryParam("nick") String nick, @QueryParam("pin") String pin) {

        // Creates a key that contains "nick" and "pin"
        String nickPin = nick + "," + pin;

        // Line break after new database entry
        String lineBreak = "\n";

        // Append value of nickPin into database
        BufferedWriter writeFile;
        try {
            String fileName = "/srv/nameserver/bindNick.txt";
            File bindNickFile = new File(fileName);
            System.out.println("File " + fileName + " is located in: " + bindNickFile.getAbsolutePath());

            writeFile = new BufferedWriter(new FileWriter(bindNickFile, true));
            writeFile.append(nickPin + lineBreak);
            writeFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "User Registration successful! Please Login.";
    }

    @GET
    @Path("/forgotpin")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String forgotPIN(@QueryParam("nick") String nick) {

        // Create part of the key that contains nickname
        String nickName = nick;

        // Compare nickname with string in database
        Boolean foundPIN = false;
        String PIN = null;

        BufferedReader readFile;
        try {
            String fileName = "/srv/nameserver/bindNick.txt";
            File bindNickFile = new File(fileName);
            System.out.println("File " + fileName + " is located in: " + bindNickFile.getAbsolutePath());

            readFile = new BufferedReader(new FileReader(bindNickFile));
            String fileLine = readFile.readLine();

            while (fileLine != null && foundPIN == false) {
                if (fileLine.contains(nickName)) {
                    foundPIN = true;
                    PIN = fileLine.split(",")[1];
                }
                fileLine = readFile.readLine();
            }
            readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (foundPIN == true) {
            return PIN;
        } else {
            return "Error. Couldn't find user.";
        }
    }

}
