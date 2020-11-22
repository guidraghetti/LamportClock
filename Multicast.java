import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class Multicast {

    private static Boolean creator = false;
    private static Boolean receiver = false;
    private static int porta;
    private static InetAddress grupo;
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
        System.out.println("Estou pronto, vou esperar os outros");
        Scanner in = new Scanner(System.in);
        String verifyMessage = in.nextLine();
        System.out.println("Digite 'verify' após inicializar todos os processos para a inicialização multicast");
        try {
            int i = 0;
            if (verifyMessage.equalsIgnoreCase("verify")) {
                byte[] verify = new byte[1024];
                verify = verifyMessage.getBytes();
                DatagramPacket sendVerify = new DatagramPacket(verify, verify.length, grupo, porta);
                socket.send(sendVerify);
            }
            while (i < configFile.getNOfProcess() - 1) {
                byte[] receivedOk = new byte[1024];
                DatagramPacket processReady = new DatagramPacket(receivedOk, receivedOk.length);
                try {
                    socket.receive(processReady);
                    i++;
                } catch (Exception e) {
                    condition = false;
                    break;
                }
                Thread.sleep(2000);
            }
            byte[] startLocalsClock = new byte[1024];
            startLocalsClock = "start".getBytes();
            DatagramPacket sendStartCommand = new DatagramPacket(startLocalsClock, startLocalsClock.length, grupo,
                    porta);
            socket.send(sendStartCommand);
            condition = false;

        } catch (Exception e) {

        }
    }

    public static void receiver(MulticastSocket socket) throws InterruptedException {
        ReadConfigFile configFile = new ReadConfigFile();
        configFile.readConfigAddToList();
        try {
            if (receiver) {
                System.out.println("Estou pronto");
                byte[] sendOk = new byte[1024];
                sendOk = "ready".getBytes();
                DatagramPacket sendReady = new DatagramPacket(sendOk, sendOk.length, grupo, porta);
                socket.send(sendReady);
                receiver = false;
            }

            byte[] response = new byte[1024];
            DatagramPacket res = new DatagramPacket(response, response.length);
            socket.receive(res);

            String responseData = new String(res.getData(), 0, res.getLength());

            if (responseData.equals("verify")) {
                receiver = true;
            }

            if (responseData.equals("start")) {
                System.out.println("Que comecem os jogos");
            }

        } catch (Exception e) {

        }

    }

}
