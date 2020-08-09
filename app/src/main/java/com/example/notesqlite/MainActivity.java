package com.example.notesqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GhiChuAdapter.OnItemClickInterface, View.OnClickListener {

    SQLite database;
    private Dialog dialogAdd, dialogEdit;
    private RecyclerView rvnotes;
    private ArrayList<GhiChu> listNotes;
    private GhiChuAdapter adapterNotes;
    private EditText etcontent, eteditcontent;
    private Button btnadd, btncancel, btneditcontent, btneditcancel;
    private int noteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvnotes = (RecyclerView) findViewById(R.id.rvNotes);
        listNotes = new ArrayList<>();
        adapterNotes = new GhiChuAdapter(MainActivity.this, listNotes);
        rvnotes.setAdapter(adapterNotes);
        rvnotes.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapterNotes.SetOnItemClick(MainActivity.this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapterNotes, MainActivity.this));
        itemTouchHelper.attachToRecyclerView(rvnotes);

        //Tạo database
        database = new SQLite(MainActivity.this, "ghichu.sqlite", null, 1);
        //Tạo bảng ghi chú
        database.QueryData("CREATE TABLE IF NOT EXISTS GhiChu(Id INTEGER PRIMARY KEY AUTOINCREMENT, TenGhiChu VARCHAR(200))");

        //Them du lieu
        //database.QueryData("INSERT INTO GhiChu VALUES(null, 'Viết app')");

        //Lay du lieu
        UpdateUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAdd) {
            DialogAdd();
        }
        return super.onOptionsItemSelected(item);
    }
    //ITEM CLICK
    @Override
    public void Click(View v, int position) {
        GhiChu item = listNotes.get(position);
        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
        DialogEdit(item);
        noteId = item.getId();
    }
    //DIALOG ĐỂ EDIT GHI CHÚ
    private void DialogEdit(GhiChu item) {
        dialogEdit = new Dialog(MainActivity.this);
        dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEdit.setContentView(R.layout.dialog_edit_note);
        dialogEdit.setCanceledOnTouchOutside(false);

        eteditcontent = (EditText) dialogEdit.findViewById(R.id.etEditContent);
        btneditcontent = (Button) dialogEdit.findViewById(R.id.btnEdit);
        btneditcancel = (Button) dialogEdit.findViewById(R.id.btnEditCancel);

        eteditcontent.setText(item.getTitle());
        btneditcontent.setOnClickListener(this);
        btneditcancel.setOnClickListener(this);

        dialogEdit.show();
    }
    //DIALOG ĐỂ THÊM GHI CHÚ
    private void DialogAdd() {
        dialogAdd = new Dialog(MainActivity.this);
        dialogAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);//Bỏ phần tiêu đề mặc định khi custom dialog trên các version Android cũ
        dialogAdd.setContentView(R.layout.dialog_add_note);
        dialogAdd.setCanceledOnTouchOutside(false);

        etcontent = (EditText) dialogAdd.findViewById(R.id.etContent);
        btnadd = (Button) dialogAdd.findViewById(R.id.btnAdd);
        btncancel = (Button) dialogAdd.findViewById(R.id.btnCancel);

        btncancel.setOnClickListener(this);
        btnadd.setOnClickListener(this);
        dialogAdd.show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAdd:
                AddNote();
                break;
            case R.id.btnCancel:
                dialogAdd.dismiss();
                break;
            case R.id.btnEdit:
                EditNote(noteId);
                break;
            case R.id.btnEditCancel:
                dialogEdit.dismiss();
                break;
        }
    }
    //CẬP NHẬT LẠI MÀN HÌNH HIỂN THỊ GHI CHÚ
    private void UpdateUI() {
        Cursor data = database.GetData("SELECT * FROM GhiChu");
        listNotes.clear();
        while(data.moveToNext()){
            //String title = data.getString(1); //cột Id là thứ 0, cột tên ghi chú là thứ 1 trong mỗi item
            listNotes.add(new GhiChu(data.getInt(0), data.getString(1)));
            adapterNotes.notifyDataSetChanged();
            //Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
        }
    }
    //THÊM MỚI GHI CHÚ KHI NHẤN ADD
    private void AddNote() {
        String noteContent = etcontent.getText().toString().trim();
        if(noteContent.equals("")) {
            Toast.makeText(MainActivity.this, "Nội dung ghi chú không được để trống", Toast.LENGTH_SHORT).show();
        }else {
            database.QueryData("INSERT INTO GhiChu VALUES(null, '" + noteContent + "')");
            dialogAdd.dismiss();
            Toast.makeText(MainActivity.this, "Đã thêm ghi chú", Toast.LENGTH_SHORT).show();
            UpdateUI();
        }
    }
    //SỬA GHI CHÚ KHI NHÂN VÀO TỪNG ITEM GHI CHÚ
    private void EditNote(int noteId) {
        String newNoteContent = eteditcontent.getText().toString().trim();
        if(newNoteContent.equals("")) {
            Toast.makeText(MainActivity.this, "Nội dung ghi chú không được để trống", Toast.LENGTH_SHORT).show();
        }else {
            database.QueryData("UPDATE GhiChu SET TenGhiChu = '"+ newNoteContent +"' WHERE Id = '"+ noteId +"'");
            dialogEdit.dismiss();
            Toast.makeText(MainActivity.this, "Đã cập nhật ghi chú", Toast.LENGTH_SHORT).show();
            UpdateUI();
        }
    }
}
