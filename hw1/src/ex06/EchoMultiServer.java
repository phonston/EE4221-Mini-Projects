package ex06;

/**
 *
 * @author vanting
 */
import java.net.*;
import java.io.*;

public class EchoMultiServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(33333);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 33333.");
            System.exit(-1);
        }

        while (listening)
	    new EchoMultiServerThread(serverSocket.accept()).start();

        serverSocket.close();
    }
}