package connection;

import channel.IChannel;

public class ReadProtocolFactory implements IProtocolFactory {

    @Override
    public Protocol newProtocol(IChannel channel) {
        return new ReadProtocol(channel);
    }

    }
