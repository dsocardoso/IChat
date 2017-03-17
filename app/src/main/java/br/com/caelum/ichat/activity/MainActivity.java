package br.com.caelum.ichat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import br.com.caelum.ichat.app.ChatApplication;
import br.com.caelum.ichat.adapter.MensagemAdapter;
import br.com.caelum.ichat.callback.EnviarMensagemCallback;
import br.com.caelum.ichat.callback.FailureEvent;
import br.com.caelum.ichat.callback.OuvirMensagemCallback;
import br.com.caelum.ichat.component.ChatComponent;
import br.com.caelum.ichat.event.MensagemEvent;
import br.com.caelum.ichat.modelo.Mensagem;
import br.com.caelum.ichat.service.ChatService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caelum.com.br.ichat_alura.R;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private int idDoCliente;
    @BindView(R.id.btn_enviar)
    Button button;
    @BindView(R.id.et_texto)
    EditText editText;
    @BindView(R.id.mensagem)
    ListView listaDeMensagens;
    @BindView(R.id.iv_avatar_usuario)
    ImageView avatar;

    private List<Mensagem> mensagens;
    private ChatComponent component;
    @Inject
    ChatService chatService;
    @Inject
    Picasso picasso;
    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // chamada inj dependência
        ChatApplication app = (ChatApplication) getApplication();
        component = app.getComponent();
        component.inject(this);

        // inicializando lista
        mensagens = new ArrayList<>();
        MensagemAdapter adapter = new MensagemAdapter(idDoCliente, mensagens, this);
        listaDeMensagens.setAdapter(adapter);
        picasso.with(this).load("http://api.adorable.io/avatars/285/" + idDoCliente + ".png").into(avatar);

        ouvirMensagem(new MensagemEvent());
        eventBus.register(this);
    }

    @OnClick(R.id.btn_enviar)
    public void enviarMensagem(){
        chatService.enviar(new Mensagem(idDoCliente, editText.getText().toString()))
                .enqueue(new EnviarMensagemCallback());
    }

    @Subscribe
    public void colocaNaLista(MensagemEvent mensagemEvent) {
        mensagens.add(mensagemEvent.mensagem);
        MensagemAdapter adapter = new MensagemAdapter(idDoCliente, mensagens, this);
        listaDeMensagens.setAdapter(adapter);
    }
    @Subscribe
    public void ouvirMensagem(MensagemEvent mensagemEvent) {
        Call<Mensagem> call = chatService.ouvirMensagens();
        call.enqueue(new OuvirMensagemCallback(this,eventBus));
    }
    @Subscribe
    public void lidarCom(FailureEvent event){
        ouvirMensagem(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);

    }
}
