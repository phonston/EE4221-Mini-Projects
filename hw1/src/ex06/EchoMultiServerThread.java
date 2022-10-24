package ex06;

/**
 *
 * @author vanting
 */
import java.net.*;
import java.io.*;

public class EchoMultiServerThread extends Thread {
    private Socket socket = null;

    public EchoMultiServerThread(Socket socket) {
	//super("EchoMultiServerThread");
	this.socket = socket;
    }

    public void run() {

	try {
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
	    socket.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}