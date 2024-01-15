package org.evosuite.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class model {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        // 构建请求体
        RequestBody body = buildRequestBody();

        String token = "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"; // 请替换为实际的 Bearer Token
        Request request = new Request.Builder()
                .url("https://cn2us02.opapi.win/v1/chat/completions")
                .method("POST", body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)  // 添加 Bearer Token
                .build();

        try {
            Response response = client.newCall(request).execute();

            //将 JSON 转换为数据结构
            String responseBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            ChatCompletion chatCompletion = objectMapper.readValue(responseBody, ChatCompletion.class);

            System.out.println("Assistant's response: " + chatCompletion.getChoices().get(0).getMessage().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //创建请求体，转换为json格式
    private static RequestBody buildRequestBody() {

        String model = "gpt-3.5-turbo";
        String systemContent = "You are a helpful assistant.";
        String userContent = "Say test";

        // 构建消息列表
        List<requestMessage> requetmessages = Arrays.asList(
                new requestMessage("system", systemContent),
                new requestMessage("user", userContent)
        );

        // 构建 payload 对象
        Payload payload = new Payload(model, requetmessages);

        // 转换 payload 为 JSON 字符串
        String payloadJson = "{\"model\":\"" + payload.model + "\",\"messages\":[";
        for (requestMessage requetmessage : payload.requetmessages) {
            payloadJson += "{\"role\":\"" + requetmessage.role + "\",\"content\":\"" + requetmessage.content + "\"},";
        }
        payloadJson = payloadJson.substring(0, payloadJson.length() - 1) + "]}";

        // 构建 RequestBody
        MediaType mediaType = MediaType.parse("application/json");
        return RequestBody.create(mediaType, payloadJson);
    }

    //创建payload数据格式
    private static class Payload {
        String model;
        List<requestMessage> requetmessages;

        Payload(String model, List<requestMessage> requetmessages) {
            this.model = model;
            this.requetmessages = requetmessages;
        }
    }

    // 数据结构用于表示消息
    private static class requestMessage {
        String role;
        String content;

        requestMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ChatCompletion {
        private List<Choice> choices;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Choice> getChoices() {
            return choices;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Message {
        private String role;
        private String content;

        public String getContent() {
            return content;
        }
    }
}

