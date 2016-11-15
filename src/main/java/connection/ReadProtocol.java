package connection;

import channel.IChannel;

public class ReadProtocol extends Protocol {

    public ReadProtocol(IChannel channel) {
        super("", channel);
    }

    @Override
    protected boolean isCommand(String input) {
        return false;
    }

    @Override
    protected String selectCommand(String command, String params) {
        return Read(params);
    }

    private String Read(String input) {
        return input;
    }
}
