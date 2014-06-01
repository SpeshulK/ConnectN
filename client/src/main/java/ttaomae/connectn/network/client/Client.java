package ttaomae.connectn.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ttaomae.connectn.Board;
import ttaomae.connectn.Piece;
import ttaomae.connectn.Player;
import ttaomae.connectn.network.ConnectNProtocol;

// TODO: add constructor with board parameters
public class Client implements Runnable
{
    private Socket socket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;

    private Player player;
    /** This client's copy of the board */
    private Board board;

    private List<ClientListener> listeners;

    /**
     * Indicates whether or not this Client is waiting for some kind of
     * response. For example, it may be waiting to confirm whether or not to
     * accept a rematch.
     */
    private volatile boolean waitingForResponse;

    public Client(String hostname, int portNumber, Player player, Board board)
            throws UnknownHostException, IOException
    {
        this.socket = new Socket(hostname, portNumber);
        this.socketOut = new PrintWriter(socket.getOutputStream(), true);
        this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.player = player;
        this.board = board;

        this.listeners = new ArrayList<>();
    }

    public void disconnect()
    {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run()
    {
        while (true) {
            try {
                String message = this.getMessageFromServer();

                // start a new game
                if (message.equals(ConnectNProtocol.START)) {
                    // make sure the board is empty by undoing everything
                    // TODO: kind of hack-y. must ensure that the panel and client have the same board
                    while (this.board.undoPlay());

                    System.out.println("CLIENT: Starting game");
                    playGame();
                }

                // confirm a rematch
                else if (message.equals(ConnectNProtocol.REMATCH)) {
                    getRematch();
                }

                // server is pinging so verify connection
                else if (message.equals(ConnectNProtocol.PING)) {
                    sendMessageToServer(ConnectNProtocol.PING);
                }

                // not what we expected so start over and get a new message
                else {
                    continue;
                }
            } catch (IOException e) {
                System.err.println("Error reading from socket.");
                break;
            }
        }
        System.out.println("CLIENT: Done!");
    }

    private void playGame() throws IOException
    {
        while (this.board.getWinner() == Piece.NONE) {
            // get message from server
            String message = this.getMessageFromServer();

            // server is waiting for move
            if (message.equals(ConnectNProtocol.READY)) {
                // get a move and play it
                int move = -1;
                while (!this.board.isValidMove(move)) {
                    move = player.getMove(this.board.copy());
                }
                this.board.play(move);

                this.sendMessageToServer(ConnectNProtocol.constructMove(move));
            }
            // server sent opponent move
            else if (ConnectNProtocol.verifyMove(message)) {
                // play opponent's move
                this.board.play(ConnectNProtocol.parseMove(message));
            }
            // server is pinging so verify connection
            else if (message.equals(ConnectNProtocol.PING)) {
                this.sendMessageToServer(ConnectNProtocol.PING);
            }
            // opponent has disconnected; end game
            else if (message.equals(ConnectNProtocol.DICONNECTED)) {
                break;
            }
        }

        System.out.println(this.board.getWinner() + " wins!");
    }

    private void getRematch() throws IOException
    {
        this.waitingForResponse = true;
        try {
            while (this.waitingForResponse) {
                synchronized (this) {
                    this.wait();
                }
            }
        } catch (InterruptedException e) {
            this.sendMessageToServer(ConnectNProtocol.NO);
        }
    }

    public void confirmRematch()
    {
        this.sendMessageToServer(ConnectNProtocol.YES);
        this.waitingForResponse = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void denyRematch()
    {
        this.sendMessageToServer(ConnectNProtocol.NO);
        this.waitingForResponse = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    private String getMessageFromServer() throws IOException
    {
        String result = this.socketIn.readLine();
        this.notifyListeners(true, result);

        return result;
    }

    private void sendMessageToServer(String message)
    {
        this.socketOut.println(message);
        this.notifyListeners(false, message);
    }

    public void addListener(ClientListener cl)
    {
        this.listeners.add(cl);
    }

    private void notifyListeners(boolean received, String message)
    {
        for (ClientListener cl : this.listeners) {
            if (cl != null) {
                if (received) {
                    cl.clientReceivedMessage(message);
                }
                else {
                    cl.clientSentMessage(message);
                }
            }
        }
    }
}