package ru.geekbrains.oskin_di.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.geekbrains.oskin_di.command.Command;
import ru.geekbrains.oskin_di.core.handler.CommandInboundHandler;
import ru.geekbrains.oskin_di.service.Callback;
import ru.geekbrains.oskin_di.util.Config;


public class NettyClient {

    private SocketChannel channel;

    private static NettyClient instance;

    public static NettyClient getInstance() {
        if (instance == null) {
            instance = new NettyClient();
        }
        return instance;
    }

    public void startClient() {
        Thread thread = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new CommandInboundHandler()
                                );
                            }
                        });


                ChannelFuture future = bootstrap.connect(Config.getAddress(), Config.getPort()).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        thread.start();
    }

    public void stopClient() {
        try {
            if (channel.isActive()) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(Command command, Callback callback) {
        channel.pipeline().get(CommandInboundHandler.class).setResultCommand(callback);
        channel.writeAndFlush(command);
    }

}