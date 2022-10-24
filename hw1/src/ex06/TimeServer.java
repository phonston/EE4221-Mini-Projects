/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex06;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 *
 * @author vanting
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        
        ServerSocket ss = new ServerSocket(33333);
        
        while (true) {
            Socket s = ss.accept();
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            out.println(new Date());
            s.close();
        }
    }
}
