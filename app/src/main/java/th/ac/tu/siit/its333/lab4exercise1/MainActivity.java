package th.ac.tu.siit.its333.lab4exercise1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    CourseDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvgp = (TextView)findViewById(R.id.tvGP);
        TextView tvcr = (TextView)findViewById(R.id.tvCR);
        TextView tvgpa = (TextView)findViewById(R.id.tvGPA);

        helper = new CourseDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(credit),SUM(credit*value) FROM course;", null);
        cursor.moveToFirst();
        double tempgp = cursor.getDouble(1);
        int credit = cursor.getInt(0);

        if(credit > 0) {
            double gpa = tempgp/credit;
            tvgp.setText(Double.toString(tempgp));
            tvcr.setText(Integer.toString(credit));
            tvgpa.setText(String.format("%.2f",gpa));
        }
        else {
            tvgp.setText("0.0");
            tvcr.setText("0");
            tvgpa.setText("0.0");
        }

        // This method is called when this activity is put foreground.

    }

    public void buttonClicked(View v) {
        int id = v.getId();
        Intent i;

        switch(id) {
            case R.id.btAdd:
                i = new Intent(this, AddCourseActivity.class);
                startActivityForResult(i, 88);
                break;

            case R.id.btShow:
                i = new Intent(this, ListCourseActivity.class);
                startActivity(i);
                break;

            case R.id.btReset:
                helper = new CourseDBHelper(this);
                SQLiteDatabase db = helper.getReadableDatabase();
                db.delete("course","",null);
                onResume();
                Toast t = Toast.makeText(this.getApplicationContext(), "All courses are cleared", Toast.LENGTH_SHORT);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 88) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("code");
                int credit = data.getIntExtra("credit", 0);
                String grade = data.getStringExtra("grade");

                helper = new CourseDBHelper(this);
                SQLiteDatabase db = helper.getReadableDatabase();
                ContentValues r = new ContentValues();
                r.put("code", code);
                r.put("credit",credit);
                r.put("grade",grade);
                r.put("value",gradeToValue(grade));
                long new_id = db.insert("course",null,r);

                if (new_id == -1) {
                    Toast t = Toast.makeText(this.getApplicationContext(), "Add course failed"
                            , Toast.LENGTH_SHORT);
                    t.show();
                }
                else {
                    Toast t = Toast.makeText(this.getApplicationContext(), "Add course succeeded"
                            , Toast.LENGTH_SHORT);
                    t.show();
                }
                //db.close();
            }
        }

        Log.d("course", "onActivityResult");
    }


    double gradeToValue(String g) {
        if (g.equals("A"))
            return 4.0;
        else if (g.equals("B+"))
            return 3.5;
        else if (g.equals("B"))
            return 3.0;
        else if (g.equals("C+"))
            return 2.5;
        else if (g.equals("C"))
            return 2.0;
        else if (g.equals("D+"))
            return 1.5;
        else if (g.equals("D"))
            return 1.0;
        else
            return 0.0;
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
}
