package labs.secondSemester.client;

import javafx.scene.control.Alert;
import labs.secondSemester.client.controllers.MyAlert;
import labs.secondSemester.commons.commands.Command;
import labs.secondSemester.commons.commands.ExecuteFile;
import labs.secondSemester.commons.commands.Exit;
import labs.secondSemester.commons.exceptions.FailedBuildingException;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.managers.Console;
import labs.secondSemester.commons.managers.ScriptManager;
import labs.secondSemester.commons.network.ClientIdentification;
import labs.secondSemester.commons.network.Response;
import labs.secondSemester.commons.objects.Dragon;
import labs.secondSemester.commons.objects.forms.DragonForm;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    private final Client client;
    @Getter
    private final ArrayList<Response> responses;

    public FileManager(Client client){
        this.client = client;
        responses = new ArrayList<>();
    }

    public void executeFile(String argument, ClientIdentification clientID) {
        try {
            if (!(new File(argument).isFile())) {
                throw new IOException("Невозможно прочесть файл.");
            }
            ScriptManager.addFile(argument);
            BufferedReader br = ScriptManager.getBufferedReaders().getLast();
            Scanner fileScanner = new Scanner(br);
            String line;

            Console.print("------ Выполняется файл " + argument + " ------", false);
            while (!(line = ScriptManager.nextLine(fileScanner)).isBlank()) {
                String[] command = line.split(" ");
                if (command[0].equals("execute_file")) {
                    if (ScriptManager.isRecursive(command[1])) {
                        throw new RuntimeException("Найдена рекурсия! Повторно вызывается файл " + command[1]);
                    }
                }

                try {
                    CommandFactory commandFactory = new CommandFactory(clientID);

                    Command scriptCommand = commandFactory.buildCommand(line);
                    if (scriptCommand.getName().equals("add") || scriptCommand.getName().equals("insert_at") || scriptCommand.getName().equals("update")) {
                        DragonForm newDragon = new DragonForm();
                        try {
                            Dragon buildedDragon = newDragon.build(fileScanner, true);
                            scriptCommand.setObjectArgument(buildedDragon);
                        } catch (FailedBuildingException | IllegalValueException e) {
                            responses.add(new Response(e.getMessage()));
                        }
                    }

                    if (scriptCommand.getClass().equals(Exit.class)) {
                        scriptCommand.execute(null, false, null, null);
                    }
                    if (scriptCommand.getClass().equals(ExecuteFile.class)){
                        executeFile(scriptCommand.getStringArgument(), null);
                    }
                    else {
                        client.send(scriptCommand);

                        Response response = client.receive(ByteBuffer.allocate(10000));
                        responses.add(response);
                    }


                } catch (Exception e) {
                    responses.add(new Response(e.getMessage()));
                    break;
                }
            }

            ScriptManager.getBufferedReaders().removeLast();
            ScriptManager.getPathQueue().removeLast();
            br.close();

            responses.add(new Response("------ Выполнение файла " + argument + " завершено ------"));

        } catch (IOException e) {
            responses.add(new Response(e.getMessage()));
        }


    }
}
