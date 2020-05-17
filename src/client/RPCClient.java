package client;

import api.UserServiceApi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RPCClient {

    /**
     * rpc调用远程服务
     * 代理调用屏蔽远程服务调用过程
     * 代理过程：
     * 1、建立远程服务的socket连接
     * 2、获取代理方法的类型、方法、参数信息
     * 3、获取socket连接的输入和输出流
     * 4、将请求信息输入输出流
     * 5、获取远程服务调用返回结果
     * 6、对调用结果进行相应的处理
     *
     * @param clazz 代理的类型
     * @return 远程调用结果
     */
    public static Object rpc(Class clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 建立与内存缓冲区的socket连接
                Socket socket = new Socket("127.0.0.1",8010);

                // 获取代理类型、代理方法、参数类型、参数对象
                String className = clazz.getName();
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();

                // 将请求内容发送到socket输出流中
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeUTF(className);
                out.writeUTF(methodName);
                out.writeObject(parameterTypes);
                out.writeObject(args);

                // 从socket输入流中获取返回结果
                ObjectInputStream ins = new ObjectInputStream(socket.getInputStream());
                Object obj = ins.readObject();

                //关闭连接内存缓冲区的输入输出流、socket连接
                ins.close();
                out.close();
                socket.close();
                return obj;
            }
        });
    }

    public static void main(String[] args) {
        UserServiceApi userServiceApi = (UserServiceApi) rpc(UserServiceApi.class);
        System.out.println(userServiceApi.getUser());
    }
}
