package com.example.SocketConnection;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketServer {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final Object queueLock = new Object();
    private static BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

    private static final String BASE64_SECRET_KEY = "tQnQCfrs6yqTg+iSd1/gd5krCdG5cHleOPedcVlIRTg=";
   private static List<String> decryptedList = new ArrayList<>();
//   private static Queue<byte[]> queue = new LinkedList<>();

//    public static void main(String[] args) throws IOException {
    public void ckeck(){
        try {
            SocketServer socketServer = new SocketServer();
            socketServer.monitorQueue();

            // Create a server socket channel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("192.168.1.7", 8080));

            // Accept connections from clients
            System.out.println("Server is running...");
            while (true) {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                System.out.println("Client connected: " + clientSocketChannel.getRemoteAddress());

                // Handle client connection
                handleClient(clientSocketChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void monitorQueue() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            while (true) {
                try {
                    decodeData(queue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            executor.shutdown();
        });
    }
    private static void handleClient(SocketChannel clientSocketChannel) throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // Receive data from the client
            while (clientSocketChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                String receivedData = new String(bytes);
                synchronized (queue) {
                    queue.offer(bytes);
                }
                buffer.clear();
            }
        } finally {
            clientSocketChannel.close();
        }
    }


public static void decodeData(byte[] receivedPacket){
    try {
        // Decode base64-encoded key
        byte[] secretKeyBytes = Base64.getDecoder().decode(BASE64_SECRET_KEY);
        // Decrypt data
        byte[] decryptedBytes = decrypt(receivedPacket, secretKeyBytes);
        String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);
        String[] values = decryptedData.split(",");


        Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z]+)");

        // Create a matcher to find the matches in the data string
        Matcher matcher = pattern.matcher(decryptedData);

        // Iterate over the matches
        while (matcher.find()) {
            // Extract the key and value from the match
            String value = matcher.group(1);
            String key = matcher.group(2);
            if (key.equals("spoone")){
                System.out.println("spo "+value);
            }
            System.out.println("Key: " + key + ", Value: " + value);
        }

        System.out.println("decryptedList 0 "+values[0]);
        System.out.println("decryptedList 2 "+values[1]);
        System.out.println("decryptedList 3 "+values[2]);
        System.out.println("decryptedList 4 "+values[3]);
//        System.out.println("decryptedList 5 "+values[4]);
//        System.out.println("decryptedList 6 "+values[5]);
//        System.out.println("decryptedList 7 "+values[6]);
//        System.out.println("decryptedList 8 "+values[7]);
//        System.out.println("decryptedList 9 "+values[8]);
        System.out.println("decryptedList 10 "+decryptedData);
        decryptedData = null;
//        receivedPacket = null;
        // Display original data
//        System.out.println("Original Data:");
//        for (String value : decryptedList) {
//            System.out.println(value);
//        }
        queue.poll();
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
             BadPaddingException e) {
        e.printStackTrace();
    }
}
    private static byte[] decrypt(byte[] data, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }


    // Function to deserialize decrypted data
//    private static List<String> deserialize(String decryptedData) {
//        String[] parts = decryptedData.split(":");
//        for (String part : parts) {
//            decryptedList.add(part);
//        }
//        return decryptedList;
//    }
}

