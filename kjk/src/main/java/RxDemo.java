import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class RxDemo {

    public static void main(String[] args){
        test();
    }

    private static void log(Object o){
        System.out.println(o);
    }

    private static void test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
                e.onComplete();
                e.onNext(6);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "this is the result:"+integer;
            }
        })
         .subscribe(new Observer<String>() {
            private int i;
            private Disposable mDisposable;
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(String integer) {
                log(integer);
                i++;
                if(i==4){
                    //mDisposable.dispose();
                    log("mDisposable.dispose()");
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                log("onComplete");

            }
        });
    }
}
