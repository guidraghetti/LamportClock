import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Multicast {

    private static Boolean creator = false;
    private static Boolean receiver = false;
    private static int porta;
    private static InetAddress grupo;
    private static ArrayList<String> listOfReceiverVotes = new ArrayList<String>();
    private static Boolean condition = true;

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length < 1) {
            System.out.println("Você precisa indicar qual a linha do arquivo de configuração é o seu processo");
            System.exit('1');
        }
        System.out.println(args[0]);
        porta = 3001;
        MulticastSocket socket = new MulticastSocket(porta);
        socket.setSoTimeout(500);
        grupo = InetAddress.getByName("224.0.0.0");
        socket.joinGroup(grupo);
        String nick = args[1];

        if (nick.equalsIgnoreCase("server")) {
            creator = true;
        }

        while (condition) {
            if (creator)
                creator(socket);
            else
                receiver(socket);
        }

        socket.leaveGroup(grupo);
        socket.close();
    }

    public static void creator(MulticastSocket socket) throws InterruptedException {
        ReadConfigFile configFile = new ReadConfigFile();
        configFile.readConfigAddToList();
        try {
            Thread.sleep(20000);

            byte[] requestlistOfReceiverVotes = new byte[1024];
            requestlistOfReceiverVotes = "request".getBytes();
            DatagramPacket data = new DatagramPacket(requestlistOfReceiverVotes, requestlistOfReceiverVotes.length,
                    grupo, porta);
            socket.send(data);
            System.out.println(configFile.getNOfProcess()-1);
            for (int i = 0; i < configFile.getNOfProcess() - 1; i++) {
                byte[] received = new byte[1024];
                DatagramPacket dataVote = new DatagramPacket(received, received.length);

                try {
                    socket.receive(dataVote);
                } catch (Exception ex) {
                    System.out.println("TimedOut!");
                    condition = false;
                    break;
                }

                String vote = new String(dataVote.getData(), 0, dataVote.getLength());

                if (!vote.equalsIgnoreCase("request")) {
                    listOfReceiverVotes.add(vote);
                }

                Thread.sleep(2000);
            }

            byte[] commitMessage = new byte[1024];
            commitMessage = "global_commit".getBytes();
            DatagramPacket sendGlobalCommit = new DatagramPacket(commitMessage, commitMessage.length, grupo, porta);

            socket.send(sendGlobalCommit);
            condition = false;

        } catch (IOException e) {
        }
    }

    public static void receiver(MulticastSocket socket) throws InterruptedException {
        ReadConfigFile configFile = new ReadConfigFile();
        configFile.readConfigAddToList();
        try {
            if (receiver) {
                byte[] sendVote = new byte[1024];
                String generatedVote = "ready";
                System.out.println("SendVote");
                sendVote = generatedVote.getBytes();
                DatagramPacket data = new DatagramPacket(sendVote, sendVote.length, grupo, porta);

                socket.send(data);
                Thread.sleep(2000);
                receiver = false;
            }

            byte[] receiveData = new byte[1024];
            DatagramPacket data = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(data);

            String voterResponse = new String(data.getData(), 0, data.getLength());

            if (voterResponse.equalsIgnoreCase("request")) {
                receiver = true;
            }

            if (voterResponse.equalsIgnoreCase("global_commit")) {
                // Lamport lamport = new Lamport();
                System.out.println("Chegou na hora de inicializar");
                condition = false;
            }
        } catch (IOException e) {
        }

    }
}
