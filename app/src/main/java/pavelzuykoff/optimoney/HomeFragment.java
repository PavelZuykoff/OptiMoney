package pavelzuykoff.optimoney;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private DateConverter date = new DateConverter();
    private View view;
    private TextView currentDateTV;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(MainActivity.TAG, "onCreate: ");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d(MainActivity.TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        currentDateTV = (TextView) view.findViewById(R.id.current_date_tv);
        currentDateTV.setText(date.getCurrentDateStringFormat());

        return view;


    }


}
