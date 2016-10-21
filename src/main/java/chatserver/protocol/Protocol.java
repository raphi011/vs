package chatserver.protocol;

public abstract class Protocol {

    protected abstract String selectCommand(String command, String input);

    public String nextCommand(String input) {
        int commandEndIndex = input.indexOf(' ');
        String command = input.substring(0, commandEndIndex);
        String args = input.substring(commandEndIndex);

        return selectCommand(command, args);
    }
}
