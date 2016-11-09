package connection;

import channel.IChannel;

public abstract class Protocol {

    protected final String argsDelimiter;

    protected IChannel channel;

    public Protocol(String argsDelimiter) {
        this.argsDelimiter = argsDelimiter;
    }

    protected abstract boolean isCommand(String input);

    protected abstract String selectCommand(String command, String params);

    public String nextCommand(String input) {
        if (isCommand(input)) {
            String[] commandParts = input.split(argsDelimiter, 2);
            String command = commandParts[0];
            String params = commandParts.length == 2 ? commandParts[1] : "";
            return selectCommand(command, params);
        } else {
            return selectCommand("", input);
        }

    }

    public void setChannel(IChannel channel) {
        this.channel = channel;
    }
}
