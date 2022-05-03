package com.company;

import java.io.Serializable;


public class Message implements Serializable {


    private int senderID;
    private String receiverName;
    private String text;

    public Message(int sdr, String rcvr, String tx) {
        setSenderID(sdr);
        setReceiverName(rcvr);
        setText(tx);
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSenderID() {return senderID;}

    public void setSenderID(int senderID) {this.senderID = senderID;}
}
