import java.net.DatagramPacket;
import java.net.InetAddress;

public class Lamport {
    private Process myProcess;
    private int numberOfMyProcess;
    private int localTimeStamp = 0;
    private int counter = 0;

    ReadConfigFile readFile = new ReadConfigFile();

    public Lamport(int numberOfMyProcess) {
        this.numberOfMyProcess = numberOfMyProcess;
    }

    public void getMyProcess(int numberOfMyProcess) {
        myProcess = readFile.getIndexListOfProcess(numberOfMyProcess);
    }

    public void createEvent() throws InterruptedException {
        while (counter < 100) {
            int random = (int) (Math.random() * (1000 - 500 + 1) + 500);
            Thread.sleep(random);
            chooseTypeOfEvent();
            counter++;
        } 
        
    }

    public void chooseTypeOfEvent () {
        int random = 1 + (int)(Math.random() * ((100 - 1) + 1));
        double chance = myProcess.chance * 100;
        if (random > chance) {
            localEvent();
        } else {
            try{
                sendEvent();
            } catch(Exception e) {

            }
        }

    }
    public void localEvent() {
        localTimeStamp +=1;
        System.out.println("Evento local");

    }

    public void sendEvent() throws Exception {
        byte[] sendMessageAndLocalClock = new byte [1024];
        String messageAndClock = "Olá:" + localTimeStamp;
        sendMessageAndLocalClock = messageAndClock.getBytes();
        Process otherProcess = readFile.getRandomProcess(numberOfMyProcess);
        InetAddress host = InetAddress.getByName(otherProcess.host);
        DatagramPacket sendData = new DatagramPacket(sendMessageAndLocalClock, 
        sendMessageAndLocalClock.length, host, otherProcess.port);


    }

     //Falta: receber mensagem, atualizar o relógio com o recebimento e testar.
    
    
}
