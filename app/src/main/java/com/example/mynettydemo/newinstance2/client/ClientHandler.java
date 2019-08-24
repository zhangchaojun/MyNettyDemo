package com.example.mynettydemo.newinstance2.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private byte[] req;

    private int counter;

    public ClientHandler() {
        req = ( "你1Unless required by applicable law or agreed to in writing, software\t我" +
                "2distributed under the License is distributed on an \"AS IS\" BASIS,\t我" +
                "3WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\t我" +
                "4See the License for the specific language governing permissions and\t我" +
                "5limitations under the License.This connector uses the BIO implementation that requires the JSSE\t我" +
                "6style configuration. When using the APR/native implementation, the\t我" +
                "7penSSL style configuration is required as described in the APR/native\t我" +
                "8documentation.An Engine represents the entry point (within Catalina) that processes\t我" +
                "9every request.  The Engine implementation for Tomcat stand alone\t我" +
                "10analyzes the HTTP headers included with the request, and passes them\t我" +
                "11on to the appropriate Host (virtual host)# Unless required by applicable law or agreed to in writing, software\t我" +
                "12distributed under the License is distributed on an \"AS IS\" BASIS,\t我" +
                "13WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\t我" +
                "14See the License for the specific language governing permissions and\t我" +
                "15limitations under the License.# For example, set the org.apache.catalina.util.LifecycleBase logger to log\t我" +
                "16each component that extends LifecycleBase changing state:\t我" +
                "你17org.apache.catalina.util.LifecycleBase.level = FINE\t我"
        ).getBytes();

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ByteBuf message = Unpooled.buffer(req.length);
        message.writeBytes(req);

        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String buf = (String) msg;
        System.out.println("client receive message is : " + buf + "    the counter is : " + (++counter));
        if(counter == 17){
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception Caught :"+cause.getMessage());
        ctx.close();
    }
}
