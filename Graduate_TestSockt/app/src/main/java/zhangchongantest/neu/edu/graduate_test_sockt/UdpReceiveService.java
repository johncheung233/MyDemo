package zhangchongantest.neu.edu.graduate_test_sockt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class UdpReceiveService extends Service {
    private ReceiveBinder mbinder = new ReceiveBinder();
    private DatagramSocket mDataSocket;
    private ByteArrayOutputStream byteOutputStream;
    private int mPort;
    private String str_result;

    public UdpReceiveService() {
    }

    class ReceiveBinder extends Binder {
        UdpReceiveService getBinder() {
            return UdpReceiveService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Config.TAG, "onCreat");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(Config.TAG, "onBind");
        mPort = Integer.parseInt(intent.getStringExtra(Config.GET_PORT));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    while (true) {
//                        Socket socket = DataManager.getInstance().getSocket();
//                        if (socket == null || socket.isClosed()) {
//                            Thread.sleep(2000);
//                            continue;
//                        }
//                        InputStream inputStream = socket.getInputStream();
//                        if (byteOutputStream == null) {
//                            byteOutputStream = new ByteArrayOutputStream(1024);
//                        }
//                        byte[] buffer = new byte[1024];
//                        int length = 0;
//                        String recev = "";
//                        while ((length = inputStream.read(buffer)) != -1) {
//                            byteOutputStream.write(buffer, 0, length);
//                            recev = byteOutputStream.toString("utf-8");
//                            Log.e(Config.TAG, "receive msg = " + recev);
//                            if (recev.contains(Config.End_char)) {
//                                setStr_result(recev);
//                                byteOutputStream.reset();
//                                socket.close();
//                                break;
//                            }
//                        }
                    DatagramPacket packet;
                    StringBuilder builder = new StringBuilder();
                    while (true) {
                        if (mDataSocket == null || mDataSocket.isClosed()) {
                            mDataSocket = new DatagramSocket(mPort);
                        }
                        byte[] datas = new byte[1024];
                        packet = new DatagramPacket(datas, datas.length);
                        Log.e(Config.TAG, "receive Init");
                        mDataSocket.receive(packet);
                        Log.e(Config.TAG, "received");
                        String result = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        setStr_result(result);
//                        Log.e(Config.TAG, "result=" + result);
//                        builder.append(result)
//                                .append(">>>")
//                                .append("respon");
//                        String response = builder.toString();
//                        mDataSocket.send(new DatagramPacket(response.getBytes(),response.getBytes().length,packet.getAddress(),mPort));
//                        builder.delete(0,builder.length());
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return mbinder;
    }

    public String getStr_result() {
        return str_result;
    }

    public void setStr_result(String str_result) {
        this.str_result = str_result;
    }

}
