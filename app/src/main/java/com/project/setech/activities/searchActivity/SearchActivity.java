package com.project.setech.activities.searchActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.setech.R;
import com.project.setech.activities.detailsActivity.DetailsActivity;
import com.project.setech.activities.listActivity.listRecyclerView.RecyclerItemClickListener;
import com.project.setech.activities.mainActivity.MainActivity;
import com.project.setech.activities.searchActivity.searchRecyclerView.SearchViewAdapter;
import com.project.setech.model.IItem;
import com.project.setech.model.ItemFactory;
import com.project.setech.model.NewItemFactory;
import com.project.setech.repository.IRepository;
import com.project.setech.repository.Repository;
import com.project.setech.util.CategoryType;
import com.project.setech.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private IRepository repository;

    private RecyclerView recyclerView;
    private SearchViewAdapter searchViewAdapter;

    private Button clicked;
    private Button orderClicked;
    private String order;
    private String clickedString;

    private String queryString;

    private Button sortByOpenButton;

    private Button priceSortButton;
    private Button nameSortButton;
    private Button viewsSortButton;

    private Button increasingSortButton;
    private Button decreasingSortButton;

    private LinearLayout sortByExpandedLayout;

    private ProgressBar listRecyclerProgressBar;

    private List<IItem> itemsList;
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //actionbar
        ActionBar actionBar = getSupportActionBar();

        //set back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        itemsList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        int columns = 2;

        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        sortByOpenButton = findViewById(R.id.sortByOpenButton);

        priceSortButton = findViewById(R.id.priceButton);
        nameSortButton = findViewById(R.id.nameButton);
        viewsSortButton = findViewById(R.id.viewsButton);

        increasingSortButton = findViewById(R.id.increasingButton);
        decreasingSortButton = findViewById(R.id.decreasingButton);

        sortByExpandedLayout = findViewById(R.id.sortByExpandedLayout);
        sortByExpandedLayout.setVisibility(View.GONE);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(SearchActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d("test", "onItemClick: "+itemsList.get(position).getId());

                        Intent newIntent = new Intent(SearchActivity.this, DetailsActivity.class);
                        newIntent.putExtra("ItemId", itemsList.get(position).getId());
                        newIntent.putExtra("SearchBoolean", true);
                        newIntent.putExtra("QueryString", queryString);
                        startActivity(newIntent);
                        finish();
                    }
                })
        );

        sortByOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortByExpandedLayout.getVisibility() == View.GONE) {
                    sortByExpandedLayout.setVisibility(View.VISIBLE);
                    sortByOpenButton.setCompoundDrawablesWithIntrinsicBounds(null,null, AppCompatResources.getDrawable(SearchActivity.this,R.drawable.arrow_up),null);
                    slideDownAnim(sortByExpandedLayout);
                }
                else {
                    sortByExpandedLayout.setVisibility(View.GONE);
                    sortByOpenButton.setCompoundDrawablesWithIntrinsicBounds(null,null,AppCompatResources.getDrawable(SearchActivity.this,R.drawable.arrow_down),null);
                }
            }
        });

        priceSortButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                clickedString = "price";
                selectSortButton(priceSortButton, false);
            }
        });

        nameSortButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                clickedString = "name";
                selectSortButton(nameSortButton, false);
            }
        });

        viewsSortButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                clickedString = "view";
                selectSortButton(viewsSortButton, false);
            }
        });

        increasingSortButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                order = "increase";
                selectOrderSortButton(increasingSortButton, false);
            }
        });

        decreasingSortButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                order = "decrease";
                selectOrderSortButton(decreasingSortButton, false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectSortButton(Button sortBtn, boolean first) {
        priceSortButton.setBackground(AppCompatResources.getDrawable(this,R.drawable.sort_button_border_not_highlighted));
        nameSortButton.setBackground(AppCompatResources.getDrawable(this,R.drawable.sort_button_border_not_highlighted));
        viewsSortButton.setBackground(AppCompatResources.getDrawable(this,R.drawable.sort_button_border_not_highlighted));

        priceSortButton.setTextColor(AppCompatResources.getColorStateList(this,R.color.grey));
        nameSortButton.setTextColor(AppCompatResources.getColorStateList(this,R.color.grey));
        viewsSortButton.setTextColor(AppCompatResources.getColorStateList(this,R.color.grey));

        sortBtn.setBackground(AppCompatResources.getDrawable(this,R.drawable.sort_button_border_highlighted));
        sortBtn.setTextColor(AppCompatResources.getColorStateList(this,R.color.black));

        clicked = sortBtn;

        if(!first) {
            if (orderClicked == increasingSortButton) {
                order = "increase";
            } else if (orderClicked == decreasingSortButton) {
                order = "decrease";
            }

            if (clicked == nameSortButton) {
                searchViewAdapter.sortName(order);
            } else if (clicked == priceSortButton) {
                searchViewAdapter.sortPrice(order);
            } else if (clicked == viewsSortButton) {
                searchViewAdapter.sortView(order);
            }
        } else {
            order = "increase";
            clicked = nameSortButton;
            clickedString = "name";
            searchViewAdapter.sortName(order);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectOrderSortButton(Button sortBtn, boolean first) {
        increasingSortButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.sort_button_border_not_highlighted));
        decreasingSortButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.sort_button_border_not_highlighted));

        increasingSortButton.setTextColor(AppCompatResources.getColorStateList(this, R.color.grey));
        decreasingSortButton.setTextColor(AppCompatResources.getColorStateList(this, R.color.grey));

        sortBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.sort_button_border_highlighted));
        sortBtn.setTextColor(AppCompatResources.getColorStateList(this, R.color.black));

        orderClicked = sortBtn;

        if(!first) {

            if (clicked == nameSortButton) {
                searchViewAdapter.sortName(order);
            } else if (clicked == priceSortButton) {
                searchViewAdapter.sortPrice(order);
            } else if (clicked == viewsSortButton) {
                searchViewAdapter.sortView(order);
            }
        } else {
            order = "increase";
            clicked = nameSortButton;
            clickedString = "name";
            searchViewAdapter.sortName(order);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();

        if (!itemsList.isEmpty()) {
            return;
        }

        searchString = (String) getIntent().getSerializableExtra("SearchString");
        getSupportActionBar().setTitle("Search Results For: " + searchString);

        queryString = searchString;

        repository = new Repository(SearchActivity.this,new NewItemFactory());

        repository.fetchItems(items -> {
            // Create recycler view
            itemsList = items;

            searchViewAdapter = new SearchViewAdapter(SearchActivity.this, itemsList, CategoryType.ALL);

            recyclerView.setAdapter(searchViewAdapter);
            searchViewAdapter.getFilter().filter(searchString);
            searchViewAdapter.notifyDataSetChanged();

            listRecyclerProgressBar = findViewById(R.id.listRecyclerProgressBar);
            listRecyclerProgressBar.setVisibility(View.GONE);

            selectSortButton(nameSortButton, true);
            selectOrderSortButton(increasingSortButton, true);
        });
    }

    private void slideDownAnim(View view) {
        Animation slideDown = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.slide_down);
        view.startAnimation(slideDown);
    }

    private void slideUpAnim(View view) {
        Animation slideDown = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.slide_up);
        view.startAnimation(slideDown);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sortByExpandedLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void goToSearchActivity(String searchString) {
        Intent newIntent = new Intent(SearchActivity.this, SearchActivity.class);
        newIntent.putExtra("SearchString", searchString);
        startActivity(newIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryString = query;
                goToSearchActivity(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }
}