//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.labs.secondsemester.client;

import com.labs.secondsemester.common.commands.Command;
import com.labs.secondsemester.common.exceptions.IllegalValueException;
import com.labs.secondsemester.common.managers.CommandManager;
import com.labs.secondsemester.common.managers.Validator;
import com.labs.secondsemester.common.network.ClientIdentification;
import lombok.Setter;

import java.util.Arrays;

@Setter
public class CommandFactory {
    private final CommandManager commandManager = new CommandManager();
    private ClientIdentification clientID;

    public CommandFactory(ClientIdentification clientID) {
        this.clientID = clientID;
    }

    public Command buildCommand(String request) throws IllegalValueException, ArrayIndexOutOfBoundsException, NumberFormatException {
        Validator validator = new Validator();
        request = request.trim();
        String commandName = request.split(" ")[0];
        String[] arguments;
        if (request.split(" ").length > 1) {
            arguments = Arrays.copyOfRange(request.split(" "), 1, request.split(" ").length);
        } else {
            arguments = new String[]{""};
        }

        Command command = this.commandManager.getCommandMap().get(commandName);

        validator.commandValidation(command, arguments);

        command.setStringArgument("");
        if (request.trim().split(" ").length > 1) {
            command.setStringArgument(request.trim().split(" ")[1]);
        }
        command.setClientID(clientID);

        return command;
    }
}
