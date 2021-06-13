package ru.tsoyk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VkManager {

    @Autowired
    public static Vk vk;

    public void sendMessage(String msg) {

    }
}
