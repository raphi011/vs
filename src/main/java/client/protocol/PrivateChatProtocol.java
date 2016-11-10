package client.protocol;

import connection.Protocol;

import java.io.PrintStream;

public class PrivateChatProtocol extends Protocol {
    private final String username;
    private final PrintStream out;

    public PrivateChatProtocol(String username, PrintStream out) {
        super("\\|");
        this.username = username;
        this.out = out;
    }

    @Override
    protected boolean isCommand(String input) {
        return false;
    }

    @Override
    protected String selectCommand(String command, String input) {
        String[] params = splitParams(input);


        out.println(String.format("%s: %s", params[0], params[1]));

        return "!ack";
    }
}