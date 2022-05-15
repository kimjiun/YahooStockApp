package com.kimjiun.yahoostockapp;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Observable;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kimjiun.yahoostockapp.databinding.ActivityMainBinding;
import com.kimjiun.yahoostockapp.storio.StockUpdateTable;
import com.kimjiun.yahoostockapp.storio.StorIOFactory;
import com.kimjiun.yahoostockapp.yahoo.RetrofitYahooServiceFactory;
import com.kimjiun.yahoostockapp.yahoo.YahooService;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends RxAppCompatActivity {
    private LinearLayoutManager layoutManager;
    private StockDataAdapter stockDataAdapter;
    // CHANGE HERE
    private final String SYMBOLS = "TSLA,AAPL,GOOG,MSFT";

    BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        RxJavaPlugins.setErrorHandler(ErrorHandler.get());

        setView();
        setStocks();
    }

    private void setView(){
        activityMainBinding.stockUpdatesRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        activityMainBinding.stockUpdatesRecyclerView.setLayoutManager(layoutManager);

        stockDataAdapter = new StockDataAdapter();
        activityMainBinding.stockUpdatesRecyclerView.setAdapter(stockDataAdapter);
    }

    private void setStocks(){
        YahooService yahooService = new RetrofitYahooServiceFactory().create();

        Observable.interval(0, 10, TimeUnit.SECONDS)
                .compose(bindToLifecycle())
                .flatMap(
                        i -> yahooService.yqlQuery("US", "en", SYMBOLS)
                                .toObservable()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> {
                    MyLog.log("doOnError", "error");
                    Toast.makeText(this, "We couldn't reach internet - falling back to local data",
                            Toast.LENGTH_SHORT)
                            .show();
                })
                .observeOn(Schedulers.io())
                .map(r -> r.getQuoteResponse().getResult())
                .flatMap(Observable::fromIterable)
                .map(StockUpdate::create)
                .doOnNext(this::saveStockUpdate)
                .onErrorResumeNext(
                        v2(StorIOFactory.get(this)
                                .get()
                                .listOfObjects(StockUpdate.class)
                                .withQuery(Query.builder()
                                        .table(StockUpdateTable.TABLE)
                                        .orderBy("date DESC")
                                        .limit(50)
                                        .build())
                                .prepare()
                                .asRxObservable()
                        )
                                .take(1)
                                .flatMap(Observable::fromIterable)
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stockUpdate -> {
                    Log.d("APP", "New update " + stockUpdate.getStockSymbol());
                    activityMainBinding.noDataAvailable.setVisibility(View.GONE);
                    stockDataAdapter.add(stockUpdate);
                }, error ->{
                    Log.d("APP", "No Item " + stockDataAdapter.getItemCount());
                    if(stockDataAdapter.getItemCount() == 0){
                        activityMainBinding.noDataAvailable.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void saveStockUpdate(StockUpdate stockUpdate){
        MyLog.log("saveStockUpdate", stockUpdate.getStockSymbol());
        StorIOFactory.get(this)
                .put()
                .object(stockUpdate)
                .prepare()
                .asRxSingle()
                .subscribe();
    }

    private void deleteStockUpdate(StockUpdate stockUpdate){
        MyLog.log("deleteStockUpdate", stockUpdate.getStockSymbol());
        StorIOFactory.get(this)
                .delete()
                .object(stockUpdate)
                .prepare()
                .asRxSingle()
                .subscribe();
    }

    public static <T> Observable<T> v2(rx.Observable<T> source) {
        return toV2Observable(source);
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }
}