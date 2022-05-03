package com.company;

import java.net.*;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Socket client = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            client = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            Scanner txtinput_scanner = new Scanner(System.in);
            String txtinput = "";
            System.out.println("Please Enter Your Name:");
            Message msg = new Message(0, "", txtinput_scanner.nextLine());
            out.writeObject(msg);
            msg = (Message) in.readObject();
            while (Objects.equals(msg.getText(), "This username is already token.")) {
                System.out.println(msg.getText());
                System.out.println("Please Enter Your Name:\n");
                msg = new Message(0, "", txtinput_scanner.nextLine());
                out.writeObject(msg);
                msg = (Message) in.readObject();
            }

            int id = msg.getSenderID();

            String rcvr = null;
            while (rcvr != "") {
                System.out.println("Please Enter Your Friend Name:");
                rcvr = txtinput_scanner.nextLine();
                while (!txtinput.equals("exit()")) {
                    txtinput = txtinput_scanner.nextLine();
                    msg = new Message(id, rcvr, txtinput);
                    out.writeObject(msg);
                    msg = (Message) in.readObject();
                    if (!Objects.equals(msg.getText(), "")) {
                        System.out.println(msg.getText());
                    }
                    out.flush();
                }
            }
            out.close();
            in.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
