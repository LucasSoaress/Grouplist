package grouplist.lucassoares.com.grouplist;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import helper.Base64Custom;
import helper.MyApplication;

public class principalActivity extends AppCompatActivity
{
    public TextView mostrarData;
    private String dataFormatada;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");
    private static final String ARQUIVO_PREFERENCIA = "ArquivoPreferencia";
    public String dia;
    public String mes;
    public static final String USUARIOS = "usuarios";
    private RecyclerView mRoomRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<EscapeRoom, RoomViewHolder> mFirebaseAdapter;

    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView tituloText;
        public TextView descricaoText;
        public TextView dataText;

        public RoomViewHolder(View itemView)
        {
            super(itemView);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            tituloText = (TextView)itemView.findViewById(R.id.tituloId);
            descricaoText = (TextView)itemView.findViewById(R.id.descricaoId);
            dataText = (TextView)itemView.findViewById(R.id.dataId);
        }

        @Override
        public void onClick(View view)
        {
            Log.d("oloco","texte");
        }

    }

    public principalActivity()
    {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.activityPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.activityPaused();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group List");
        startService();

        mRoomRecyclerView = (RecyclerView)findViewById(R.id.recyclerViewId);
        mLinearLayoutManager = new LinearLayoutManager(principalActivity.this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.keepSynced(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PREFERENCIA,0);
        String email = sharedPreferences.getString("email","");
        String emailConvertido = Base64Custom.converterBase64(email);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<EscapeRoom, RoomViewHolder>(
                EscapeRoom.class,
                R.layout.principal_layout,
                RoomViewHolder.class,
                mFirebaseDatabaseReference.child(USUARIOS).child(emailConvertido).child("tarefas").orderByChild("data"))

        {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            protected void populateViewHolder(RoomViewHolder viewHolder, EscapeRoom model, int position)
            {
                    viewHolder.tituloText.setText(model.getTitulo());
                    viewHolder.descricaoText.setText(model.getDescricao());
                    viewHolder.dataText.setText(model.getData());
            }
        };



        mRoomRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRoomRecyclerView.setAdapter(mFirebaseAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                View messageLayout = LayoutInflater.from(principalActivity.this).inflate(R.layout.message,null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(principalActivity.this);
                builder.setTitle("Nova tarefa");
              //  builder.setMessage("Adicione uma nova tarefa");
                builder.setView(messageLayout);
                builder.setCancelable(false);

               final EditText tituloTarefa = (EditText)messageLayout.findViewById(R.id.tituloTarefa);
               final EditText descricaoTarefa = (EditText)messageLayout.findViewById(R.id.descricaoTarefa);
               final Button adicionarData = (Button)messageLayout.findViewById(R.id.btn_adicionarData);
               mostrarData = (TextView)messageLayout.findViewById(R.id.textView_mostrarData);


                adicionarData.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        abrirData(view);
                    }
                });

                builder.setPositiveButton("Adicionar tarefa", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String tituloTarefaTexto =  tituloTarefa.getText().toString();
                        String descricaoTarefaTexto = descricaoTarefa.getText().toString();

                        if (tituloTarefaTexto.isEmpty() || descricaoTarefaTexto.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),"Preencha todos os dados, para adicionar uma tarefa" ,Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PREFERENCIA,0);
                            Tarefas tarefas = new Tarefas();

                            tarefas.setTitulo(tituloTarefa.getText().toString());
                            tarefas.setDescricao(descricaoTarefa.getText().toString());
                            tarefas.setData(dataFormatada);

                            if (sharedPreferences.contains("email"))
                            {
                                String emailDoUsuario = sharedPreferences.getString("email", "");
                                String emailConvertido = Base64Custom.converterBase64(emailDoUsuario);
                                try
                                {
                                    usuarioReferencia.child(emailConvertido).child("tarefas").push().setValue(tarefas);
                                    Toast.makeText(getApplicationContext(),"Sua tarefa foi adicionada com sucesso",Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Ocorreu um erro",Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_sair:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(principalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.action_compartilhar:
                break;
            case R.id.action_sobre:
                Intent intent_sobre = new Intent(principalActivity.this,SobreActivity.class);
                startActivity(intent_sobre);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int year, month, day;

    @TargetApi(Build.VERSION_CODES.N)
    public void abrirData(View view)
    {
        iniciarCalendario();
        Calendar cDefault = Calendar.getInstance();
        cDefault.set(year,month,day);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,listener,
                cDefault.get(Calendar.YEAR),cDefault.get(Calendar.MONTH),cDefault.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void iniciarCalendario()
    {
        if (year == 0)
        {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
    }

DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
{
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
    {
        // i = ano
        // i1 = mês
        // i = dia

        i1 += 1;
        if(i2 < 10)
        {
           dia  = "0"+Integer.toString(i2);
        }
        else
        {
            dia = Integer.toString(i2);
        }

        if(i1 < 10)
        {
            mes = "0"+Integer.toString(i1);
        }
        else
        {
            mes = Integer.toString(i1);
        }

       dataFormatada =  dia + "/" + mes + "/" + Integer.toString(i);
        mostrarData.setText("Data: " + dataFormatada);
    }
};
    public void startService()
    {
        startService(new Intent(getBaseContext(),helper.Service.class));
    }
}
