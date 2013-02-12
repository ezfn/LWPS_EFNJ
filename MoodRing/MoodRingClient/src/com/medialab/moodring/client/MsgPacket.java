package com.medialab.moodring.client;


public class MsgPacket {
	
	String Time;
	String Sender;
	String Msg;
	
	public MsgPacket(String time_sent, String sender2, String message){
		
		Time = time_sent;
		Sender = sender2;
		Msg = message;
	}

}
