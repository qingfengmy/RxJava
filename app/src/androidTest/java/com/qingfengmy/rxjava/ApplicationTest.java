package com.qingfengmy.rxjava;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private final String TAG = "RXJAVA";

    public ApplicationTest() {
        super(Application.class);
    }

    /**
     * 1. 基本使用
     */
    @Test
    public void test1() {
        // 观察者(订阅者)
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext " + s);
            }
        };

        // 被观察者
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello");
                subscriber.onNext("rx");
                subscriber.onNext("java");
            }
        });

        // 注册
        observable.subscribe(observer);

    }

    /**
     * 2. Subscriber替换observer，增加两个方法
     */
    @Test
    public void test2() {
        // 观察者(订阅者)
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext " + s);
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.e(TAG, "onStart ");
            }

            @Override
            public void setProducer(Producer p) {
                super.setProducer(p);
                Log.e(TAG, "setProducer ");
            }
        };

        // 被观察者
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello");
                subscriber.onNext("rx");
                subscriber.onNext("java");
            }
        });

        // 注册
        observable.subscribe(subscriber);

    }

    /**
     * 3. Observable的just方法，和create方法一样，创建事件的方法之一
     * 事件是被观察者的事件
     */
    @Test
    public void test3() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext " + s);
            }
        };
        Observable<String> observable = Observable.just("aaa", "bbb", "ccc");
        observable.subscribe(subscriber);
    }

    /**
     * 4. Observable的from方法，和create/just方法一样，创建事件的方法之一
     * 事件是被观察者的事件
     */
    @Test
    public void test4() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext " + s);
            }
        };
        String[] arr = {"aaa", "bbb", "ccc"};
        Observable<String> observable = Observable.from(arr);
        observable.subscribe(subscriber);
    }

    /**
     * 5. Subscriber还支持不完整定义的回调，也就是仅实现了onCompleted, onError，onNext三方法之一的订阅者。
     * Action0--call方法|回调方法不带参数;
     * Action1--call方法|回调方法带参数;
     */
    @Test
    public void test5() {
        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }

        };

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable e) {
                Log.e(TAG, e.getMessage());
            }
        };

        Action0 onComplate = new Action0() {
            @Override
            public void call() {
                Log.e(TAG, "onComplate");
            }
        };

        String[] arr = {"aaa", "bbb", "ccc"};
        Observable<String> observable = Observable.from(arr);
        // 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
        // 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
        observable.subscribe(onNextAction, onErrorAction);
        // 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onComplate);

    }

    /**
     * 打印字符串数组
     */
    @Test
    public void test6() {
        String[] names = {"aaa", "bbb", "ccc"};
        Observable.from(names).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "名字是：" + s);
            }
        });
    }

    /**
     * 由 id 取得图片并显示
     */
    @Test
    public void test7() {
        // 见mainActivity

    }

    /**
     * 线程问题
     * <p/>
     * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
     * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
     * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
     * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
     * 有了这几个 Scheduler ，就可以使用 subscribeOn() 和 observeOn() 两个方法来对线程进行控制了。
     * <p/>
     * subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
     * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
     */
    @Test
    public void test8() {
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.print(integer);
                    }
                });
    }

    /**
     * 变换之map: 所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列。
     * 这里出现了一个叫做 Func1 的类。它和 Action1 非常相似，也是 RxJava 的一个接口，用于包装含有一个参数的方法。
     * Func1 和 Action 的区别在于， Func1 包装的是有返回值的方法。
     * 另外，和 ActionX 一样， FuncX 也有多个，用于不同参数个数的方法。
     * FuncX 和 ActionX 的区别在 FuncX 包装的是有返回值的方法。
     * ------------------
     * FuncX和ActionX一样都是观察者，消费事件的地方
     */
    @Test
    public void test9() {
        Observable.just("1234")
                .map(new Func1<String, Integer>() {// 事件流中的String类型变换成Integer类型，继续向下传递。
                    @Override
                    public Integer call(String s) {
                        return Integer.parseInt(s);
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "integer=" + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "integer=" + integer);
            }
        });
    }

    /**
     * flatMap1:根据List<Student>打印student的name
     */
    @Test
    public void test10() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("allen"));
        students.add(new Student("shark"));
        students.add(new Student("iversion"));
        students.add(new Student("tracy"));

        // 这里Action的泛型只能是Student，因为Observable的from方法是Student的集合。
        // 事件源产生的事件流的类型是Student，事件消费者这里只能接受到Student。如果想获取String类型的name，需要转换类型
//        Observable.from(students).subscribe(new Action1<Student>() {
//            @Override
//            public void call(Student student) {
//
//            }
//        });
        Observable.from(students).map(new Func1<Student, String>() {
            @Override
            public String call(Student student) {
                return student.getName();
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String name) {
                Log.e(TAG, name);
            }
        });

    }

    /**
     * flatMap2:根据List<Student>打印student的所选课程
     * flatMap() 和 map() 有一个相同点：它也是把传入的参数转化之后返回另一个对象。
     * 但需要注意，和 map() 不同的是， flatMap() 中返回的是个 Observable 对象，
     * 并且这个 Observable 对象并不是被直接发送到了 Subscriber 的回调方法中。
     * flatMap() 的原理是这样的：
     * 1. 使用传入的事件对象创建一个 Observable 对象；
     * 2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
     * 3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，
     * 而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。
     * 这三个步骤，把事件拆成了两级，通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。
     * 而这个『铺平』就是 flatMap() 所谓的 flat。
     */
    @Test
    public void test11() {
        List<Student> students = new ArrayList<>();
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("计算机基础"));
        students.add(new Student("allen", courses));
        courses.add(new Course("android"));
        students.add(new Student("shark", courses));
        courses.add(new Course("生活"));
        students.add(new Student("iversion", courses));
        courses.add(new Course("篮球"));
        students.add(new Student("tracy", courses));
        Observable.from(students).map(new Func1<Student, List<Course>>() {
            @Override
            public List<Course> call(Student student) {
                return student.getCourses();
            }
        }).subscribe(new Action1<List<Course>>() {
            // 这里使用Action1无效，因为Action1只支持实现onNext，onError，也就是参数泛型只支持String和Throwable。
            @Override
            public void call(List<Course> courses) {
                for (Course c : courses)
                    Log.e(TAG, c.getName());
                Log.e(TAG, "------------------");
            }
        });
    }

    /**
     * flatMap3:根据List<Student>打印student的所选课程
     */
    @Test
    public void test12() {
        List<Student> students = new ArrayList<>();
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("计算机基础"));
        students.add(new Student("allen", courses));
        courses.add(new Course("android"));
        students.add(new Student("shark", courses));
        courses.add(new Course("生活"));
        students.add(new Student("iversion", courses));
        courses.add(new Course("篮球"));
        students.add(new Student("tracy", courses));

        Observable.from(students).flatMap(new Func1<Student, Observable<Course>>() {
            @Override
            public Observable<Course> call(Student student) {
                return Observable.from(student.getCourses());
            }
        }).subscribe(new Subscriber<Course>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Course course) {
                Log.e(TAG, course.getName());
            }

        });
    }

    /**
     * lift变换：代理
     * lift() 是针对事件项和事件序列(Onsubscrib)的
     */
    @Test
    public void test13() {
        Observable.just(12).lift(new Observable.Operator<String, Integer>() {
            @Override
            public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                // 代理内容

                return new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        // 旧的观察者
                        subscriber.onCompleted();
                        Log.e("aaa", "新的subscriber:onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                        Log.e("aaa", "新的subscriber:onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        // 代理前
                        String s = integer + "";
                        Log.e("aaa", "新的Subscriber：onNext代理前");
                        subscriber.onNext(s);
                        // 代理后
                        Log.e("aaa", "新的Subscriber：onNext代理后");

                    }
                };
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

                Log.e("aaa", "旧的subscriber:onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("aaa", "旧的subscriber:onError");
            }

            @Override
            public void onNext(String s) {
                Log.e("aaa", "旧的subscriber:onNext---" + s);
            }
        });


    }

    /**
     * compose:对 Observable 整体的变换
     */
    @Test
    public void test14() {
        // 非代码，代码说明
       /* public class LiftAllTransformer implements Observable.Transformer<Integer, String> {
            @Override
            public Observable<String> call(Observable<Integer> observable) {
                return observable
                        .lift1()
                        .lift2()
                        .lift3()
                        .lift4();
            }
        }
        ...
        Transformer liftAll = new LiftAllTransformer();
        observable1.compose(liftAll).subscribe(subscriber1);
        observable2.compose(liftAll).subscribe(subscriber2);
        observable3.compose(liftAll).subscribe(subscriber3);
        observable4.compose(liftAll).subscribe(subscriber4);*/
    }

    /**
     * 多线程切换
     * subscribOn() 指的是事件发生的线程，Observable生成的线程，事件产生只有一个，所以只能定义一个。
     * observeOn() 指定的是它之后的操作（事件消费）所在的线程。因此如果有多次切换线程的需求，只要在每个想要切换线程的位置调用一次 observeOn() 即可。
     * SubscribeOn和observeOn其内部实现原理也是代理，也就是list。
     */
    @Test
    public void test15() {
       /* Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(mapOperator) // 新线程，由 observeOn() 指定
                .observeOn(Schedulers.io())
                .map(mapOperator2) // IO 线程，由 observeOn() 指定
                .observeOn(AndroidSchedulers.mainThread)
                .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定*/
    }

    /**
     * doOnSubscribe: Observable.doOnSubscribe(),
     * 它和 Subscriber.onStart() 同样是在 subscribe() 调用后而且在事件发送前执行，但区别在于它可以指定线程。
     * 默认情况下， doOnSubscribe() 执行在 subscribe() 发生的线程；.
     * 而如果在 doOnSubscribe() 之后有 subscribeOn() 的话，它将执行在离它最近的 subscribeOn() 所指定的线程。
     * <p/>
     * 所解决的问题，如：subscriber中的onstart方法，onstart方法是属于subscripber的，所以其线程和他一致，
     * 可是如果在时间发送前的事件需要切换线程，比较麻烦。
     */
    @Test
    public void test16() {
       /* Observable.create(onSubscribe)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressBar.setVisibility(View.VISIBLE); // 需要在主线程执行
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread()) // 指定主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);*/
    }

    /**
     * 与retrofit结合使用案例1：请求完数据后要进行数据库更新整理再显示
     */
    @Test
    public void test17() {
        /*getUser(userId)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        processUser(user);
                    }).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<User>() {
                        @Override
                        public void onNext(User user) {
                            userView.setUser(user);
                        }

                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable error) {
                            // Error handling
                            ...
                        }
                    });*/
    }

    /**
     * 与retrofit结合案例2：假设 /user 接口并不能直接访问，而需要填入一个在线获取的 token，也就是多次网络请求
     */
    @Test
    public void test18() {
        /*
        @GET("/token")
        public Observable<String> getToken();

        @GET("/user")
        public Observable<User> getUser(@Query("token") String token, @Query("userId") String userId);

        ...

        getToken()
                .flatMap(new Func1<String, Observable<User>>() {
                    @Override
                    public Observable<User> onNext(String token) {
                        return getUser(token, userId);
                    })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<User>() {
                        @Override
                        public void onNext(User user) {
                            userView.setUser(user);
                        }

                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable error) {
                            // Error handling
                            ...
                        }
                    });*/
    }


}