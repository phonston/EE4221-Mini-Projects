package student;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Formatter;

/**
 * A slave program that compute the product of two matrix objects and return to
 * the master.
 *
 * @author vanting
 */
public class Slave {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 42210;

    /**
     * Driver function.
     */
    public static void main(String[] args) throws InterruptedException {

        // If run it on EC2, provide the master's IP through command line argument
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        System.out.println("Connecting to the master.");
        try (
                Socket socket = new Socket(host, DEFAULT_PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Matrix matA = (Matrix) in.readObject();
            Matrix matB = (Matrix) in.readObject();
            Matrix product = matA.multiply(matB);
            out.writeObject(product);

            System.out.println("Task is completed.");

        } catch (UnknownHostException e) {
            System.err.println("Unknown master URL.");
        } catch (IOException e) {
            System.err.println("Connection is terminated by master.");
        } catch (ClassNotFoundException ex) {
            System.err.println("Class of the serialized object cannot be found.");
        }

    }
}

// ------------------------------------------------------------------------------------------------------
class Matrix implements Serializable {

    private long[][] nums;

    public Matrix() {
        this(1, 1);
    }

    public Matrix(int row, int col) {
        nums = new long[row][col];
    }

    // Initialize this matrix with random numbers between 0 to n (exclusive)
    public Matrix(int row, int col, int n) {
        this(row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                nums[i][j] = (int) (Math.random() * n);
            }
        }
    }

    public Matrix(long[][] n) {
        this(n.length, n[0].length);
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[0].length; j++) {
                nums[i][j] = n[i][j];
            }
        }
    }

    // Copy constructor
    public Matrix(Matrix other) {
        this(other.row(), other.col());
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[0].length; j++) {
                nums[i][j] = other.at(i, j);
            }
        }
    }

    // -----------------------------------------------------------------
    public long at(int row, int col) {
        return nums[row][col];
    }

    public int row() {
        return nums.length;
    }

    public int col() {
        return nums[0].length;
    }

    // Return a row of this matrix
    public long[] rowAt(int row) {
        return nums[row].clone();
    }

    /**
     * Return a sub-matrix of this matrix.
     * The sub-matrix includes the rows from 'start' to 'end - 1'.
     */
    public Matrix subMatrix(int start, int end) {
        long[][] temp = new long[end - start][];
        for (int i = 0; i < (end - start); i++) {
            temp[i] = nums[start + i];
        }
        return new Matrix(temp);
    }

    // Return the product of this multiplying other; return null on fail
    public Matrix multiply(Matrix other) {

        int row1 = this.nums.length;
        int col1 = this.nums[0].length;
        int row2 = other.nums.length;
        int col2 = other.nums[0].length;

        if (col1 == row2) {

            long sum = 0;
            long[][] product = new long[row1][col2];

            for (int i = 0; i < row1; i++) {
                for (int j = 0; j < col2; j++) {
                    for (int k = 0; k < col1; k++) {
                        sum = sum + nums[i][k] * other.nums[k][j];
                    }
                    product[i][j] = sum;
                    sum = 0;
                }
            }
            return new Matrix(product);
        } else {
            return null;
        }
    }

    public void print() {
        System.out.println(this);
    }

    // Return a string representation of this matrix
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        Formatter fmt = new Formatter(sb);
        int row = this.nums.length;
        int col = this.nums[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                fmt.format("[%2d]", this.nums[i][j]);
            }
            fmt.format("%n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (o != null && o instanceof Matrix) {
            Matrix other = (Matrix) o;

            if (nums.length != other.row()) {
                return false;
            }

            if (nums[0].length != other.col()) {
                return false;
            }

            for (int i = 0; i < nums.length; i++) {
                for (int j = 0; j < nums[0].length; j++) {
                    if (nums[i][j] != other.at(i, j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.deepHashCode(this.nums);
        return hash;
    }

    // function return a matrix from appending given matrix to the end of this
    // matrix in O(1)
    public Matrix append(Matrix other) {
        int row = this.row() + other.row();
        int col = this.col();
        long[][] temp = new long[row][col];
        for (int i = 0; i < this.row(); i++) {
            temp[i] = this.rowAt(i);
        }
        for (int i = 0; i < other.row(); i++) {
            temp[i + this.row()] = other.rowAt(i);
        }
        return new Matrix(temp);
    }
}
