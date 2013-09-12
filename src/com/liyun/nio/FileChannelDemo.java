package com.liyun.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.liyun.dataMinning.UserBaseCF;

public class FileChannelDemo {
	public static void readFile() {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(new File(UserBaseCF.class.getClassLoader().getResource("dataset").getFile()),
					"r");
			FileChannel inChannel = raf.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(1024);
			int bytesRead;
			while ((bytesRead = inChannel.read(buf)) != -1) {
				buf.flip();
				while (buf.hasRemaining()) {
					System.out.print((char) buf.get());
				}
				buf.clear();
			}
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void channelMethodDemo() {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		System.out.println(buf.capacity());
		System.out.println(buf.limit());
		System.out.println(buf.position());
		buf.put(new Byte("12"));
		System.out.println("----------------------");
		System.out.println(buf.capacity());
		System.out.println(buf.limit());
		System.out.println(buf.position());
//		System.out.println((char)buf.get());
		
//		buf.rewind();
//		System.out.println("---------rewind-------------");
//		System.out.println(buf.capacity());
//		System.out.println(buf.limit());
//		System.out.println(buf.position());
		
//		buf.flip();
//		System.out.println("----------flip------------");
//		System.out.println(buf.capacity());
//		System.out.println(buf.limit());
//		System.out.println(buf.position());
		
		buf.compact();
		System.out.println("----------compact------------");
		System.out.println(buf.capacity());
		System.out.println(buf.limit());
		System.out.println(buf.position());
		buf.put((byte)12);
		System.out.println(buf.capacity());
		System.out.println(buf.limit());
		System.out.println(buf.position());
		
		buf.put((byte)12);
		System.out.println(buf.capacity());
		System.out.println(buf.limit());
		System.out.println(buf.position());

	}
	
	public static void selectorDemo(){
		try {
			Selector selector = Selector.open();
			SocketChannel sc = SocketChannel.open(new InetSocketAddress(80));
			sc.configureBlocking(false);
			SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) throws Exception {
		channelMethodDemo();
	}
}
