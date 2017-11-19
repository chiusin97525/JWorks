package command;

import action.LoginAction;
import databaseAPI.DatabaseAPI;
import io.OutputGen;
import models.Student;
import models.User;

public class LoginCommand extends Command {

    public LoginCommand(DatabaseAPI api, OutputGen outputStream) {
        super(api, outputStream);
    }

    /**
     * Command to log into the application. Passes the user to 
     * @param args the arguments for the command to use. First argument is the username, the second argument is the
     *             password.
     * @return whether or not authentication succeeded.
     */
    @Override
    public boolean execute(String[] args) {
        // Parse arguments
        if (args.length != 2) {
            return false;
        }
        
        String user = args[0];
        String password = args[1];
        LoginAction action = new LoginAction();

        // Call authentication action
        Object result = action.execute(user, password, this.databaseAPI);
        
        // Parse result
        if (result instanceof User) {
        	outputStream.outputPayload(result);
        	return true;
        }
        
        // Only reached if authentication fails
        return false;
    }
}
