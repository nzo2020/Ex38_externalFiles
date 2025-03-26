package com.example.ex38_externalfiles;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * MainActivity handles file operations for external storage.
 * It allows the user to save, read, reset, and exit with file operations in external storage.
 * The app requests permission to access external storage if not already granted.
 *
 * @author Noa Zohar <nz2020@bs.amalnet.k12.il>
 * @version 1.0
 * @since 26/03/2025
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Request code for permission result.
     */
    private static final int REQUEST_CODE_PERMISSION = 1;

    /**
     * The name of the external storage file.
     */
    private final String FILENAME = "exttest.txt";

    /**
     * EditText for user input.
     */
    private EditText etInput;

    /**
     * TextView to display the content of the file.
     */
    private TextView tvShow;

    /**
     * Called when the activity is created. Sets up the layout and checks for permissions and external storage availability.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = findViewById(R.id.etInput);
        tvShow = findViewById(R.id.tvShow);

        if (checkPermission() && isExternalStorageAvailable()){
            tvShow.setText(readText());
        }else{
            requestPermission();
        }
    }

    /**
     * Checks if external storage is available.
     *
     * @return True if external storage is available, false otherwise.
     */


    public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if the app has permission to write to external storage.
     *
     * @return True if permission is granted, false otherwise.
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permission to write to external storage.
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
    }

    /**
     * Handles the result of the permission request.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission to access external storage granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission to access external storage NOT granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Saves the content of the EditText to the external storage file.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void save_click(View view) {
        if (isExternalStorageAvailable() && checkPermission()) {
            try {
                File externalDir = Environment.getExternalStorageDirectory();
                File file = new File(externalDir, FILENAME);
                file.getParentFile().mkdirs();
                FileWriter writer = new FileWriter(file, true);
                writer.write(etInput.getText().toString());
                writer.close();
                tvShow.setText(readText());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save text file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "External memory or permission problem", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Clears the content of the external storage file and resets the EditText and TextView.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void reset_click(View view) {
        if (isExternalStorageAvailable() && checkPermission()) {
            try {
                File externalDir = Environment.getExternalStorageDirectory();
                File file = new File(externalDir, FILENAME);
                file.getParentFile().mkdirs();
                FileWriter writer = new FileWriter(file);
                writer.write("");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to clear text file", Toast.LENGTH_SHORT).show();
            }
            etInput.setText("");
            tvShow.setText("");
        } else {
            Toast.makeText(this, "External memory or permission problem", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the content and exits the application.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void exit_click(View view) {
        save_click(view);
        finish();
    }

    /**
     * Reads the content of the external storage file.
     *
     * @return The text content from the file.
     */
    public String readText() {
        String text = "";
        try {
            File externalDir = Environment.getExternalStorageDirectory();
            File file = new File(externalDir, FILENAME);
            file.getParentFile().mkdirs();
            FileReader reader = new FileReader(file);
            BufferedReader bR = new BufferedReader(reader);
            StringBuilder sB = new StringBuilder();
            String line = bR.readLine();
            while (line != null) {
                sB.append(line + '\n');
                line = bR.readLine();
            }
            text = sB.toString();
            bR.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * Inflates the menu and adds the option for credits.
     *
     * @param menu The menu to inflate.
     * @return True if the menu was successfully inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creditsmain, menu);
        return true;
    }

    /**
     * Handles menu item selections, navigating to the credits screen if selected.
     *
     * @param item The selected menu item.
     * @return True if the selection was handled.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuCredits) {
            Intent intent = new Intent(this, mainCredits.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
