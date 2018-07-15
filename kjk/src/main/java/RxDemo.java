import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import sun.rmi.runtime.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RxDemo {

    public static void main(String[] args) {
        //test();
        //zip();
        //concat1();
        //flatmap();
        //concatmap();

       // distinct();

        filter();


    }

    private static void filter() {
        Observable.fromArray(0,-1,4,9,-90,90,-7)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer>0;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                log(integer);
            }
        });
    }

    //去重
    private static void distinct() {
        Observable.just(1, 1, 1, 2, 2, 3, 4, 5)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                       log(integer);
                    }
                });
    }


    private static void concatmap() {
        Observable<String> stringObservable = Observable.fromArray("aaa", "bbb", "ccc", "ddd");
        Observable<Integer> integerObservable = Observable.range(0, 3);

        integerObservable.concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                log("flatMap:"+integer);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        log(o);
                    }
                });

    }

    //把一个发射器Observable 通过某种方法转换为多个Observables，然后再把这些分散的Observables装进一个单一的发射器Observable。
    // 但有个需要注意的是，flatMap并不能保证事件的顺序，如果需要保证，需要用到我们下面要讲的ConcatMap
    //一个变多个,然后这多个依次发出
    private static void flatmap() {
        Observable<String> stringObservable = Observable.fromArray("aaa", "bbb", "ccc", "ddd");
        Observable<Integer> integerObservable = Observable.range(0, 3);

        integerObservable.flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                log("flatMap:"+integer);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(String o) throws Exception {
                log(o);
            }
        });

    }

    //组合成了一个新的发射器，非常懂事的孩子，有条不紊的排序接收
    private static void concat1() {
        Observable<String> stringObservable = Observable.fromArray("aaa", "bbb", "ccc", "ddd");
        Observable<Integer> integerObservable = Observable.range(0, 8);

        Observable.concat(stringObservable, integerObservable)
                .subscribe(new Consumer<Serializable>() {
                    @Override
                    public void accept(Serializable serializable) throws Exception {
                        log(serializable);
                    }
                });
    }

    /**
     * 1) zip 组合事件的过程就是分别从发射器A和发射器B各取出一个事件来组合，并且一个事件只能被使用一次，组合的顺序是严格按照事件发送的顺序来进行的，所以上面截图中，可以看到，1永远是和A 结合的，2永远是和B结合的。
     * <p>
     * 2) 最终接收器收到的事件数量是和发送器发送事件最少的那个发送器的发送事件数目相同，所以如截图中，5很孤单，没有人愿意和它交往，孤独终老的单身狗。
     */
    private static void zip() {
        Observable<String> stringObservable = Observable.fromArray("aaa", "bbb", "ccc", "ddd");
        Observable<Integer> integerObservable = Observable.range(0, 8);

        Observable.zip(stringObservable, integerObservable, new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                log(s);
            }
        });


    }

    private static void log(Object o) {
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
                return "this is the result:" + integer;
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
                        if (i == 4) {
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
