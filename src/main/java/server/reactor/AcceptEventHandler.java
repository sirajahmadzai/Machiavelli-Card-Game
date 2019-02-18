package server.reactor;

import commands.Command;
import server.ClientMessageSender;
import server.models.Machiavelli;
import server.models.Player;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEventHandler implements EventHandler {
    private final Selector demultiplexer;
    private Machiavelli machiavelli = Machiavelli.getInstance();

    public AcceptEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    private SelectionKey registerClient(SocketChannel clientSocket) throws IOException {
        clientSocket.configureBlocking(false);
        return clientSocket.register(demultiplexer, SelectionKey.OP_READ);
    }

    @Override
    public void handleEvent(SelectionKey event) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) event.channel();
            SocketChannel clientSocket = serverSocketChannel.accept();
            if(clientSocket == null){
                return;
            }
            SelectionKey key = registerClient(clientSocket);

            if (machiavelli.isTableFull()) {
                ClientMessageSender.sendCommand(clientSocket, Command.CommandNames.TABLE_IS_FULL);
            } else {
                Player player = machiavelli.addPlayer();
                key.attach(player);
                ClientMessageSender.getInstance().registerPlayer(player, clientSocket);

                machiavelli.introducePlayer(player);
                ClientMessageSender.sendCommand(clientSocket, Command.CommandNames.WHO_ARE_YOU);
                machiavelli.startGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

