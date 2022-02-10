package com.acezhhh.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author acezhhh
 * @date 2022/1/24
 */
@Component
public class NettyServer {
    /**
     * boss 线程组用于处理连接工作
     */
    private EventLoopGroup acceptor = new NioEventLoopGroup();

    /**
     * work 线程组用于数据处理
     */
    private EventLoopGroup worker = new NioEventLoopGroup();
    @Value("${webSocket.port:0}")
    private Integer webSocketPort;

    @Value("${socket.port:0}")
    private Integer socketPort;

    private EventLoopGroup socketAcceptor = new NioEventLoopGroup();

    private EventLoopGroup socketWorker = new NioEventLoopGroup();

    @Autowired
    private NettyHandler nettyHandler;

    @Autowired
    private SimpleServerHandler simpleServerHandler;

    /**
     * 启动Netty Server
     *
     * @throws InterruptedException
     */
    public void start() {
        try {
            //1、创建启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //2、配置启动参数等
            /**设置循环线程组，前者用于处理客户端连接事件，后者用于处理网络IO(server使用两个参数这个)
             *public ServerBootstrap group(EventLoopGroup group)
             *public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup)
             */
            bootstrap.group(acceptor, worker);
            /**设置选项
             * 参数：Socket的标准参数（key，value），可自行百度
             * eg:
             * bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
             *bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
             * */
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 设置发送缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024); // 这是接收缓冲大小

            //用于构造socketchannel工厂
            bootstrap.channel(NioServerSocketChannel.class);
            /**
             * 传入自定义客户端Handle
             */
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new HttpServerCodec());
                    socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                    socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                    socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                    socketChannel.pipeline().addLast(nettyHandler);
                }
            });

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = bootstrap.bind(webSocketPort).sync();

            // 等待服务器 socket 关闭 。
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptor.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 启动后端socket连接
     */
    public void startSocket(){
        //1、创建启动类
        ServerBootstrap socket = new ServerBootstrap();

        try {
            socket.group(socketAcceptor, socketWorker);
            socket.option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 设置发送缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024); // 这是接收缓冲大小
            socket.channel(NioServerSocketChannel.class);
            socket.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //设置特殊分隔符
                    ByteBuf buf=Unpooled.copiedBuffer("$_".getBytes());
                    socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));

                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new StringEncoder());
                    socketChannel.pipeline().addLast(simpleServerHandler);
                }
            });
            ChannelFuture f = socket.bind(socketPort).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socketAcceptor.shutdownGracefully();
            socketWorker.shutdownGracefully();
        }
    }

    public void destory() throws InterruptedException {
        acceptor.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();

        socketAcceptor.shutdownGracefully().sync();
        socketWorker.shutdownGracefully().sync();
    }
}
