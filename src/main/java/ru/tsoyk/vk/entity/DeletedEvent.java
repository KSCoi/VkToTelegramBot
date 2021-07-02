package ru.tsoyk.vk.entity;

import com.google.gson.annotations.SerializedName;

public class DeletedEvent {
    @SerializedName("owner_id")
    private Integer ownerId;
    @SerializedName("id")
    private Integer id;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("deleter_id")
    private Integer deleterId;
}
