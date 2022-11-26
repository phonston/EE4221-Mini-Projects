package student;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The master divides a matrix into N partitions and distributes them to N
 * slaves
 * to compute the multiplication of another matrix in parallel.
 *
 * @author vanting
 */
public class MasterDDDDDDDD {

    // The master is listening at this port
    public static final int DEFAULT_PORT = 42210;

    // Each socket in the list maintains a connection to a slave
    private static final List<Socket> sockets = new CopyOnWriteArrayList<>();

    /**
     * Driver function.
     */
    public static void main(String[] args) {

        acceptConnectionFromSlaves();

        System.out.println("Enter [run] to start computation.");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            if (scanner.next().equalsIgnoreCase("run")) {
                break;
            }
        }

        // Create two matrices with random values
        Matrix matA = new Matrix(1000, 1200, 100);
        Matrix matB = new Matrix(1200, 1500, 100);

        // We request the slaves to compute the product first
        long start = System.currentTimeMillis();
        Matrix multiProduct = parallelMultiply(matA, matB);
        double multiTime = System.currentTimeMillis() - start;

        // Then we compute the product locally using the built-in mutliply() method
        start = System.currentTimeMillis();
        Matrix singleProduct = matA.multiply(matB);
        double singleTime = System.currentTimeMillis() - start;

        // Finally, we check if they are the same and their runtime ratio.
        if (singleProduct.equals(multiProduct)) {
            System.out.println("The computation is correct.");
            System.out.println("The execution time of the cluster is: " + multiTime / 1000);
            System.out.println("The execution time of the master is: " + singleTime / 1000);
            System.out.println("Single-to-Multi Ratio: " + singleTime / multiTime);
        } else {
            System.out.println("The computation is NOT correct.");
        }

        System.exit(0);

    }

    /**
     * This method sends two matrices to a slave through the given socket. It
     * returns the product matrix computed by the slave.
     * 
     * @param socket the socket connected to the slave
     * @param matA   the first matrix
     * @param matB   the second matrix
     * @return the product of first and second matrices
     */
    public static Matrix executeMultiplicationOnSlave(Socket socket, Matrix matA, Matrix matB) {

        Matrix product = null;

        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(matA);
            out.writeObject(matB);

            product = (Matrix) in.readObject();

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to slave.");
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.err.println("Class of the serialized object cannot be found.");
        }

        return product;
    }

    // =====================================================================================================================
    // The following methods are to be completed by students.

    /**
     * This method creates a thread to listen at the DEFAULT_PORT. The thread
     * will create a socket for each incoming connection accepted and store the
     * socket in the private list 'sockets' declared above. The thread runs
     * endlessly.
     */
    public static void acceptConnectionFromSlaves() {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.execute(() -> {
            try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    sockets.add(socket);

                    System.out.println(sockets.size() + " slaves are connected.");
                }
            } catch (IOException e) {
                System.err.println("Couldn't listen at port " + DEFAULT_PORT);
            }
        });
    }

    /**
     * This method divides the matrix A into N partitions and distributes them to
     * N slaves to compute the multiplication of another matrix B in parallel.
     * 
     * @param matA the matrix A
     * @param matB the matrix B
     * @return the product of A and B matrices
     */
    public static Matrix parallelMultiply(Matrix matA, Matrix matB) {
        int n = sockets.size();

        List<Matrix> matrixList = new CopyOnWriteArrayList<>();
        List<Matrix> partitions = partition(matA, n);

        ExecutorService executor = Executors.newFixedThreadPool(n);
        for (int i = 0; i < n; i++) {
            int finalI = i;
            executor.execute(() -> {
                Matrix product = executeMultiplicationOnSlave(sockets.get(finalI), partitions.get(finalI), matB);
                // no need to synchronize add() as executor functions are already thread safe
                // (google this for doubts)
                matrixList.add(product);
            });
        }

        Matrix result = combine(matrixList);
        // System.out.println("After combination:");
        // result.print();
        return result;
    }

    /**
     * This method divides the given matrix into N equal-size partitions. The
     * number of partitions is specified by the argument slice. If the matrix
     * cannot be divided evenly, the extra rows go to the last (bottom) partition.
     * 
     * For example, if the matrix has ten rows and the slice is 3, then it will
     * return a list of matrices in the sizes 3-3-4 respectively.
     * 
     * @param matrix the matrix to be split into multiple partitions
     * @param slice  the number of partitions
     * @return a list of the partitioned matrices
     */
    public static List<Matrix> partition(Matrix matrix, int slice) {
        List<Matrix> matrixList = new CopyOnWriteArrayList<>();
        int row = matrix.row(),
                rowPerSlice = row / slice,
                remainder = row % slice,
                start = 0,
                end = rowPerSlice;

        for (int i = 0; i < slice; i++) {
            if (i == slice - 1) {
                end += remainder;
            }
            Matrix subMatrix = matrix.subMatrix(start, end);
            matrixList.add(subMatrix);
            start = end;
            end += rowPerSlice;
        }

        return matrixList;
    }

    /**
     * This method merges a given list of partitioned matrices into one matrix.
     * The partitioned matrices are assumed having same number of columns. The
     * merge follows the partition's order in the list. The first partition is
     * on the top of the merged matrix.
     * 
     * @param matrices a list of the partitioned matrices
     * @return the merged matrix
     */
    public static Matrix combine(List<Matrix> matrices) {
        Matrix result = null;
        for (Matrix matrix : matrices) {
            if (result == null)
                result = matrix;
            else
                result = append(result, matrix);
        }

        return result;
    }

    public static Matrix append(Matrix matA, Matrix matB) {
        int row = matA.row() + matB.row();
        int col = matA.col();
        long[][] temp = new long[row][col];
        for (int i = 0; i < matA.row(); i++) {
            temp[i] = matA.rowAt(i);
        }
        for (int i = 0; i < matB.row(); i++) {
            temp[i + matA.row()] = matB.rowAt(i);
        }
        return new Matrix(temp);
    }
}
