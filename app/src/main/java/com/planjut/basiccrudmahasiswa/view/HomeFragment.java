package com.planjut.basiccrudmahasiswa.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextId;
    TextInputEditText txtINrp, txtINama, txtIAlamat, txtIETTmpLahir, txtIETTglLahir;
    TextView txtVImageName;
    ProgressBar progressBar;
    ScrollView scrollView;
    ListView listView;
    Button buttonAddUpdate;

    List<Mahasiswa> mahasiswaList;
    boolean isUpdating = false;

    //Retrofit Area
    FileService fileService;
    Button btnChooseFile;
    Button btnUpload;
    String imagePath;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        editTextId = rootView.findViewById(R.id.edTxtMahasiswaId);
        txtINrp = rootView.findViewById(R.id.txtINrp);
        txtINama = rootView.findViewById(R.id.txtINama);
        txtIAlamat = rootView.findViewById(R.id.txtIAlamat);
        txtIETTmpLahir = rootView.findViewById(R.id.txtIETTmpLahir);
        txtIETTglLahir = rootView.findViewById(R.id.txtIETTglLahir);
        txtVImageName = rootView.findViewById(R.id.txtVFoto);
        buttonAddUpdate = rootView.findViewById(R.id.buttonAddUpdate);
        progressBar = rootView.findViewById(R.id.progressBar);
        listView = rootView.findViewById(R.id.lViewMahasiswa);
        scrollView = rootView.findViewById(R.id.scVListMahasiswa);
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
        btnChooseFile = rootView.findViewById(R.id.btnChooseFile);
//        btnUpload = rootView.findViewById(R.id.btnUpload);
        fileService = APIUtils.getFileService();

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        return rootView;
    }

    private void createMahasiswa() {
        String nrp = txtINrp.getText().toString().trim();
        String nama = txtINama.getText().toString().trim();
        String alamat = txtIAlamat.getText().toString().trim();
        String tmpLahir = txtIETTmpLahir.getText().toString().trim();
        String tglLahir = txtIETTglLahir.getText().toString().trim();
        String imageName = txtVImageName.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            txtINama.setError("Silahkan masukkan nama");
            txtINama.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nrp)){
            txtINrp.setError("Silahkan masukkan nrp");
            txtINrp.requestFocus();
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
        if (TextUtils.isEmpty(imageName) || txtVImageName.getText().equals("Upload foto terlebih dahulu!")) {
            txtVImageName.setText("Upload foto terlebih dahulu!");
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
                Toast.makeText(getActivity(), "ERROR! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String, String> params = new HashMap<>();
        params.put("nrp", nrp);
        params.put("nama", nama);
        params.put("alamat", alamat);
        params.put("tmpLahir", tmpLahir);
        params.put("tglLahir", tglLahir);
        params.put("imageName", file.getName());

        HomeFragment.PerformNetworkRequest request = new HomeFragment.PerformNetworkRequest(ApiMahasiswa.URL_CREATE, params, CODE_POST_REQUEST);
        request.execute();

        txtINrp.setText("");
        txtINama.setText("");
        txtIAlamat.setText("");
        txtIETTmpLahir.setText("");
        txtIETTglLahir.setText("");
        txtVImageName.setText("");
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
                    Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_LONG).show();
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
            super(getActivity(), R.layout.layout_mahasiwa_list, mahasiswaList);
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
                    txtINrp.setText(mahasiswa.getNrp());
                    txtINama.setText(mahasiswa.getNama());
                    txtIAlamat.setText(mahasiswa.getAlamat());
                    txtIAlamat.setText(mahasiswa.getAlamat());
                    txtIETTmpLahir.setText(mahasiswa.getTmpLahir());
                    txtIETTglLahir.setText(mahasiswa.getTglLahir());
                    txtVImageName.setText(mahasiswa.getImageName());
                    buttonAddUpdate.setText("Perbaharui Data");
                }
            });
            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        HomeFragment.PerformNetworkRequest request = new HomeFragment.PerformNetworkRequest(ApiMahasiswa.URL_READ, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshMahasiswaList(JSONArray mahasiswa) throws JSONException {
        mahasiswaList.clear();
        for (int i = 0; i < mahasiswa.length(); i++) {
            JSONObject obj = mahasiswa.getJSONObject(i);
            mahasiswaList.add(new Mahasiswa(
                    obj.getInt("id"),
                    obj.getString("nrp"),
                    obj.getString("nama"),
                    obj.getString("alamat"),
                    obj.getString("tmpLahir"),
                    obj.getString("tglLahir"),
                    obj.getString("imageName")
            ));
        }
        Log.d(HomeFragment.class.getSimpleName(), mahasiswaList.toString());
        HomeFragment.MahasiswaAdapter adapter = new HomeFragment.MahasiswaAdapter(mahasiswaList);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);

    }

    private void updateMahasiswa() {
        String id = editTextId.getText().toString().trim();
        String nrp = txtINrp.getText().toString().trim();
        String nama = txtINama.getText().toString().trim();
        String alamat = txtIAlamat.getText().toString().trim();
        String tmpLahir = txtIETTmpLahir.getText().toString().trim();
        String tglLahir = txtIETTglLahir.getText().toString().trim();
        String imageName = txtVImageName.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            txtINama.setError("Silahkan masukkan nama");
            txtINama.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nrp)) {
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
        if (TextUtils.isEmpty(imageName) || txtVImageName.getText().equals("Upload foto terlebih dahulu!")) {
            txtVImageName.setText("Upload foto terlebih dahulu!");
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
                Toast.makeText(getActivity(), "ERROR! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("nrp", nrp);
        params.put("nama", nama);
        params.put("alamat", alamat);
        params.put("tmpLahir", tmpLahir);
        params.put("tglLahir", tglLahir);
        params.put("imageName", file.getName());

        HomeFragment.PerformNetworkRequest request = new HomeFragment.PerformNetworkRequest(ApiMahasiswa.URL_UPDATE, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUpdate.setText("Tambahkan Data");
        txtINrp.setText("");
        txtINama.setText("");
        txtIAlamat.setText("");
        txtIETTmpLahir.setText("");
        txtIETTglLahir.setText("");
        txtVImageName.setText("");

        isUpdating = false;
    }

    private void deleteMahasiswa(int id) {
        buttonAddUpdate.setText("Tambahkan Data");
        txtINrp.setText("");
        txtINama.setText("");
        txtIAlamat.setText("");
        txtIETTmpLahir.setText("");
        txtIETTglLahir.setText("");
        txtVImageName.setText("");

        isUpdating = false;
        HomeFragment.PerformNetworkRequest request = new HomeFragment.PerformNetworkRequest(ApiMahasiswa.URL_DELETE + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    //Retrofit Area

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (data == null){
                Toast.makeText(getActivity(), "Unable to choose image!", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri imageUri = data.getData();
            imagePath = getRealPathFromUri(imageUri);
            File file = new File(imagePath);
            txtVImageName.setText(file.getName());
        }
    }

    private String getRealPathFromUri(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    public static void setListViewHeightBasedOnChildren
            (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
