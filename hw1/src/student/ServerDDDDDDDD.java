package student;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Multi-threaded server program that multiplies matrices using fork-join
 * framework.
 *
 * @author vanting
 */
public class ServerDDDDDDDD implements Runnable {

    public static final int DEFAULT_PORT = 42210;
    private Socket socket;

    public ServerDDDDDDDD() {
    }

    public ServerDDDDDDDD(Socket socket) {
        this.socket = socket;
    }

    /**
     * Driver function. Start this server at the default port.
     */
    public static void main(String[] args) {
        start(DEFAULT_PORT);
    }

    /**
     * Start matrix server at the specified port. It should accept and handle
     * multiple client requests concurrently.
     *
     * @param port port number listened by the server
     */
    public static void start(int port) {

        // your implementation here

        // 1. accept a new connection from client
        // 2. create a task with the socket
        // 3. submit the task to a thread pool to execute
        ServerSocket serverSocket = null;
        boolean listening = true;
        try {
            // Set server port
            serverSocket = new ServerSocket(port);

            // Keep server running until a connection has been made
            while (listening)
                // Start a new thread once a connection has been accepted.
                new Thread(new ServerDDDDDDDD(serverSocket.accept())).start();
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }
    }

    /**
     * Handle a matrix client request. It reads two matrices from socket,
     * compute their product, and then send the product matrix back to the
     * client.
     */
    @Override
    public void run() {

        // your implementation here

        // 1. read two matrices from the socket
        // 2. call multiThreadMultiply() to compute their product
        // 3. send back the product matrix
        try {
            ObjectInputStream ob = new ObjectInputStream(socket.getInputStream());
            try {
                Matrix matA = null, matB = null, product = null;

                // Wait and read the sent object
                Object matrixA = ob.readObject();
                Object matrixB = ob.readObject();

                // Parse the received objects
                matA = (Matrix) matrixA;
                matB = (Matrix) matrixB;
                // System.out.println("Received objects!");
                // System.out.println(matA);
                // System.out.println(matB);

                // Start calculating
                if (matA == null | matB == null) {
                    ObjectOutputStream result = new ObjectOutputStream(socket.getOutputStream());
                    result.writeObject(null);
                    result.flush();
                } else {
                    product = multiThreadMultiply(matA, matB);
                    // Send result back to client
                    ObjectOutputStream result = new ObjectOutputStream(socket.getOutputStream());
                    result.writeObject(product);
                    result.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compute A x B using fork-join framework.
     * 
     * @param matA matrix A
     * @param matB matrix B
     * @return the matrix product of AxB
     */
    public static Matrix multiThreadMultiply(Matrix matA, Matrix matB) {

        Matrix product = null;

        // your implementation here

        // 1. create a fork-join task (parallelMultiply)
        // 2. submit the task to a fork-join pool
        try (ForkJoinPool pool = new ForkJoinPool()) {
            ParallelMultiply task = new ParallelMultiply(matA, matB);
            pool.invoke(task);
            product = task.product;
        }

        return product;
    }
}

/**
 * Design a recursive and resultless ForkJoinTask. It splits the matrix
 * multiplication
 * into multiple tasks to be executed in parallel.
 * 
 */
class ParallelMultiply extends RecursiveAction {

    // your implementation here
    Matrix matA = null, matB = null, product = null;
    private static int FORK_THRESHOLD = 1;

    ParallelMultiply(Matrix matA, Matrix matB) {
        this.matA = matA;
        this.matB = matB;
    }

    @Override
    protected void compute() {
        if (matA.row() == FORK_THRESHOLD) {
            product = matA.multiply(matB);
            // System.out.println("Finished a fork calculation");
            // System.out.println(product);
        } else {
            // Split the matrix into two matrices
            // since we are not given a function to split the matrix automatically I am
            // forced to create a helper function to split the matrix
            List<long[][]> result = splitter(matA);
            Matrix leftMat = new Matrix(result.get(0)), rightMat = new Matrix(result.get(1));

            // Split the workload to two
            // System.out.println("Forking left:" + leftMat + "\n");
            // System.out.println("Forking right:" + rightMat + "\n");
            ParallelMultiply leftMultiply = new ParallelMultiply(leftMat, matB);
            ParallelMultiply rightMultiply = new ParallelMultiply(rightMat, matB);
            invokeAll(leftMultiply, rightMultiply);

            // Merge the results
            product = new Matrix(
                    append(convertMatrixArr(leftMultiply.product),
                            convertMatrixArr(rightMultiply.product)));
        }
    }

    // Helper function to split matrix
    private List<long[][]> splitter(Matrix matA) {
        List<long[][]> result = new ArrayList<long[][]>();
        int middle = (int) Math.floor(matA.row() / 2),
                rows = matA.row(),
                cols = matA.col();

        long[][] leftArr = new long[middle][cols],
                rightArr = new long[rows - middle][cols],
                tempArr = new long[rows][cols];

        tempArr = convertMatrixArr(matA);

        System.arraycopy(tempArr, 0, leftArr, 0, middle);
        System.arraycopy(tempArr, middle, rightArr, 0, rows - middle);

        result.add(leftArr);
        result.add(rightArr);

        return result;
    }

    // Helper function to convert Matrix to array
    private long[][] convertMatrixArr(Matrix matA) {
        int rows = matA.row(),
                cols = matA.col();
        long[][] tempArr = new long[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                tempArr[i][j] = (int) matA.at(i, j);
        }

        return tempArr;
    }

    // Helper function to append 2D arrays
    public static long[][] append(long[][] a, long[][] b) {
        long[][] result = new long[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
