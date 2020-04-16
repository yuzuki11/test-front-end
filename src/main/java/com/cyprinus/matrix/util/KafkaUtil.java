package com.cyprinus.matrix.util;

import com.cyprinus.matrix.entity.Submit;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("unchecked")
@Component
public class KafkaUtil {

    private final
    KafkaTemplate template;

    private final
    ObjectUtil objectUtil;

    @Autowired
    public KafkaUtil(KafkaTemplate template, ObjectUtil objectUtil) {
        this.template = template;
        this.objectUtil = objectUtil;
        template.send("email","test");
        template.send("websocket","test");
        template.send("statistics","test");
    }

    static class MailPayload implements Serializable {

        String model;

        String subject;

        String target;

        Object values;

        MailPayload(String model, String subject, String target, Object values) {
            this.model = model;
            this.subject = subject;
            this.target = target;
            this.values = values;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public Object getValues() {
            return values;
        }

        public void setValues(Object values) {
            this.values = values;
        }
    }

    static class WebSocketPayload implements Serializable {

        String level;

        String target;

        String content;

        WebSocketPayload(String level, String target, String content) {
            this.level = level;
            this.target = target;
            this.content = content;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    static class StatisticsPayload implements Serializable {

        private Submit submit;

        private String lessonId;

        public StatisticsPayload() {
        }

        StatisticsPayload(Submit submit, String lessonId) {
            this.submit = submit;
            this.lessonId = lessonId;
        }

        public Submit getSubmit() {
            return submit;
        }

        public void setSubmit(Submit submit) {
            this.submit = submit;
        }

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }
    }

    public void sendMail(String model, String subject, String target,String targetUserId , Object values) throws JsonProcessingException {
        MailPayload payload = new MailPayload(model, subject, target, values);
        String json = objectUtil.obj2json(payload);
        ListenableFuture<SendResult<String, String>> future = template.send("mail", json);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                try {
                    promptByWebsocket("SUCCESS", targetUserId, "正在发送邮件...");
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Throwable e) {
                try {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                try {
                    promptByWebsocket("ERROR", targetUserId, "邮件发送失败！");
                } catch (Exception ignored) {
                }
            }
        });
    }

    public void promptByWebsocket(String level, String target, String content) throws JsonProcessingException {
        WebSocketPayload payload = new WebSocketPayload(level, target, content);//这里的"level"分为SUCCESS INFO ERROR三个等级，对应着SocketIO里的事件
        String json = objectUtil.obj2json(payload);
        template.send("websocket", json);
    }

    public void sendSubmit(String lessonId, Submit submit) throws JsonProcessingException {
        StatisticsPayload payload = new StatisticsPayload(submit, lessonId);
        String json = objectUtil.obj2json(payload);
        template.send("statistics", json);
    }

}
