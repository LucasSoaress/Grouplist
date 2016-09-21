package grouplist.lucassoares.com.grouplist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import helper.Base64Custom;

public class MainActivity extends AppCompatActivity
{
    private EditText emailUsuario;
    private EditText senhaUsuario;
    private Button   fazerLogin;
    private TextView fazerCadastro;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String emailDoUsuario;
    private String senhaDoUsuario;
    private static final String ARQUIVO_PREFERENCIA = "ArquivoPreferencia";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailUsuario = (EditText)findViewById(R.id.login_email);
        senhaUsuario = (EditText)findViewById(R.id.login_senha);
        fazerLogin = (Button)findViewById(R.id.login_botao);
        fazerCadastro = (TextView)findViewById(R.id.login_naoTemConta);

        mAuth = FirebaseAuth.getInstance();

        fazerCadastro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fazerLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (verificaConexao())
                {
                    emailDoUsuario = emailUsuario.getText().toString();
                    senhaDoUsuario = senhaUsuario.getText().toString();

                    if(emailDoUsuario.isEmpty() || senhaDoUsuario.isEmpty())
                    {
                        Toast.makeText(MainActivity.this,"Digite seu email e senha, para que possa efetuar o login",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        fazerLogin();
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this,"Você não tem uma conexão com a internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    Intent intent = new Intent(MainActivity.this, principalActivity.class);
                    startActivity(intent);
                    finish();
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

    private void fazerLogin()
    {
        mAuth.signInWithEmailAndPassword(emailUsuario.getText().toString(),senhaUsuario.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PREFERENCIA,0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",emailDoUsuario);
                            editor.commit();

                            Intent intent = new Intent(MainActivity.this, principalActivity.class);
                            startActivity(intent);
                            finish();
                        }


                        else if (!task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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


}
