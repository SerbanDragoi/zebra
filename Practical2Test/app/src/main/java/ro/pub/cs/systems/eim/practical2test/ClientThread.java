package ro.pub.cs.systems.eim.practical2test;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String hour;
    private final String minute;
    private final TextView resultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String hour, String minute, TextView weatherForecastTextView) {
        this.address = address;
        this.port = port;
        this.hour = hour;
        this.minute = minute;
        this.resultTextView = weatherForecastTextView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            printWriter.println(hour);
            printWriter.flush();
            printWriter.println(minute);
            printWriter.flush();
            String hasPassed;

            // reads the weather information from the server
            while ((hasPassed = bufferedReader.readLine()) != null) {
                final String finalizedHasPassed = hasPassed;

                // updates the UI with the weather information. This is done using postt() method to ensure it is executed on UI thread
                resultTextView.post(() -> resultTextView.setText(finalizedHasPassed));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
