package zhangchongantest.neu.edu.newtest.Okhttp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpModel {
    private OkHttpClient mOkHttpClient;
    public OkhttpModel() {
        mOkHttpClient = new OkHttpClient();
    }

    protected void asyRequest(String url){
        Request request = new Request.Builder().url("http://www.baidu.com")
                                                .method("GET",null).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("DEBUG","asy response msg:"+response.body().string());
            }
        });
    }

    protected void synRequest(String url){
        Request request = new Request.Builder().url("http://www.baidu.com")
                .method("GET",null).build();
        Call call = mOkHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Log.e("DEBUG","syn response msg:"+response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void postKeyValue(){
        RequestBody requestBody = new FormBody.Builder().add("accountName","john")
                .add("accountPassword","123")
                .build();
        Request request = new Request.Builder().url("URL").post(requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    protected void postString(){
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String value  = "{accountName:john;accountPassword:123}";
        RequestBody requestBody = RequestBody.create(mediaType,value);
        Request request = new Request.Builder().url("url").post(requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    protected void postFile(){
        File file = new File(Environment.getDataDirectory(),"zhangchongan.png");
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = RequestBody.create(mediaType,file);
        Request request = new Request.Builder().url("URL").post(requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    protected void getFile(){
        Request request = new Request.Builder().url("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1861936634,3639793594&fm=26&gp=0.jpg").get().build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DEBUG","getFile fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                int length = 0;
                File file = new File(Environment.getExternalStorageDirectory(),"sheep.jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while ((length = inputStream.read(buffer))!=-1){
                    fileOutputStream.write(buffer,0,length);
                    Log.e("DEBUG","getFile length"+length);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            }
        });
    }

    protected void postMultipartFile(){
        File file = new File(Environment.getExternalStorageDirectory(),"sheep.jpg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("accountName","john")
                .addFormDataPart("accountPasswod","123")
                .addFormDataPart("image","sheep.jpg",RequestBody.create(MediaType.parse("image/jpg"),file))
                .build();
        Request request = new Request.Builder().url("URL").post(requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

}
