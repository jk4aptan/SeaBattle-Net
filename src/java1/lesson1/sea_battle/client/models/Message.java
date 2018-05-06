package java1.lesson1.sea_battle.client.models;

import java1.lesson1.sea_battle.server.models.Messageable;

import java.io.Serializable;

public class Message implements Messageable, Serializable {
    private String type;
    private String content;

    public Message(String type, String content) {
        this.type = type;
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getType() {
        return type;
    }
}
