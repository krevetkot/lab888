package com.labs.secondsemester.client;

import com.labs.secondsemester.common.commands.Command;
import com.labs.secondsemester.common.commands.ExecuteFile;
import com.labs.secondsemester.common.commands.Exit;
import com.labs.secondsemester.common.exceptions.FailedBuildingException;
import com.labs.secondsemester.common.exceptions.IllegalValueException;
import com.labs.secondsemester.common.managers.Console;
import com.labs.secondsemester.common.managers.ScriptManager;
import com.labs.secondsemester.common.network.ClientIdentification;
import com.labs.secondsemester.common.network.Response;
import com.labs.secondsemester.common.objects.Dragon;
import com.labs.secondsemester.common.objects.forms.DragonForm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class FileManager {
    private final Client client;

    public FileManager(Client client){
        this.client = client;
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
                            Console.print(e.getMessage(), false);
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
                        for (String element: response.getResponse()){
                            System.out.println(element);
                        }
                    }


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }

            ScriptManager.getBufferedReaders().removeLast();
            ScriptManager.getPathQueue().removeLast();
            br.close();

            System.out.println("------ Выполнение файла " + argument + " завершено ------");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }
}
