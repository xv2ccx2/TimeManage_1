package com.example.timemanage;

import android.view.View;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.TimeUnit;


/**主要用于实现对接文心一言API的功能
 */
public class WenXin{
    public static final String APP_ID = "60393524";//这个似乎还用不到
    public static final String API_KEY = "3xEP0U8Z9fSdoOdrLj8qEn5F";//填你自己应用的apikey
    public static final String SECRET_KEY = "RweV1nsNmBiiJGgJjEy0edgovEYEMfGs";//填你自己应用的secretkey

    public JSONArray Dialogue_Content;//用来储存对话内容，当然初始是空的

    WenXin(){
        //构造函数，先初始化Dialogue_Content一下，此时里边是空的啥也没有
        //不过也可以预先添加对话，以实现一些希望的业务功能
        Dialogue_Content=new JSONArray();
    }

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .readTimeout(60000,TimeUnit.MILLISECONDS)
            .build();

    public String GetAnswer(String user_msg) throws IOException, JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("role", "user");
        jsonObject.put("content", user_msg);

        // 将JSONObject添加到JSONArray中
        //这里就是把用户说的话添加进对话内容里，然后发给文心一言
        Dialogue_Content.put(jsonObject);

        MediaType mediaType = MediaType.parse("application/json");
        //这是一行参考代码，只能进行一次对话，要想多次对话就必须动态添加历史对话的内容
        //RequestBody body = RequestBody.create(mediaType,  "{\"messages\":[{\"role\":\"user\",\"content\":\"你好啊\"}],\"disable_search\":false,\"enable_citation\":false}");

        RequestBody body = RequestBody.create(mediaType,  "{\"messages\":" +
                Dialogue_Content.toString() +
                ",\"disable_search\":false,\"enable_citation\":false}");


        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" +
                        getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();

        //解析出文心一言的回答
        JSONObject json_feedback = new JSONObject(response.body().string());
        //这里在开发的时候遇到了一个问题，注意response在上一行被取出里边的内容之后就自动关闭了，不能多次传参。
        String re=json_feedback.getString("result");
        //接下来把文心一言的回答加入到Dialogue_Content中
        JSONObject jsontmp=new JSONObject();
        jsontmp.put("assistant",re);
        Dialogue_Content.put(jsontmp);
        re = re + '\n' + '\n' + '\n';

        return re;
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    public String getAccessToken() throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
}