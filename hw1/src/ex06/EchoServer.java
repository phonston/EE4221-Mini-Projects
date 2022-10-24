package ex06;

/*
 * EchoServer.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author vanting
 */
import java.net.*;
import java.io.*;

public class EchoServer {
    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(33333);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 33333.");
            System.exit(1);
        }
        
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        String inputLine, outputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Client says: " + inputLine);
            outputLine = inputLine;
            out.println(outputLine);
            if (outputLine.equals("Bye"))
                break;
        }
        
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}