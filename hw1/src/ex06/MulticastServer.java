package ex06;

/*
 * MulticastServer.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author vanting
 */
import java.io.*;

public class MulticastServer {
    public static void main(String[] args) throws IOException {
        
        new MulticastServerThread().start();
    }
}