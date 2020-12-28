mvvm

### viewmodel工厂

```java
/**
 * Factory for ViewModels
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource mDataSource;

    public ViewModelFactory(UserDataSource dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mDataSource);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
```

//2.业务vm

```
public class LogicViewModel extends AndroidViewModel {
       public LogicViewModel(@NonNull Application application, String topic) {
        super(application);//通过getApplictaion（）可以拿到
    }
}



//3.容器get, viewmodel被provider的viewstore管理（hashmap存储，类似单例）
ViewModelProviders.of(this, ViewModelFactory).get(ViewModel.class)；







```