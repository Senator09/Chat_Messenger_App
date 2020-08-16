import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * [Chat Messenger is a simple application that enables clients(users)
to connect and exchange text-massages through a server. I have
also added a chat filter feature—any text that goes through the
server, gets screened. Similarly, bad words like IU (Indiana
University) gets filtered. This project significantly expanded my
understanding of network I/O, threads and complex data
structures (Java).]
 *
 * @author your name and section
 * @version date
 */
final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    public static Object obj = new Object();
    ChatFilter filter = new ChatFilter("/Users/alyekaba/Downloads/Project5/src/badwords");

    private ChatServer(int port) throws IOException {
        this.port = port;
        System.out.println(this.port);
    }

    public ChatServer(int port, String word_to_filter) throws IOException {
        this.port = port;
        this.filter = new ChatFilter(word_to_filter);
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 1500);
            while (true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void broadcast(String message) {
        message = filter.filter(message);
        synchronized (obj) {
            for (ClientThread client : clients) {
                client.writeMessage(message + "\n");
            }
        }
    }

    private void directMessage(String message, String username) {
        String[] line = null;
        String recipient = "";
        if(message.equals("/list")){
            recipient = username;
        }else {
            line = message.split(" ", 3);
            message = filter.filter(line[2]);
            recipient = line[1];
        }
        synchronized (obj) {
            for (ClientThread client : clients) {
                if (client.username.equals(recipient)) {
                    if (username.equals(recipient)) {
                        if(message.equals("/list")) {
                            for(int i=0; i < clients.size(); i++){
                                if(!clients.get(i).username.equals(username))
                                    client.writeMessage(clients.get(i).username);
                            }

                        }else{
                            client.writeMessage("You cant message yourself.");
                        }
                        return;
                    } else {
                        client.writeMessage(message);

                    }


                }
            }
        }

    }

    private void remove(int id) {
        synchronized (obj) {
            clients.removeIf(client -> client.id == id);
        }
    }
    //Check:
    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) throws IOException {
        ChatServer server;
        if (args.length == 1) {
            server = new ChatServer(Integer.parseInt(args[0]));
        } else {
            server = new ChatServer(1500);
        }
        server.start();
    }


    /**
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     *
     * @author your name and section
     * @version date
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;


        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sOutput.flush();
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                //while loop here... DO HERE
                while (clients.contains(username)) {
                    sOutput.writeObject("This username is taken. Please enter another username.");
                    sOutput.flush();
                    this.username = (String) sInput.readObject();
                }
                sOutput.writeObject("Welcome to the Chat Server!\n");
                sOutput.flush();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private boolean writeMessage(String message) {
            if (!socket.isConnected()) {
                return false;
            }
            try {
                //sOutput = new ObjectOutputStream(socket.getOutputStream());

                sOutput.writeObject(message);
                sOutput.flush();

            } catch(IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        private void close() {
            try {
                sOutput.close();
                sInput.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject(); //error handling or close
                    if (cm.getType() == 1) {
                        broadcast(this.username + "has logged out"); //only use broadcast to say that person has logged out.
                        this.close();
                        break;
                    } else if(cm.getType() == 2){
                        directMessage(cm.getMessage(),this.username);

                    } else {
                        //sOutput.writeObject(this.username + ": " + cm.getMessage());
                        broadcast(this.username + ": " + cm.getMessage());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(username + ": "+ cm.getMessage());
            }



            // Send message back to the client
            /*
            try {
                sOutput.writeObject("Pong");
            } catch (IOException e) {
                e.printStackTrace();
            }
             */
        }
    }
}
