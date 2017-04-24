package cases;

import java.io.IOException;

import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.Socket;

/**
 * Created by zhengfan on 2017/4/1 0001.
 */
public class SocketTest {


    public void test() throws IOException {
        Socket socket = Socket.newSocketDomain();
    }
}
