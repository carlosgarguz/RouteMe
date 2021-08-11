package com.carlosgarguz.routeme.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.fragments.MyPreferenceFragment;

public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.configuration_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_preferences_menu_item:
                Toast.makeText(this, "Los valores se han reseteado correctamente", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.clear();
                prefsEditor.commit();
                PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.configuration_layout, true);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
                //MapFragment.actualizarPosicionInicial(prefs);

                /*getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().getBackStackEntryCount();
                finish();*/


                 /*Preferencias compartidas o shared preferencias,cada preferencia se almacena como
                una clave valor.
                Una vez obtenida nuestra coleccion de preferencias podemos insertar,modificar o
                modificar preferencias, pero para poder hacer eso tendremos que accceder a su objecto
                edit*/
                /*SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

                editor.clear();
                //una vez que hemos terminado los cambios utilizaremos commit para confirmarlos
                editor.commit();*/

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
