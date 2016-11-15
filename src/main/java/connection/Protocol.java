package connection;

import channel.IChannel;

import java.io.IOException;

public abstract class Protocol {

    protected IChannel channel;

    public Protocol(String argsDelimiter, IChannel channel) {
        this.argsDelimiter = argsDelimiter;
        this.channel = channel;
    }

    protected final String argsDelimiter;

    public void close() throws IOException {
       channel.close();
    }

    public void onClosed() { }

    protected abstract boolean isCommand(String input);

    protected String[] splitParams(String input) {
        return input.split(argsDelimiter);
    }

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
}
