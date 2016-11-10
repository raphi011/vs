package connection;

public class ReadProtocolFactory implements IProtocolFactory {

    @Override
    public Protocol newProtocol() {
        return new ReadProtocol();
    }
}
