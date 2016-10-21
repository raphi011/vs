package chatserver.protocol;

public abstract class Protocol {

    protected abstract String selectCommand(String command, String params);

    public String nextCommand(String input) {
        String[] commandParts = input.split(" ", 2);
        String command = commandParts[0];
        String params = commandParts.length == 2 ? commandParts[1] : "";

        return selectCommand(command, params);
    }
}
