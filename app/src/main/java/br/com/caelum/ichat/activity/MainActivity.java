package br.com.caelum.ichat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import br.com.caelum.ichat.app.ChatApplication;
import br.com.caelum.ichat.adapter.MensagemAdapter;
import br.com.caelum.ichat.callback.EnviarMensagemCallback;
import br.com.caelum.ichat.callback.OuvirMensagemCallback;
import br.com.caelum.ichat.component.ChatComponent;
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

    private List<Mensagem> mensagens;
    private ChatComponent component;
    @Inject
    ChatService chatService;

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

        ouvirMensagem();
    }

    @OnClick(R.id.btn_enviar)
    public void enviarMensagem(){
        chatService.enviar(new Mensagem(idDoCliente, editText.getText().toString()))
                .enqueue(new EnviarMensagemCallback());
    }

    public void ouvirMensagem() {
        Call<Mensagem> call = chatService.ouvirMensagens();
        call.enqueue(new OuvirMensagemCallback(this));
    }

    public void colocaNaLista(Mensagem mensagem) {
        mensagens.add(mensagem);
        MensagemAdapter adapter = new MensagemAdapter(idDoCliente, mensagens, this);
        listaDeMensagens.setAdapter(adapter);
    }
}
