package grouplist.lucassoares.com.grouplist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import helper.Base64Custom;
import model.usuario;

public class CadastroActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnCadastrarUsuario;
    private EditText nomeDoUsuario;
    private EditText senhaDoUsuario;
    private EditText emailDoUsuario;
    private model.usuario usuario;
    private String emailTexto;
    private String nomeTexto;
    private String senhaTexto;
    private static final String ARQUIVO_PREFERENCIA = "ArquivoPreferencia";

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");


        btnCadastrarUsuario = (Button) findViewById(R.id.cadastro_botaoCadastrar);
        nomeDoUsuario = (EditText) findViewById(R.id.cadastro_nome);
        emailDoUsuario = (EditText) findViewById(R.id.cadastro_email);
        senhaDoUsuario = (EditText) findViewById(R.id.cadastro_senha);


        btnCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (verificaConexao())
                {
                    nomeTexto = nomeDoUsuario.getText().toString();
                    emailTexto = emailDoUsuario.getText().toString();
                    senhaTexto = senhaDoUsuario.getText().toString();

                    if (nomeTexto.isEmpty() ||emailTexto.isEmpty()|| senhaTexto.isEmpty())
                    {
                        Toast.makeText(CadastroActivity.this,"Preencha os campos, para criar sua conta",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        usuario = new usuario();
                        usuario.setNome(nomeTexto);
                        usuario.setEmail(emailTexto);
                        usuario.setSenha(senhaTexto);
                        cadastrarUsuario();
                    }
                }
                else
                {
                    Toast.makeText(CadastroActivity.this,"Você não tem uma conexão com a internet, por favor tente mais tarde.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {

                }
                else
                {

                }
            }
        };
    }
    public  boolean verificaConexao()
    {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected())
        {
            conectado = true;
        }
        else
        {
            conectado = false;
        }
        return conectado;
    }

    private void cadastrarUsuario()
    {
        mAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
                            startActivity(intent);
                            SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PREFERENCIA,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",usuario.getEmail());
                            editor.commit();
                        }
                        else if (!task.isSuccessful())
                        {
                            Toast.makeText(CadastroActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }
}
