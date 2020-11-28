import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.DatagramSocket;

public class Lamport {
    private Process myProcess;
    private int numberOfMyProcess;
    private int localTimeStamp = 0;
    private int counter = 0;

    ReadConfigFile readFile = new ReadConfigFile();    

    public Lamport(int numberOfMyProcess) {
        this.numberOfMyProcess = numberOfMyProcess;
        readFile.readConfigAddToList(); 
    }

    public void createEvent() throws InterruptedException {
        System.out.println("Chegou aqui");
        while (counter < 100) {
            int random = (int) (Math.random() * (1000 - 500 + 1) + 500);
            Thread.sleep(random);
            chooseTypeOfEvent();
            counter++;
        }

    }

    public void calcLamport(String[] data) {
        int receivedTimeStamp = Integer.parseInt(data[2]);
        int receivedId = Integer.parseInt(data[1]);
        localTimeStamp = Math.max(localTimeStamp, receivedTimeStamp);
        localTimeStamp += 1;
        String concatLocalTimeStamp = "" + localTimeStamp + myProcess.id;
        localTimeStamp = Integer.parseInt(concatLocalTimeStamp);
        System.out.println(System.currentTimeMillis() + "   " + myProcess.id + "  " + localTimeStamp + " " + receivedId
                + "   " + receivedTimeStamp);
    }

    public void listener() throws SocketException {
        DatagramSocket socket = new DatagramSocket(myProcess.port);
        DatagramPacket packet;
        byte[] data = new byte[1024];
        while (true) {
            try {
                packet = new DatagramPacket(data, data.length);
                socket.setSoTimeout(500);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                String[] msgAndClock = response.split(":");
                calcLamport(msgAndClock);
            } catch (Exception e) {
                socket.close();
                break;
            }
        }
    }

    public void chooseTypeOfEvent() {
        System.out.println("Chegou no chooseTypeOfEvent");
        int random = 1 + (int) (Math.random() * ((100 - 1) + 1));        
        double chance = myProcess.chance * 100;        
        if (random > chance) {
            localEvent();
        } else {
            try {
                sendEvent();
            } catch (Exception e) {

            }
        }

    }

    public void localEvent() {
        localTimeStamp += 1;
        String concatLocalTimeStamp = localTimeStamp + myProcess.id + "";
        System.out.println(System.currentTimeMillis() + " " + myProcess.id + "  " + concatLocalTimeStamp);
        return;

    }

    public void sendEvent() throws Exception {
        byte[] sendMessageAndLocalClock = new byte[1024];
        String messageAndClock = "Olá:" + myProcess.id + ":" + localTimeStamp;
        sendMessageAndLocalClock = messageAndClock.getBytes();
        Process otherProcess = readFile.getRandomProcess(numberOfMyProcess);
        InetAddress host = InetAddress.getByName(otherProcess.host);
        DatagramPacket sendData = new DatagramPacket(sendMessageAndLocalClock, sendMessageAndLocalClock.length, host,
                otherProcess.port);
        DatagramSocket socket = new DatagramSocket(myProcess.port);
        socket.send(sendData);
        socket.close();
        String concatLocalTimeStamp = localTimeStamp + myProcess.id + "";
        System.out.println(System.currentTimeMillis() + "    " + myProcess.id + "   " + concatLocalTimeStamp + "    "
                + otherProcess.id);
        return;

    }

    public void getMyProcess() {
        System.out.println("Entrou no getMyProcess");
        myProcess = readFile.getIndexListOfProcess(numberOfMyProcess);
        System.out.println("My PROCEES ID:  " + myProcess.id);
        try {
            //listener();
            createEvent();
        } catch (Exception e) {

        }
    }

    public void start() {
        System.out.println(
                "---------------------------------------------------------------------------------------------------");
        System.out.println("Entrou no try");
        this.getMyProcess();
        // this.createEvent();
    }

    // Falta: receber mensagem, atualizar o relógio com o recebimento e testar.

}
