package com.example.SocketConnection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocketConnectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocketConnectionApplication.class, args);
SocketServer socketServer = new SocketServer();
socketServer.ckeck();
	}


}
