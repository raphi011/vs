package chatserver.protocol;

import connection.Protocol;

public interface IProtocolFactory {

    Protocol newProtocol();
}
