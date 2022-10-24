package ex06;

/*
 * EchoClient.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author vanting
 */
import java.io.*;
import java.net.*;

public class EchoClient {
    
    public static void main(String[] args) throws IOException {
        
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        try {
            // the server runs locally
            echoSocket = new Socket("127.0.0.1", 33333);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about this server.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to server.");
            System.exit(1);
        }
        
        // for reading user input
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        String userInput, serverInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            if (out.checkError()) {
                System.out.println("Connection closed by server.");
                break;
            }
            
            // verify server echo
            serverInput = in.readLine();
            System.out.println("Server echo: " + serverInput);
            if (serverInput.equals("Bye")) {
                System.out.println("Connection closed by user.");
                break;
            }
        }
        
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}