package grouplist.lucassoares.com.grouplist;


import android.widget.EditText;

public class Tarefas {

    private String titulo;
    private String descricao;
    private String data;

    public Tarefas() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

