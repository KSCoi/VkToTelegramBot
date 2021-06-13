package ru.tsoyk.service;

import com.vk.api.sdk.objects.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class VkServer {

    @Autowired
    public static Vk vk;

    public static void main(String[] args) throws InterruptedException {
        while (true)    {
            Thread.sleep(300);

            try {
                Message message = vk.getMessageText();
                if (message != null)    {
                   // execute(message);
                }
            }
            catch ( Exception e )   {

            }
        }

    }
}
