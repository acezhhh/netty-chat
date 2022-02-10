package com.acezhhh.server.channel;

import com.acezhhh.common.vo.UserVo;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;

/**
 * @author acezhhh
 * @date 2022/1/26
 */
public class UserChannel implements Channel {

    private UserVo userVo;
    private Channel channel;

    public UserChannel() {
    }

    public UserVo getUserVo() {
        return userVo;
    }

    public void setUserVo(UserVo userVo) {
        this.userVo = userVo;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public ChannelId id() {
        return this.channel.id();
    }

    @Override
    public EventLoop eventLoop() {
        return this.channel.eventLoop();
    }

    @Override
    public Channel parent() {
        return this.channel.parent();
    }

    @Override
    public ChannelConfig config() {
        return this.channel.config();
    }

    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return this.channel.isRegistered();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public ChannelMetadata metadata() {
        return null;
    }

    @Override
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public ChannelFuture closeFuture() {
        return this.channel.closeFuture();
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public long bytesBeforeUnwritable() {
        return this.channel.bytesBeforeUnwritable();
    }

    @Override
    public long bytesBeforeWritable() {
        return this.channel.bytesBeforeWritable();
    }

    @Override
    public Unsafe unsafe() {
        return this.channel.unsafe();
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.channel.pipeline();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.channel.alloc();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.channel.bind(localAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.channel.connect(remoteAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.channel.connect(remoteAddress, localAddress);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.channel.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.channel.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.channel.deregister();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.channel.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.channel.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.channel.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.channel.disconnect(promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return this.channel.close();
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return this.channel.deregister(promise);
    }

    @Override
    public Channel read() {
        return this.channel.read();
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.channel.write(msg);
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.channel.write(msg, promise);
    }

    @Override
    public Channel flush() {
        return this.channel.flush();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.channel.write(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.channel.writeAndFlush(msg);
    }

    @Override
    public ChannelPromise newPromise() {
        return this.channel.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.channel.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.channel.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return this.channel.newFailedFuture(cause);
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.channel.voidPromise();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.channel.attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.channel.hasAttr(key);
    }

    @Override
    public int compareTo(Channel o) {
        return this.channel.compareTo(o);
    }
}