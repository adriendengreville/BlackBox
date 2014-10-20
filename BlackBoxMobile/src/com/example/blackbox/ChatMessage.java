package com.example.blackbox;
import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	// KEYS pour l'échange de clés
	static final int PASSWORD = 0, MESSAGE = 1, LOGOUT = 2, KEYCommon = 3, KEYPublic = 4, KEYPrivate = 5, ConnectERR = 6, ConnectOK = 7, MP = 8;
	private int type;
	private String message;
	private String dest;
	private String timeStamp;
	private String sender;
	
	// constructor
	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
		this.sender = "null";
	}
	
	public ChatMessage(int type, String message, String dest) {
		super();
		this.dest = dest;
	}
	
	// getters
	int getType() {
		return type;
	}
	
	void setType(int type){
		this.type = type;
	}
	
	String getMessage() {
		return message;
	}
	
	void setMessage(String msg){
		this.message = msg;
	}
	
	String getDest(){
		return this.dest;
	}
	
	void setDest(String dest){
		this.dest = dest;
	}
	
	void setTimeStamp(String time){
		this.timeStamp = time;
	}
	
	String getTimeStamp(){
		return this.timeStamp;
	}
	
	void setSender(String sender){
		this.sender = sender;
	}
	
	String getSender(){
		return this.sender;
	}
}

