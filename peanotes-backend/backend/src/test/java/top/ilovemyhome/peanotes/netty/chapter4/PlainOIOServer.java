package top.ilovemyhome.peanotes.netty.chapter4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlainOIOServer {

    public static void main(String[] args) throws IOException {
        PlainOIOServer server = new PlainOIOServer();
        server.serve(1111);
    }

    public void serve(int port) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port);

        for(;;){
            final Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
            new Thread(() -> {
                OutputStream out ;
                InputStream in;
                try {
//                    in = clientSocket.getInputStream();
//                    String clientName = new String(in.readNBytes(4), StandardCharsets.UTF_8);
//                    System.out.println(clientName);

                    out = clientSocket.getOutputStream();
                    out.write(("Hi! \n").getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    clientSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    try {
                        clientSocket.close();
                    } catch (IOException ignore) {
                    }
                }
            }).start();
        }
    }
}
