//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package labs.secondSemester.client;

import labs.secondSemester.commons.commands.Command;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.managers.CommandManager;
import labs.secondSemester.commons.managers.Validator;
import labs.secondSemester.commons.network.ClientIdentification;
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
