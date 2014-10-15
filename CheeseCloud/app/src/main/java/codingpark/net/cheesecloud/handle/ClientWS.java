package codingpark.net.cheesecloud.handle;

/**
 * Created by ethanshan on 14-10-15.
 * The class singleton pattern, used to support Web Service
 * function interface
 */
public final class ClientWS {
    private static ClientWS client    = null;

    private ClientWS() {
    }

    public static ClientWS getInstance() {
        if (client == null)
            client = new ClientWS();
        return client;
    }

}
