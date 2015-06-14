package programados.ejemploconexionbasedatos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends ActionBarActivity {

    private EditText etNombre;
    private EditText etVideoconsola;
    private TextView tvGenero;
    private TextView tvValoracion;
    private TextView tvPEGI;
    private TextView tvPrecio;
    private Button btnConectar;

    private String IP = "192.168.1.131:3306";
    private String baseDatos = "/videoconsolas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = (EditText)findViewById(R.id.etNombre);
        etVideoconsola = (EditText)findViewById(R.id.etVideoconsola);
        tvGenero = (TextView)findViewById(R.id.tvGenero);
        tvValoracion = (TextView)findViewById(R.id.tvValoracion);
        tvPEGI = (TextView)findViewById(R.id.tvPEGI);
        tvPrecio = (TextView)findViewById(R.id.tvPrecio);
        btnConectar = (Button)findViewById(R.id.btnConectar);


        btnConectar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String videoconsola = etVideoconsola.getText().toString();
                String juego = etNombre.getText().toString();

                if (videoconsola.equals("New Nintendo3DS")){
                    videoconsola = "`" + videoconsola + "`";
                }
                new ConexionDB().execute(IP,baseDatos,videoconsola,juego);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ConexionDB extends AsyncTask<String,Void,ResultSet>{

        @Override
        protected ResultSet doInBackground(String... strings) {

            try {
                Connection conn;
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://"+strings[0]+strings[1], "root", "");

                Statement estado = conn.createStatement();
                System.out.println("Conexion establecida");
                String peticion ="select * from " +strings[2]+" where nombre='"+strings[3]+"'";
                ResultSet result = estado.executeQuery(peticion);
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultSet result) {

            try {
                if (result != null){
                    if (!result.next()) {
                        Toast toast = Toast.makeText(getApplicationContext(),"No existen resultados con ese nombre",Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        tvGenero.setText(result.getString("genero"));
                        tvValoracion.setText(Float.toString(result.getFloat("valoracion")));
                        tvPEGI.setText(Integer.toString(result.getInt("PEGI")));
                        tvPrecio.setText(Float.toString(result.getFloat("precio")));
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"La videoconsola no está en la base de datos",Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
