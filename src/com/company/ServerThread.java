package com.company;


import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread {
    static HashMap<Integer, String> Users = new HashMap<>();
    static HashMap<String, ArrayList<Message>> messageQueue = new HashMap<>();

    public static void main(String[] args) {
        ServerSocket server = null;

        try {
            server = new ServerSocket(4321);
            server.setReuseAddress(true);

            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected "
                        + client.getInetAddress()
                        .getHostAddress());

                ClientHandler clientSock
                        = new ClientHandler(client);

                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            System.out.println("Disconnected !");
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("Disconnected !");
                }
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            Random rnd = new Random();
            int id = rnd.nextInt(99999999);

            while (Users.containsKey(id)) {
                id = rnd.nextInt(99999999);
            }

            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                Message msg;

                if ((msg = (Message) in.readObject()) != null) {

                    // Check If The Username Is Used
                    String usr = msg.getText();
                    System.out.println("User: " + usr);
                    while (Users.containsValue(usr)) {
                        msg = new Message(0, "", "This username is already token.");
                        out.writeObject(msg);
                        msg = (Message) in.readObject();
                        usr = msg.getText();
                    }
                    msg = new Message(id, "", "");
                    Users.put(id, usr);
                    messageQueue.put(usr, new ArrayList<Message>() {
                    });
                    out.writeObject(msg);


                    while (!Objects.equals(msg.getText(), "exit()")) {
                        msg = (Message) in.readObject();
                        if(!messageQueue.get(usr).isEmpty()){
                            for (Message m:messageQueue.get(usr)) {
                                out.writeObject(m);
                            }
                            messageQueue.get(usr).clear();
//                            out.writeObject(messageQueue.get(usr).get(0));
//                            System.out.println(messageQueue.get(usr).get(0).getText());
//                            messageQueue.get(usr).remove(0);
                            out.flush();
                        } else{
                            out.writeObject(new Message(0, "", ""));
                        }

                        if (!Objects.equals(msg.getText(), "")) {

                            try{
                                Users.get(msg.getReceiverName());
                            }catch (Exception ignored){
                                msg = new Message(0, "", "User Not Found.");
                                out.writeObject(msg);
                            }

                            ArrayList<Message> q = messageQueue.get(msg.getReceiverName());
                            q.add(new Message(0, "", Users.get(msg.getSenderID()) + " : " + msg.getText()));

                            System.out.println("Sender: " + msg.getSenderID() + " Receiver: " +
                                    msg.getReceiverName() + " Text: " + msg.getText());
                            msg.setText("");
                        }
                    }
                    // Delete the User
                    Users.remove(msg.getSenderID());

                    // close
                    out.close();
                    in.close();



                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

