package com.thefour.newsrecommender.app;



import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.CursorLoader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thefour.newsrecommender.app.R;
import com.thefour.newsrecommender.app.data.NewsContract;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryPickerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    CursorAdapter mAdapter;
    ListView mListView;
    Set<String> mInterestingCategory;
    Cursor mFullCategories;

    private static final String[] CATEGORY_COLUMNS = {NewsContract.CategoryEntry._ID, NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME} ;
    private static final int COLUMN_ID_INDEX = 0;
    private static final int COLUMN_CATEGORY_NAME_INDEX =1;
    public static final String CHANGE_CATEGORIES = "change_categories";

    public CategoryPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_category_picker,
                null,
                new String[]{NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME, NewsContract.CategoryEntry._ID},
                new int[]{R.id.textViewCategoryTitle,R.id.textViewCategoryIdPicker});
        mFullCategories = getContext().getContentResolver()
                .query(NewsContract.CategoryEntry.CONTENT_URI,CATEGORY_COLUMNS,null,null,null);
        if(mFullCategories.getCount()==0){
            //Todo get categories from server
            UpdateCategoriesTask updateListCategoriesTask = new UpdateCategoriesTask(getContext());
            updateListCategoriesTask.execute("http://10.0.2.2:8080/RankedListNews/categories");

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0,savedInstanceState,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category_picker, container, false);
        mInterestingCategory = new HashSet<String>();

        mListView = (ListView)rootView.findViewById(R.id.listViewCategoryPicker);
        mListView.setAdapter(mAdapter);




        //for debug purpose
//        SharedPreferences setting = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
//        Set<String> interestingCategory = new HashSet<String>();
//        interestingCategory = setting.getStringSet("interestingCategory",null);
//        if(interestingCategory!=null){
//            for(String category : interestingCategory){
//                Toast.makeText(getActivity(),category,Toast.LENGTH_SHORT).show();
//            }
//        }


        Button okButton = (Button)rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //save set of category user selected;
                for(int i = 0; i < mListView.getCount();i++)
                {
                    View view = mListView.getChildAt(i);
                    CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBoxCategoryPicker);
                    if(checkBox.isChecked()){
                        TextView textView = (TextView)view.findViewById(R.id.textViewCategoryIdPicker);
                        mInterestingCategory.add(textView.getText().toString());
                    }
                }
                SharedPreferences setting = getActivity().getSharedPreferences(MainActivity.PREFS_NAME,Context.MODE_PRIVATE);
                setting.edit().putStringSet("interestingCategory",mInterestingCategory).commit();
                //getActivity().setContentView(R.layout.fragment_view_pager);
                if(getActivity().getIntent().getBooleanExtra(CHANGE_CATEGORIES,false)){
                    getActivity().finish();
                }else{
                    getActivity().finish();
                }
            }
        });

        Button cancelButton = (Button)rootView.findViewById(R.id.laterButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user cancels changing interest catergories just finish
                if(getActivity().getIntent().getBooleanExtra(CHANGE_CATEGORIES,false)){
                    getActivity().finish();
                }else{// this is the fist time app run, select full categories as default.
                    for(int i = 0; i < mListView.getCount();i++)
                    {
                        View view = mListView.getChildAt(i);
                        TextView textView = (TextView)view.findViewById(R.id.textViewCategoryIdPicker);
                        mInterestingCategory.add(textView.getText().toString());
                    }
                    SharedPreferences setting = getActivity().getSharedPreferences(MainActivity.PREFS_NAME,Context.MODE_PRIVATE);
                    setting.edit().putStringSet("interestingCategory",mInterestingCategory).commit();
                    getActivity().recreate();
                }
            }
        });

        return rootView ;
    }

    private void initializeCheckbox(){
        SharedPreferences setting = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> interestingCategory = new HashSet<String>();
        interestingCategory = setting.getStringSet("interestingCategory",null);
        String[] interestingCategoryArray = new String[interestingCategory.size()];
        interestingCategoryArray= interestingCategory.toArray(interestingCategoryArray);
        if(interestingCategoryArray!=null){
            for(int i=0 ; i<interestingCategoryArray.length;++i){
                for(int j=0;j<mListView.getCount();++j)
                {
                    View childView = mListView.getChildAt(j);
                    TextView categoryIdTxtView =(TextView)childView.findViewById(R.id.textViewCategoryIdPicker);
                    if(categoryIdTxtView==null){
                        return;
                    }
                    if(interestingCategoryArray[i].equals(categoryIdTxtView.getText())){
                        ((CheckBox)childView.findViewById(R.id.checkBoxCategoryPicker)).setChecked(true);
                    }
                }
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME+" ASC";
        return new CursorLoader(getActivity(), NewsContract.CategoryEntry.CONTENT_URI,CATEGORY_COLUMNS,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        //initializeCheckbox();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
