package labs.secondSemester.client;

import labs.secondSemester.commons.commands.*;
import labs.secondSemester.commons.exceptions.AccessDeniedException;
import labs.secondSemester.commons.exceptions.FailedBuildingException;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.managers.Console;
import labs.secondSemester.commons.network.*;
import labs.secondSemester.commons.objects.*;
import labs.secondSemester.commons.objects.forms.DragonForm;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client {
    private final DatagramChannel datagramChannel;
    private InetSocketAddress serverAddress;
    private Selector selector;
    private final Serializer serializer;
    private final FileManager fileManager;
    private final String ip;
    private final int BUFFER_LENGTH = 10000;
    @Setter
    @Getter
    private ClientIdentification clientID;


    public Client(String ip) throws IOException {
        fileManager = new FileManager(this);
        this.ip = ip;
        clientID = new ClientIdentification("", "");
        selector = Selector.open();
        datagramChannel = DatagramChannel.open();
        serializer = new Serializer();
        selector = Selector.open();
    }


    public void start() {

        connectServer(1);
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Подключение к серверу налажено.");
        String request = null;
        CommandFactory commandFactory = new CommandFactory(clientID);

        System.out.println("Приветствуем Вас в приложении по управлению коллекцией! Введите 'help' для вывода доступных команд.");
        System.out.println("Необходимо зарегистрироваться или выполнить вход в аккаунт. Это можно сделать командами sign_up и login.");

        while (true) {
            try {
                request = scanner.nextLine();
            } catch (NoSuchElementException e1) {
                System.out.println("До свидания! Приходите еще :)");
                System.exit(0);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            try {
                Command command = commandFactory.buildCommand(request);
                if (!clientID.isAuthorized()){
                    if (command instanceof SignUp){
                        clientID = askLoginPassword(scanner);
                        command.setClientID(clientID);
                    }
                    else if (command instanceof Login){
                        clientID = askLoginPassword(scanner);
                        command.setClientID(clientID);
                    } else {
                        if (command instanceof Exit){
                            command.execute(null, false, null, null);
                        } else {
                            System.out.println("Необходимо зарегистрироваться или выполнить вход в аккаунт. Это можно сделать командами sign_up и login.");
                            continue;
                        }
                    }
                }
                if (command instanceof Add || command instanceof InsertAt || command instanceof Update) {
                    DragonForm newDragon = new DragonForm();
                    try {
                        Dragon buildedDragon = newDragon.build(scanner, false);
                        buildedDragon.setOwner(clientID.getLogin());
                        command.setObjectArgument(buildedDragon);
                        command.setClientID(clientID);
                    } catch (FailedBuildingException | IllegalValueException e) {
                        Console.print(e.getMessage(), false);
                    }
                }
                if (command instanceof Exit) {
                    command.execute(null, false, null, null);
                }
                if (command instanceof ExecuteFile) {
                    assert request != null;
                    fileManager.executeFile(request.trim().split(" ")[1], clientID);
                    continue;
                } else {
                    command.setClientID(clientID);
                    send(command);
                }

            } catch (IllegalValueException | ArrayIndexOutOfBoundsException | NumberFormatException | SQLException | AccessDeniedException e) {
                System.out.println(e.getMessage());
                continue;
            }

            try {
                Response response = receive(buffer);
                for (String element : response.getResponse()) {
                    if (element.equals("Выполнен вход в аккаунт.")){
                        System.out.println(element);
                        clientID.setAuthorized(true);
                    }
                    else {
                        System.out.println(element);
                    }
                }
            } catch (Exception e){
                System.out.println("Проблемы с ответом от сервера.");
            }
        }
    }

    public boolean handleLoginResponse(){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        Response response = receive(buffer);
        for (String element : response.getResponse()) {
            if (element.equals("Выполнен вход в аккаунт.")){
                clientID.setAuthorized(true);
                return true;
            }
        }
        return false;
    }

    public Response handleResponse(){
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        return receive(buffer);
    }

    public ClientIdentification askLoginPassword(Scanner scanner){
        System.out.print("Введите имя пользователя: ");
        String login;
        while (true) {
            login = scanner.nextLine().trim();
            Pattern pattern = Pattern.compile("\\w+[^а-я]+\\n*$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(login);
            if (!matcher.find()){
                System.out.println("Логин должен быть непустым и состоять только из латинских букв, цифр и специальных знаков. Попробуйте еще раз.");
            } else break;
        }
        System.out.print("Введите пароль: ");

        String password;
        java.io.Console console = System.console();
        while (true) {
            if (console != null){
                char[] symbols = console.readPassword();
                password = String.valueOf(symbols);
            } else {
                password = scanner.nextLine();
            }
            Pattern pattern = Pattern.compile("\\w+[^а-я]+\\n*$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(password);
            if (!matcher.find()){
                System.out.println("Пароль должен быть непустым и состоять только из латинских букв, цифр и специальных знаков. Попробуйте еще раз.");
            } else break;
        }
        return new ClientIdentification(login, encryptStringSHA512(password));
    }

    public String encryptStringSHA512(String string){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(string.getBytes(StandardCharsets.UTF_8));
            BigInteger numRepresentation = new BigInteger(1, digest);
            StringBuilder hashedString = new StringBuilder(numRepresentation.toString(16));
            while (hashedString.length()<32){
                hashedString.insert(0, "0");
            }
            return hashedString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Dragon> getCollectionFromServer(){
        CommandFactory commandFactory = new CommandFactory(clientID);
        try {
            send(commandFactory.buildCommand("technical_show"));
        } catch (IllegalValueException e) {
            System.out.println(e.getMessage());
        }
        Response response = handleResponse();
        return parseTechShow(response.getResponse());
    }

    private ArrayList<Dragon> parseTechShow(ArrayList<String> dragons){
        ArrayList<Dragon> dragons1 = new ArrayList<>();

        for (String elem: dragons){
            String[] temp = elem.trim().split(",");
            if (temp[10].equals("null")){
                Dragon dragon = new Dragon(temp[2], new Coordinates(java.lang.Long.parseLong(temp[3]), java.lang.Float.parseFloat(temp[4]))
                        , java.lang.Long.parseLong(temp[6]), java.lang.Long.valueOf(temp[7]), Boolean.parseBoolean(temp[8]),
                        labs.secondSemester.commons.objects.DragonType.valueOf(temp[9]),
                        null, temp[0]);
                dragon.setId(Integer.parseInt(temp[1]));
                dragons1.add(dragon);
            }
            else {
                Person person = new Person(temp[10], temp[11], labs.secondSemester.commons.objects.Color.valueOf(temp[12]),
                        labs.secondSemester.commons.objects.Color.valueOf(temp[13]),
                        labs.secondSemester.commons.objects.Country.valueOf(temp[14]),
                        java.lang.Long.parseLong(temp[15]));
                Dragon dragon = new Dragon(temp[2], new Coordinates(java.lang.Long.parseLong(temp[3]), java.lang.Float.parseFloat(temp[4]))
                        , java.lang.Long.parseLong(temp[6]), java.lang.Long.valueOf(temp[7]), Boolean.parseBoolean(temp[8]),
                        labs.secondSemester.commons.objects.DragonType.valueOf(temp[9]),
                        person, temp[0]);
                dragon.setId(Integer.parseInt(temp[1]));
                dragons1.add(dragon);
            }
        }
        return dragons1;

    }



    public void send(Command command) {
        try {
            Header header = new Header(0, 0);
            int headerLength = serializer.serialize(header).length + 200;

            byte[] buffer = serializer.serialize(command);
            int bufferLength = buffer.length;
            int countOfPieces = bufferLength / (BUFFER_LENGTH - headerLength);
            if (countOfPieces * (BUFFER_LENGTH - headerLength) < bufferLength) {
                countOfPieces += 1;
            }
            for (int i = 0; i < countOfPieces; i++) {
                header = new Header(countOfPieces, i);
                headerLength = serializer.serialize(header).length + 200;
                Packet packet = new Packet(header, Arrays.copyOfRange(buffer, i * (BUFFER_LENGTH - headerLength), Math.min(bufferLength, (i + 1) * (BUFFER_LENGTH - headerLength))));
                datagramChannel.send(ByteBuffer.wrap(serializer.serialize(packet)), serverAddress);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public Response receive(ByteBuffer buffer) {
        buffer.clear();
        try {
            SocketAddress address = null;
            int time = 1;
            int tries = 1;
            int period = 100000;
            while (!serverAddress.equals(address)) {
                if (time % period == 0) {
                    connectServer(tries);
                    tries += 1;
                }
                buffer.clear();
                address = datagramChannel.receive(buffer);
                time += 1;
            }
            Packet packet = serializer.deserialize(buffer.array());
            Header header = packet.getHeader();
            int countOfPieces = header.getCount();
            ArrayList<Packet> list = new ArrayList<>(3);
            list.add(header.getNumber(), packet);
            list.add(1, null);
            int k = 1;

            while (k < countOfPieces) {
                buffer.clear();
                if (selector.select() < 0) continue;

                datagramChannel.receive(buffer);
                Packet newPacket = serializer.deserialize(buffer.array());
                Header newHeader = newPacket.getHeader();
                list.add(newHeader.getNumber(), newPacket);
                k += 1;
            }

            int buffLength = 0;
            for (int i = 0; i < countOfPieces; i++) {
                buffLength += list.get(i).getPieceOfBuffer().length;
            }
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(buffLength)) {
                for (int i = 0; i < countOfPieces; i++) {
                    byteStream.write(list.get(i).getPieceOfBuffer());
                }
                return serializer.deserialize(byteStream.toByteArray());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void connectServer(int connectionTries) {
        try {
            serverAddress = new InetSocketAddress(ip, 2224);
            datagramChannel.configureBlocking(false);
            datagramChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("Попытка подключения к серверу: " + connectionTries);
            if (connectionTries > 2) {
                System.out.println("Кажется, барахлит подключение к серверу. Попробуйте позже.");
                System.exit(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
