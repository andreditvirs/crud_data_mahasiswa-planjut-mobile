package com.planjut.basiccrudmahasiswa.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.google.android.material.textfield.TextInputEditText;
import com.planjut.basiccrudmahasiswa.R;
import com.planjut.basiccrudmahasiswa.api.APIUtils;
import com.planjut.basiccrudmahasiswa.api.ApiMahasiswa;
import com.planjut.basiccrudmahasiswa.api.RequestHandler;
import com.planjut.basiccrudmahasiswa.controller.FileService;
import com.planjut.basiccrudmahasiswa.model.FotoMahasiswa;
import com.planjut.basiccrudmahasiswa.model.Mahasiswa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextId;
    TextInputEditText txtINama, txtIAlamat, txtIETTmpLahir, txtIETTglLahir;
    ProgressBar progressBar;
    ListView listView;
    Button buttonAddUpdate;

    List<Mahasiswa> mahasiswaList;
    boolean isUpdating = false;

    //Retrofit Area
    FileService fileService;
    Button btnChooseFile;
    Button btnUpload;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextId = findViewById(R.id.edTxtMahasiswaId);
        txtINama = findViewById(R.id.txtINama);
        txtIAlamat = findViewById(R.id.txtIAlamat);
        txtIETTmpLahir = findViewById(R.id.txtIETTmpLahir);
        txtIETTglLahir = findViewById(R.id.txtIETTglLahir);
        buttonAddUpdate = findViewById(R.id.buttonAddUpdate);
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.lViewMahasiswa);
        mahasiswaList = new ArrayList<>();

        buttonAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdating) {
                    updateMahasiswa();
                } else {
                    createMahasiswa();
                }
            }
        });
        readMahasiswa();

        // Retrofit Area
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnUpload = findViewById(R.id.btnUpload);
        fileService = APIUtils.getFileService();

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

    }

    private void createMahasiswa() {
        String nama = txtINama.getText().toString().trim();
        String alamat = txtIAlamat.getText().toString().trim();
        String tmpLahir = txtIETTmpLahir.getText().toString().trim();
        String tglLahir = txtIETTglLahir.getText().toString().trim();
        if (TextUtils.isEmpty(nama)) {
            txtINama.setError("Silahkan masukkan nama");
            txtINama.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(alamat)) {
            txtIAlamat.setError("Silahkan masukkan alamat");
            txtIAlamat.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tmpLahir)) {
            txtIETTmpLahir.setError("Silahkan masukkan tempat lahir");
            txtIETTmpLahir.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tglLahir)) {
            txtIETTglLahir.setError("Silahkan masukkan tanggal lahir");
            txtIETTglLahir.requestFocus();
            return;
        }

        // Retrofit dulu ,baru ditankap nama buat database
        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Call<FotoMahasiswa> call = fileService.upload(body, filename);
        call.enqueue(new Callback<FotoMahasiswa>() {
            @Override
            public void onResponse(Call<FotoMahasiswa> call, Response<FotoMahasiswa> response) {
                if(response.isSuccessful()){
//                    Toast.makeText(MainActivity.this, "Image upload successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FotoMahasiswa> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String, String> params = new HashMap<>();
        params.put("nama", nama);
        params.put("alamat", alamat);
        params.put("tmpLahir", tmpLahir);
        params.put("tglLahir", tglLahir);
        params.put("imageName", file.getName());

        PerformNetworkRequest request = new PerformNetworkRequest(ApiMahasiswa.URL_CREATE, params, CODE_POST_REQUEST);
        request.execute();

        txtINama.setText("");
        txtIAlamat.setText("");
        txtIETTmpLahir.setText("");
        txtIETTglLahir.setText("");
    }

    public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        public PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                    refreshMahasiswaList(object.getJSONArray("mahasiswa"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    public class MahasiswaAdapter extends ArrayAdapter<Mahasiswa> {
        List<Mahasiswa> mahasiswaList;

        public MahasiswaAdapter(List<Mahasiswa> mahasiswaList) {
            super(MainActivity.this, R.layout.layout_mahasiwa_list, mahasiswaList);
            this.mahasiswaList = mahasiswaList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_mahasiwa_list, null, true);
            TextView textViewNama = listViewItem.findViewById(R.id.txtVNama);
            TextView textViewUpdate = listViewItem.findViewById(R.id.txtVUpdate);
            TextView textViewDelete = listViewItem.findViewById(R.id.txtVDelete);
            final Mahasiswa mahasiswa = mahasiswaList.get(position);
            textViewNama.setText(mahasiswa.getNama());
            textViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdating = true;
                    editTextId.setText(String.valueOf(mahasiswa.getId()));
                    txtINama.setText(mahasiswa.getNama());
                    txtIAlamat.setText(mahasiswa.getAlamat());
                    txtIAlamat.setText(mahasiswa.getAlamat());
                    txtIETTmpLahir.setText(mahasiswa.getTmpLahir());
                    txtIETTglLahir.setText(mahasiswa.getTglLahir());
                    buttonAddUpdate.setText("Perbaharui Data");
                }
            });
            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Hapus " + mahasiswa.getNama())
                            .setMessage("Apakah Anda yakin ingin menghapusnya?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteMahasiswa(mahasiswa.getId());
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            return listViewItem;
        }
    }

    private void readMahasiswa() {
        PerformNetworkRequest request = new PerformNetworkRequest(ApiMahasiswa.URL_READ, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshMahasiswaList(JSONArray mahasiswa) throws JSONException {
        mahasiswaList.clear();
        for (int i = 0; i < mahasiswa.length(); i++) {
            JSONObject obj = mahasiswa.getJSONObject(i);
            mahasiswaList.add(new Mahasiswa(
                    obj.getInt("id"),
                    obj.getString("nama"),
                    obj.getString("alamat"),
                    obj.getString("tmpLahir"),
                    obj.getString("tglLahir"),
                    obj.getString("imageName")
            ));
        }
        Log.d(MainActivity.class.getSimpleName(), mahasiswaList.toString());
        MahasiswaAdapter adapter = new MahasiswaAdapter(mahasiswaList);
        listView.setAdapter(adapter);
    }

    private void updateMahasiswa() {
        String id = editTextId.getText().toString().trim();
        String nama = txtINama.getText().toString().trim();
        String alamat = txtIAlamat.getText().toString().trim();
        String tmpLahir = txtIETTmpLahir.getText().toString().trim();
        String tglLahir = txtIETTglLahir.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            txtINama.setError("Silahkan masukkan nama");
            txtINama.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(alamat)) {
            txtIAlamat.setError("Silahkan masukkan alamat");
            txtIAlamat.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tmpLahir)) {
            txtIETTmpLahir.setError("Silahkan masukkan tempat lahir");
            txtIETTmpLahir.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tglLahir)) {
            txtIETTglLahir.setError("Silahkan masukkan tanggal lahir");
            txtIETTglLahir.requestFocus();
            return;
        }

        // Retrofit dulu ,baru ditankap nama buat database
        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Call<FotoMahasiswa> call = fileService.upload(body, filename);
        call.enqueue(new Callback<FotoMahasiswa>() {
            @Override
            public void onResponse(Call<FotoMahasiswa> call, Response<FotoMahasiswa> response) {
                if(response.isSuccessful()){
//                    Toast.makeText(MainActivity.this, "Image upload successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FotoMahasiswa> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("nama", nama);
        params.put("alamat", alamat);
        params.put("tmpLahir", tmpLahir);
        params.put("tglLahir", tglLahir);
        params.put("imageName", file.getName());

        PerformNetworkRequest request = new PerformNetworkRequest(ApiMahasiswa.URL_UPDATE, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUpdate.setText("Tambahkan Data");
        txtINama.setText("");
        txtIAlamat.setText("");
        txtIETTmpLahir.setText("");
        txtIETTglLahir.setText("");

        isUpdating = false;
    }

    private void deleteMahasiswa(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(ApiMahasiswa.URL_DELETE + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    //Retrofit Area

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (data == null){
                Toast.makeText(this, "Unable to choose image!", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri imageUri = data.getData();
            imagePath = getRealPathFromUri(imageUri);
        }
    }

    private String getRealPathFromUri(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }
}
