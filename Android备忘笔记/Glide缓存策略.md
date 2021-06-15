## 缓存策略

### 1.Lruchche

LruCache 是 Android 的一个内部类，提供了基于内存实现的缓存

```java
//对一系列值持有强引用的一个固定size的LinkedHashMap，
//当空间满后自动删除队尾的键值对  
//线程安全
public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
			//最后一个参数指定 linkedmap遍历顺序是accessOrder/insertOrder 默认false
			//每次get操作会将该元素移到first
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
    }


//将map削减至size:将last元素移除
public final V put(@NonNull K key, @NonNull V value) {
    V previous;
    synchronized (this) {
        previous = map.put(key, value);
    }
    if (previous != null) {
        entryRemoved(false, key, previous, value);
    }
    trimToSize(maxSize);
    return previous;
}

```

### 2.Glide缓存图片

```java
//缓存ActiveResources（使用中缓存，保证不会被LRu回收）,Memorycache（Lru）,DiskCache
//如果有一张图片，url变化很快，则需要禁止缓存功能
Glide.with( context )
    .load( eatFoodyImages[0] )
    .skipMemoryCache( true )
 	.diskCacheStrategy( DiskCacheStrategy.NONE)
    .into( imageViewInternet );
//如果你有一张图片，你知道你将会经常操作处理，并做了一堆不同的版本，有意义的仅仅是缓存原始分辨率图片
Glide.with( context )
    .load( eatFoodyImages[2] )
    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
    .into( imageViewFile );
```





