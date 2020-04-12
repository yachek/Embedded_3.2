package com.example.lab_32;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private Switch deadlineSwitch;
    private Spinner spinner_st;
    private Spinner spinner_deadline;
    private Button calc;
    private TextView res;

    private boolean deadlineForm;

    private Double[] teachSpeed = {0.001, 0.01, 0.05, 0.1, 0.2, 0.3};
    private Double[] timeDeadline = {0.5, 1.0, 2.0, 5.0};
    private Double[] iterationDeadline = {100.0, 200.0, 500.0, 1000.0};

    private Double[][] points = {{0.0, 6.0}, {1.0, 5.0}, {3.0, 3.0}, {2.0, 4.0}};

    private Double P = 4.0;

    private Double deadline;
    private Double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deadlineSwitch = findViewById(R.id.switch_deadline);
        spinner_st = findViewById(R.id.spinner_st);
        spinner_deadline = findViewById(R.id.spinner_d);
        calc = findViewById(R.id.calculate_button);
        res = findViewById(R.id.result_text);

        deadlineSwitch.setOnCheckedChangeListener(this);

        ArrayAdapter<Double> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, teachSpeed);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_st.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeDeadline);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner_deadline.setAdapter(adapter);

        spinner_deadline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (deadlineForm) {
                    deadline = iterationDeadline[position];
                } else {
                    deadline = timeDeadline[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast toast = Toast.makeText(getApplicationContext(), "Нічого не вибрано!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        spinner_st.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                speed = teachSpeed[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast toast = Toast.makeText(getApplicationContext(), "Нічого не вибрано!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                res.setText(main());
            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            ArrayAdapter<Double> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, iterationDeadline);
            // Определяем разметку для использования при выборе элемента
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            spinner_deadline.setAdapter(adapter);
        } else {
            ArrayAdapter<Double> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, timeDeadline);
            // Определяем разметку для использования при выборе элемента
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            spinner_deadline.setAdapter(adapter);
        }
        deadlineForm = b;
    }

    private String main() {
        double W1 = 0;
        double W2 = 0;
        Double[] point;
        int j;
        double y;
        double delta;
        int i = 0;
        long start;
        long end = 0;
        if (deadlineForm) {
            start = System.nanoTime();
            for (; i < deadline * 2; i = i + 2) {
                j = i % 4;
                point = points[j];
                y = point[0] * W1 + point[1] * W2;
                if (arrayOfPoints(W1, W2)) {
                    break;
                }
                delta = P - y;
                W1 += delta * point[0] * speed;
                W2 += delta * point[1] * speed;
            }
            if (i >= deadline * 2) {
                end = System.nanoTime();
                return "Дедлайн у " + deadline + " ітерацій досягнут, обрив циклу! W1 = " +
                        W1 + ", W2 = " + W1 + ", час - " + (end - start) + " наносекунд.";
            }
            end = System.nanoTime();
        } else {
            start = System.nanoTime();
            while (true) {
                j = i % 4;
                point = points[j];
                y = point[0] * W1 + point[1] * W2;
                delta = P - y;
                if (arrayOfPoints(W1, W2)) {
                    break;
                }
                W1 += delta * point[0] * speed;
                W2 += delta * point[1] * speed;
                i = i + 2;
                end = System.nanoTime();
                if ((end - start) >= deadline * 1000000000) {
                    return "Дедлайн у " + deadline + " секунд досягнут, обрив циклу! W1 = " +
                            W1 + ", W2 = " + W2 + ", кількість ітерацій - " + (i / 2 + 1) +
                            ", час - " + (end - start) + " наносекунд";
                }
            }
        }
        return "Рішення знайдено! W1 = " + W1 + ", W2 = " + W2 + ", час знаходження - " +
                (end - start) + " наносекунд, кількість ітерацій - " + (i / 2 + 1);
    }

    private boolean arrayOfPoints(double W1, double W2) {
        double y1;
        for (int k = 0; k < points.length / 2; k++) {
            y1 = points[k][0] * W1 + points[k][1] * W2;
            if (y1 < P) {
                return false;
            }
        }
        for (int k = points.length / 2; k < points.length; k++) {
            y1 = points[k][0] * W1 + points[k][1] * W2;
            if (y1 > P) {
                return false;
            }
        }
        return true;
    }

}

