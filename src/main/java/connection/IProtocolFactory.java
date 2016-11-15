package connection;

import channel.IChannel;

public interface IProtocolFactory {

    Protocol newProtocol(IChannel channel);
}
