1、Netty传输过程中会发生TCP粘包、拆包现象,一帧数据如果太长，传输过程中会拆成两包，太短的话可能会粘到下一包上传输。
    所以netty收到的TCP是没有任何规律的需要自己判断。当一端源源不断的收到报文的时候，就需要一个方法来确定到哪儿是完整的一帧数据。
    new DelimiterBasedFrameDecoder(20,false,delimiter);
    这个解码器就是分隔符解码器，自己定义一帧数据的结尾分隔符。
    第一个参数：是限制一帧数据最大长度如果超过这个长度就会报错。
    第二个参数：是否stripDelimiter，分段接受的时候是否把分割符去掉（默认去掉）
    第三个参数：就是分割符了，需要转成ByteBuf

2、netty 的channelHandler执行顺序：
    都是channelInboundHandler,都是addLast,执行顺序是从上到下的顺序，通过fireChannelRead可以向下一个handler传递。
    channelOutboundHandler不懂，只知道向外输出时，如果用了ctx.channel().writeAndFlush();则会从管道底部向上传递。
    如果直接ctx.writeAndFlush(),则从管道当前位置向上传递。

3、