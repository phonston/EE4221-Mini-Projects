package ex06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author vanting
 */
public class ClientSocket  {

    public static void main(String[] args) throws IOException {
        
        Socket s = new Socket("localhost", 33333);
        //Socket s = new Socket(args[0], Integer.parseInt(args[1]));

        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String str = null;
        while ((str = in.readLine()) != null) {
            System.out.println(str);
        }
    }
}
