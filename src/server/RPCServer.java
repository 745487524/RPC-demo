package server;

import api.ServiceName;
import api.UserServiceApi;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RPCServer {
    /**
     * 1、开放8010端口作为RPC调用端口
     * 2、判断客户端调用方法是什么类型
     * 3、反射生成新的实例
     * 4、返回server RPC调用结果
     */
    private ServerSocket serverSocket;

    // RPC服务地址，默认使用本地地址
    private static final String IP_ADDRESS = "localhost";

    // 开启的RPC服务端口号
    private static final int PORT = 8010;

    /**
     * 建立RPC服务处理
     * 1、开启服务端口ServerSocket -> 8010
     * 2、监听服务端口连接动作，监听到连接返回socket连接
     * 3、获取socket的输入和输出流
     * 4、获取输入流中客户端传入的请求信息
     * 5、调用相应类型的处理方法
     * 6、将调用结果写入socket输出流
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public void start() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("开放RPC调用端口8010");
        while (isValid()) {
            //获取客户端连接开启一个本地缓冲区读写socket端口
            Socket socket = serverSocket.accept();
            ObjectInputStream ins = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            String className = ins.readUTF();
            String methodName = ins.readUTF();
            Class<?>[] paramTypes = (Class<?>[]) ins.readObject();
            Object[] params = (Object[]) ins.readObject();
            // 处理api对应service对象
            Class apiClazz = Class.forName(className);
            ServiceName serviceName = (ServiceName) apiClazz.getAnnotation(ServiceName.class);
            String clazzNamePath = serviceName.value();
            Class clazz = Class.forName(clazzNamePath);
            Object clazzObj = clazz.newInstance();
            if (clazzObj instanceof UserServiceApi) {
                Method method = clazz.getMethod(methodName, paramTypes);
                Object resultObj = method.invoke(clazzObj, params);
                out.writeObject(resultObj);
                out.flush();
            }
            if (ins != null) {
                ins.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
    }

    private boolean isValid() throws IOException {
        if (serverSocket == null) throw new IOException("Connect refused");
        return !serverSocket.isClosed();
    }

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, IOException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        new RPCServer().start();
    }
}
