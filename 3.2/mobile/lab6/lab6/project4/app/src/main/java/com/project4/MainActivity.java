package com.project4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private Spinner spinnerManufacturers;
    private Spinner spinnerSortField;
    private Spinner spinnerSortOrder;
    private EditText editTextMinPrice;
    private Button buttonFilter;
    private Button buttonReset;
    private TextView textViewStats;
    private List<Feed> feedList;
    private List<Manufacturer> manufacturerList;
    private int selectedManufacturerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recycler_view_feeds);
        spinnerManufacturers = findViewById(R.id.spinner_manufacturers);
        spinnerSortField = findViewById(R.id.spinner_sort_field);
        spinnerSortOrder = findViewById(R.id.spinner_sort_order);
        editTextMinPrice = findViewById(R.id.edit_text_min_price);
        buttonFilter = findViewById(R.id.button_filter);
        buttonReset = findViewById(R.id.button_reset);
        textViewStats = findViewById(R.id.text_view_stats);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadManufacturers();
        loadFeeds();

        setupListeners();
    }

    private void loadManufacturers() {
        manufacturerList = dbHelper.getAllManufacturers();

        manufacturerList.add(0, new Manufacturer(-1, "Все производители"));

        ManufacturerAdapter manufacturerAdapter = new ManufacturerAdapter(
                this, R.layout.spinner_item, manufacturerList);
        spinnerManufacturers.setAdapter(manufacturerAdapter);
    }

    private void loadFeeds() {
        feedList = dbHelper.getAllFeeds();

        feedAdapter = new FeedAdapter(feedList, this);
        recyclerView.setAdapter(feedAdapter);
    }

    private void setupListeners() {
        spinnerManufacturers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Manufacturer selectedManufacturer = (Manufacturer) parent.getItemAtPosition(position);
                selectedManufacturerId = selectedManufacturer.getId();

                if (selectedManufacturerId == -1) {
                    loadFeeds();
                    updateStatsForAllFeeds();
                } else {
                    feedList = dbHelper.getFeedsGroupedByManufacturer(selectedManufacturerId);
                    feedAdapter.updateData(feedList);
                    updateStatsForManufacturer(selectedManufacturerId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedManufacturerId = -1;
                loadFeeds();
                updateStatsForAllFeeds();
            }
        });

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minPriceStr = editTextMinPrice.getText().toString();
                if (!minPriceStr.isEmpty()) {
                    try {
                        double minPrice = Double.parseDouble(minPriceStr);
                        feedList = dbHelper.getFeedsAbovePrice(minPrice);
                        feedAdapter.updateData(feedList);
                        Toast.makeText(MainActivity.this,
                                "Показаны корма с ценой выше " + minPrice + " ₽",
                                Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this,
                                "Введите корректную цену",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Введите минимальную цену для фильтрации",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextMinPrice.setText("");
                spinnerManufacturers.setSelection(0);
                spinnerSortField.setSelection(0);
                spinnerSortOrder.setSelection(0);
                loadFeeds();
                updateStatsForAllFeeds();
            }
        });

        spinnerSortField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void applySorting() {
        String sortField;
        boolean ascending;

        switch (spinnerSortField.getSelectedItemPosition()) {
            case 0:
                sortField = "id";
                break;
            case 1:
                sortField = "name";
                break;
            case 2:
                sortField = "price";
                break;
            case 3:
                sortField = "quantity";
                break;
            default:
                sortField = "id";
        }

        ascending = spinnerSortOrder.getSelectedItemPosition() == 0;

        feedList = dbHelper.getSortedFeeds(sortField, ascending);
        feedAdapter.updateData(feedList);
    }

    private void updateStatsForAllFeeds() {
        int totalCount = dbHelper.getTotalFeedsCount();
        double avgPrice = calculateAveragePrice(feedList);

        String statsText = "Всего наименований: " + totalCount + "\n" +
                "Средняя стоимость: " + String.format("%.2f", avgPrice) + " ₽";

        textViewStats.setText(statsText);
    }

    private void updateStatsForManufacturer(int manufacturerId) {
        double maxPrice = dbHelper.getMaxPriceByManufacturer(manufacturerId);
        double minPrice = dbHelper.getMinPriceByManufacturer(manufacturerId);
        double avgPrice = dbHelper.getAvgPriceByManufacturer(manufacturerId);
        int totalQuantity = dbHelper.getTotalQuantityByManufacturer(manufacturerId);

        String statsText = "Максимальная цена: " + String.format("%.2f", maxPrice) + " ₽\n" +
                "Минимальная цена: " + String.format("%.2f", minPrice) + " ₽\n" +
                "Средняя цена: " + String.format("%.2f", avgPrice) + " ₽\n" +
                "Всего единиц: " + totalQuantity;

        textViewStats.setText(statsText);
    }

    private double calculateAveragePrice(List<Feed> feeds) {
        if (feeds == null || feeds.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (Feed feed : feeds) {
            sum += feed.getPrice();
        }

        return sum / feeds.size();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loadFeeds();
            updateStatsForAllFeeds();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}