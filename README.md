# android-asynctask-demo

## 简介

本 demo 展示 Android 中 AsyncTask 的基本用法，通过一个模拟下载进度的示例演示如何在线程后台执行任务并更新 UI。

## 基本原理

AsyncTask 是 Android 提供的异步任务类，用于简化在后台线程执行任务并更新 UI 的过程。它封装了线程和 Handler，可以方便地在后台执行耗时操作，并在主线程更新进度和结果。

AsyncTask 有三个泛型参数：
- Params：doInBackground 的参数类型
- Progress：onProgressUpdate 的参数类型
- Result：onPostExecute 的结果类型

## 启动和使用

### 环境要求
- Android Studio 3.0+
- JDK 1.8+
- Android SDK 28

### 安装和运行
1. 用 Android Studio 打开此项目
2. 连接 Android 设备或启动模拟器
3. 点击 Run 运行项目

## 教程

### 什么是 AsyncTask？

AsyncTask 是 Android 早期提供的异步处理类，专门用于在后台执行耗时操作，并在操作过程中和完成后方便地更新 UI。它本质上是对 Thread 和 Handler 的封装。

### AsyncTask 的生命周期

AsyncTask 有四个核心方法：

1. **onPreExecute()**：在后台任务开始前调用，运行在主线程。可以用于显示加载提示。

2. **doInBackground(Params...)**：在后台线程执行，运行在子线程。这里执行耗时的操作，如网络请求、文件下载等。返回值会传给 onPostExecute。

3. **onProgressUpdate(Progress...)**：更新进度时调用，运行在主线程。当调用 publishProgress 时会触发此方法。

4. **onPostExecute(Result)**：后台任务完成后调用，运行在主线程。可以在此更新 UI。

### 基本用法

```kotlin
private inner class MyAsyncTask : AsyncTask<Int, Int, String>() {

    override fun onPreExecute() {
        textView.text = "开始下载..."
    }

    override fun doInBackground(vararg params: Int?): String {
        val total = params[0] ?: 10
        for (i in 1..total) {
            Thread.sleep(500)  // 模拟耗时操作
            publishProgress(i * 100 / total)  // 发布进度
        }
        return "下载完成!"
    }

    override fun onProgressUpdate(vararg values: Int?) {
        progressBar.progress = values[0] ?: 0
    }

    override fun onPostExecute(result: String?) {
        textView.text = result
    }
}

// 执行 AsyncTask
MyAsyncTask().execute(10)
```

### 发布进度

在 doInBackground 中调用 publishProgress 来更新进度：

```kotlin
publishProgress(progress)
```

### 注意事项

1. **必须在主线程创建**：AsyncTask 必须在主线程创建和执行
2. **不要手动调用回调方法**：不要手动调用 onPreExecute、onPostExecute 等
3. **内存泄漏**：如果 Activity 销毁时 AsyncTask 还在执行，可能导致内存泄漏。使用内部类时要用弱引用
4. **API 级别限制**：AsyncTask 在 Android API 30 (Android 11) 被标记为废弃，推荐使用 ExecutorService、RxJava 或 Kotlin Coroutines

### 生命周期问题

AsyncTask 与 Activity 生命周期不同步。当 Activity 销毁后，如果 AsyncTask 还在执行，可能导致：
- 空指针异常（尝试更新已销毁的 Activity 的 UI）
- 内存泄漏

解决方法：
1. 在 onDestroy 中取消 AsyncTask：`task.cancel(true)`
2. 在 doInBackground 中检查 isCancelled()
3. 使用 lifecycle-aware 组件
