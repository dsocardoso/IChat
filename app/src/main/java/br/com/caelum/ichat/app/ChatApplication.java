package br.com.caelum.ichat.app;

import android.app.Application;

import br.com.caelum.ichat.component.ChatComponent;
import br.com.caelum.ichat.component.DaggerChatComponent;
import br.com.caelum.ichat.module.ChatModule;

/**
 * Created by dsocardoso on 16/03/17.
 */

public class ChatApplication extends Application{
    private ChatComponent component;

    @Override
    public void onCreate(){
        super.onCreate();
        component = DaggerChatComponent.builder().chatModule(new ChatModule(this)).build();
    }
    public ChatComponent getComponent(){
        return component;
    }
}
