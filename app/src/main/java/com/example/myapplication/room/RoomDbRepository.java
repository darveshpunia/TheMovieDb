package com.example.myapplication.room;


import java.util.List;
import com.example.myapplication.util.Utils;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class RoomDbRepository<T, D extends BaseDao<T>> {
  private final CompositeDisposable mDisposable = new CompositeDisposable();
  private final D dao;

  public RoomDbRepository(D dao){ this.dao = dao; }
  public D getDao(){ return dao; }

  public void insert(T item, Action action) {
    mDisposable.add(Completable
      .fromAction(() -> dao.insert(item))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(action, Utils::captureException));
  }

  public void insert(List<T> items) {
    Completable
      .fromAction(() -> dao.insert(items))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe();
  }

  public void update(T item) {
    Completable
      .fromAction(() -> dao.update(item))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe();
  }

  public void delete(int id, Action action) {
    mDisposable.add(Completable
      .fromAction(() -> dao.delete(id))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(action, Utils::captureException));
  }

  public void delete(List<T> ids, Action action) {
    mDisposable.add(Completable
      .fromAction(() -> dao.delete(ids))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(action, Utils::captureException));
  }

  public Flowable<List<T>> getAll()  {
    return dao
      .get()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  public Maybe<T> get(int id)  {
    return dao
      .get(id)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  public void delete(Action action) {
    mDisposable.add(Completable
      .fromAction(dao::delete)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(action, Utils::captureException));
  }

  public void clearSubscriptions(){
    mDisposable.clear();
  }
}
